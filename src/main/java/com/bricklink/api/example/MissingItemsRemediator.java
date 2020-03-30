package com.bricklink.api.example;

import com.bricklink.api.ajax.BricklinkAjaxClient;
import com.bricklink.api.ajax.model.v1.Item;
import com.bricklink.api.ajax.model.v1.Type;
import com.bricklink.api.ajax.support.SearchProductResult;
import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.model.v1.BricklinkResource;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dao.InventoryIndexDao;
import net.bricklink.data.lego.dao.ItemDao;
import net.bricklink.data.lego.dao.TransactionDao;
import net.bricklink.data.lego.dto.BricklinkItem;
import net.bricklink.data.lego.dto.InventoryIndex;
import net.bricklink.data.lego.dto.Transaction;
import net.bricklink.data.lego.dto.TransactionItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication(scanBasePackages = {"com"})
@EnableConfigurationProperties
@Slf4j
public class MissingItemsRemediator {

    public static void main(String[] args) {
        SpringApplication.run(com.bricklink.api.example.BricklinkTestMain.class, args);
    }

    //@Component
    @RequiredArgsConstructor
    public static class Runner implements CommandLineRunner {
        @Value("${bricklink.consumer.key}")
        private String consumerKey;

        @Value("${bricklink.consumer.secret}")
        private String consumerSecret;

        @Value("${bricklink.token.value}")
        private String tokenValue;

        @Value("${bricklink.token.secret}")
        private String tokenSecret;

        private final InventoryIndexDao inventoryIndexDao;
        private final ItemDao itemDao;
        private final TransactionDao transactionDao;
        private final BricklinkRestClient bricklinkRestClient;

        @Override
        public void run(String... args) throws Exception {
            Map<Integer, List<ItemInventoryIndexHolder>> itemsByYearMap = new HashMap<>();
            Map<String, Object> params = new HashMap<>();
            BricklinkAjaxClient bricklinkAjaxClient = Feign
                    .builder()
                    .client(new OkHttpClient())
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder())
                    .logger(new Slf4jLogger(BricklinkAjaxClient.class))
                    .logLevel(feign.Logger.Level.FULL)
                    .target(BricklinkAjaxClient.class, "https://www.bricklink.com");

            int counter = 0;
            log.info("Starting MissingItemsRemediator");
            // Get all InventoryIndex that do not have an Item
            List<InventoryIndex> inventoryIndexList = inventoryIndexDao.getAllWithNoItem();
            // For each InventoryIndex
            for (InventoryIndex inventoryIndex : inventoryIndexList) {
                log.info("Inventory index [{}]", inventoryIndex);
                //    Use secret search API to find item number in Bricklink (if not distinct, skip it - need to go to stock and determine each non-unique item)
                params.clear();
                params.put("q", inventoryIndex.getItemNumber());
                params.put("type", "S");
                SearchProductResult searchProductResult = bricklinkAjaxClient.searchProduct(params);
                List<Type> bricklinkSetsSearch = searchProductResult.getResult().getTypeList();
                if (bricklinkSetsSearch.size() == 1) {
                    Type setsResult = bricklinkSetsSearch.get(0);
                    List<Item> sets = setsResult.getItems();
                    if (sets.size() == 1) {
                        Item set = sets.get(0);
                        log.info("\t\tBricklink Set [{}]", set);
                        boolean shouldInsertItem = false;

                        //    Attempt first to find the item; if not found, upsert it.
                        String searchItemNumber = getSearchItemNumber(inventoryIndex.getItemNumber());
                        List<net.bricklink.data.lego.dto.Item> itemList = itemDao.findItemByNumber(searchItemNumber);
                        net.bricklink.data.lego.dto.Item item = null;
                        if (itemList.isEmpty()) {
                            shouldInsertItem = true;
                            log.info("\t\t\tItem Number not found in DB [{}]", inventoryIndex.getItemNumber());
                            //    Construct Item from Bricklink data
                            item = new net.bricklink.data.lego.dto.Item();
                            item.setItemNumber(searchItemNumber);
                            item.setItemName(set.getStrItemName());
                            item.setItemTypeCode("S");
                        } else if (itemList.size() == 1) {
                            item = itemList.get(0);
                            log.info("\t\t\tItem found in DB [{}]", item);
                        } else {
                            throw new RuntimeException(String.format("Item number [%s] was not unique - found [%s]", searchItemNumber, itemList.size()));
                        }
                        log.info("\t\t\tSet [{}]", item);

                        // Get Bricklink Item
                        BricklinkResource<com.bricklink.api.rest.model.v1.Item> bricklinkResource = bricklinkRestClient.getCatalogItem("SET", set.getStrItemNo());
                        item.setIssueYear(bricklinkResource.getData().getYear_released());

                        //    Add Item to List in Hashmap whose key is year-released
                        ItemInventoryIndexHolder itemInventoryIndexHolder = new ItemInventoryIndexHolder(item, inventoryIndex);
                        addToItemsByYearMap(bricklinkResource.getData().getYear_released(), itemInventoryIndexHolder, itemsByYearMap);

                        //    Insert new Item object
                        if (shouldInsertItem) {
                            itemDao.insertItem(item);
                        }

                        //    Construct BricklinkItem
                        BricklinkItem bricklinkItem = new BricklinkItem();
                        bricklinkItem.setItemId(item.getItemId());
                        bricklinkItem.setBlItemNumber(set.getStrItemNo());
                        bricklinkItem.setBlItemId(set.getIdItem());

                        //    Insert new BricklinkItem object
                        itemDao.insertBricklinkItem(bricklinkItem);

                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (Item set : sets) {
                            sb.append("\t\t").append(set.getStrItemNo()).append(" - ").append(set.getStrItemName()).append(";\t");
                        }
                        log.error(sb.toString());
                        log.error("\t\tFound [{}] sets - skipping", sets.size());
                        StringBuilder description = new StringBuilder();
                        if (StringUtils.isNotEmpty(inventoryIndex.getDescription())) {
                            description.append("[").append(inventoryIndex.getDescription()).append("] ");
                        }
                        sb.setLength(0);
                        sb.append("Is this Bricklink ");
                        int setCounter = 0;
                        int count = sets.size();
                        for (Item set : sets) {
                            setCounter++;
                            sb.append("[").append(set.getStrItemNo()).append(" - ").append(set.getStrItemName()).append("]");
                            if (setCounter < count) {
                                sb.append(" -OR- ");
                            }
                        }
                        description.append(sb.toString());
                        inventoryIndex.setDescription(description.toString());
                        inventoryIndexDao.udpateInventoryIndex(inventoryIndex);
                    }
                } else {
                    log.error("\tBricklink Secret search was not 1");
                }
            }

            log.info("");
            log.info("Creating Transactions -----------------------------------------------------------------------");
            log.info("");
            // For each key (year) in the keyset of the year-released Hashmap
            for (Integer year : itemsByYearMap.keySet()) {
                LocalDate firstOfYear = LocalDate.of(year, 1, 1);
                log.info("year [{}]", year);
                //    Construct a Transaction object
                Transaction transaction = new Transaction();
                transaction.setTransactionDate(firstOfYear);
                transaction.setNotes("Auto generated from 2018 inventory");
                transaction.setToPartyId(0);

                //    Attempt to find transaction
                List<Transaction> transactions = transactionDao.findByAutoGeneratedAndTransactionDate(firstOfYear);
                if (transactions.size() == 1) {
                    transaction = transactions.get(0);
                } else if (transactions.size() == 0) {
                    //    Insert the Transaction object
                    transactionDao.insertTransaction(transaction);
                } else {
                    log.info("\tCould not find unique Transaction for first of year [{}] - skipping", firstOfYear);
                    continue;
                }
                log.info("\tTransaction [{}]", transaction);

                //    For each Item in the List of that key
                for (ItemInventoryIndexHolder itemInventoryIndexHolder: itemsByYearMap.get(year)) {
                    net.bricklink.data.lego.dto.Item item = itemInventoryIndexHolder.getItem();
                    InventoryIndex inventoryIndex = itemInventoryIndexHolder.getInventoryIndex();
                    log.info("\t\t ---> Item [{}]", item);
                    // Based on the quantity in the inventory index and the sealed indicator, build the TransactionItems
                    int transactionItemsToBuild = Math.max(inventoryIndex.getQuantity(), 1);
                    for (int i = 0; i < transactionItemsToBuild; i++) {
                        //       Construct a TransactionItem
                        TransactionItem transactionItem = new TransactionItem();
                        transactionItem.setTransactionId(transaction.getTransactionId());
                        transactionItem.setItemId(item.getItemId());
                        transactionItem.setTransactionTypeCode("P");
                        if (Boolean.valueOf(inventoryIndex.getSealed())) {
                            transactionItem.setBoxConditionId(11);
                        }
                        //       Insert the TransactionItem object
                        transactionDao.insertTransactionItem(transactionItem);
                        log.info("\t\tTransactionItem [{}]", transactionItem);
                    }
                }
            }
        }

        private String getSearchItemNumber(String itemNumber) {
            String searchItemNumber = itemNumber;
            int hyphenIndex = itemNumber.indexOf('-');
            if (hyphenIndex > 0) {
                searchItemNumber = itemNumber.substring(0, hyphenIndex);
            }
            return searchItemNumber;
        }

        private void addToItemsByYearMap(Integer yearReleased, ItemInventoryIndexHolder itemInventoryIndexHolder, Map<Integer, List<ItemInventoryIndexHolder>> itemsByYearMap) {
            if (itemsByYearMap.containsKey(yearReleased)) {
                itemsByYearMap.get(yearReleased).add(itemInventoryIndexHolder);
            } else {
                List<ItemInventoryIndexHolder> itemInventoryIndexHolderList = new ArrayList<>();
                itemInventoryIndexHolderList.add(itemInventoryIndexHolder);
                itemsByYearMap.put(yearReleased, itemInventoryIndexHolderList);
            }
        }

        @RequiredArgsConstructor
        @Getter
        @Setter
        @EqualsAndHashCode
        private class ItemInventoryIndexHolder {
            private final net.bricklink.data.lego.dto.Item item;
            private final InventoryIndex inventoryIndex;
        }
    }
}
