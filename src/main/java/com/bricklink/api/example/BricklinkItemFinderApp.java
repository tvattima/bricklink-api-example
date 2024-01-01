package com.bricklink.api.example;

import com.bricklink.api.ajax.BricklinkAjaxClient;
import com.bricklink.api.ajax.support.CatalogItemsForSaleResult;
import com.bricklink.api.html.BricklinkHtmlClient;
import com.bricklink.api.html.model.v2.CatalogItem;
import com.bricklink.api.rest.client.ParamsBuilder;
import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dao.InventoryIndexDao;
import net.bricklink.data.lego.dao.ItemDao;
import net.bricklink.data.lego.dto.BricklinkItem;
import net.bricklink.data.lego.dto.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = {"com"})
@EnableConfigurationProperties
@Slf4j
public class BricklinkItemFinderApp {

    NumberFormat numberFormat = NumberFormat.getPercentInstance();

    public static void main(String[] args) {
        log.info("hi");
        SpringApplication.run(BricklinkItemFinderApp.class, args);
    }

    @Component
    public class BricklinkItemFinder2 implements CommandLineRunner {
        @Autowired
        private BricklinkHtmlClient bricklinkHtmlClient;

        @Override
        public void run(String... args) throws Exception {
            CatalogItem catalogItem = bricklinkHtmlClient.getCatalogSetItemId("6339-1");
            log.info("Found CatalogItem [{}]", catalogItem);
        }
    }

    //@Component
    public class BricklinkItemFinderForInventoryIndex implements CommandLineRunner {

        @Autowired
        private ItemDao itemDao;

        @Autowired
        private InventoryIndexDao inventoryIndexDao;

        @Autowired
        private BricklinkHtmlClient bricklinkHtmlClient;

        @Override
        public void run(String... args) throws Exception {
            log.info("Starting BricklinkItemFinderForInventoryIndex");
            AtomicInteger count = new AtomicInteger(0);
            List<Item> items = inventoryIndexDao
                    .getAllWithNoItem()
                    .parallelStream()
                    .peek(ii -> {
                        count.incrementAndGet();
                    })
                    .map(ii -> {
                        Item item = new Item();
                        item.setItemNumber(ii.getItemNumber());
                        item.setItemTypeCode("S");
                        return item;
                    })
                    .map(i -> upsertBricklinkItem(bricklinkHtmlClient, i))
                    .collect(Collectors.toList());
            System.out.println("count [" + count.get() + "]");
            for (Item item : items) {
                System.out.println(item);
            }
            System.out.println();
        }
    }

    //@Component
    public class BricklinkItemFinder implements CommandLineRunner {

        @Autowired
        private ItemDao itemDao;

        @Autowired
        private InventoryIndexDao inventoryIndexDao;

        @Autowired
        private BricklinkHtmlClient bricklinkHtmlClient;

        @Override
        public void run(String... args) throws Exception {
            List<String> itemTypeCodes = Arrays.asList("S", "K", "I");
            List<Item> items = itemDao.getAllWithNoBricklinkItem()
                                      .parallelStream()
                                      .filter(i -> itemTypeCodes.contains(i.getItemTypeCode()))
                                      .map(i -> upsertBricklinkItem(bricklinkHtmlClient, i))
                                      .collect(Collectors.toList());
        }
    }

    @Slf4j
    //@Component
    public static class BricklinkPartFinder implements CommandLineRunner {

        @Autowired
        private BricklinkHtmlClient bricklinkHtmlClient;

        @Autowired
        private BricklinkAjaxClient bricklinkAjaxClient;

        @Override
        public void run(String... args) throws Exception {
            CatalogItem catalogItem = bricklinkHtmlClient.getCatalogPartItemId("99780");
            log.info("CatalogItem {}",catalogItem);

            CatalogItemsForSaleResult catalogNewItemsForSaleResult = bricklinkAjaxClient.catalogItemsForSale(
                    new ParamsBuilder()
                            .of("itemid", catalogItem.getItemId())
                            .of("rpp", 50)
                            .of("loc", "US")
                            .of("minqty", "6")
                            .of("cond", "N")
                            .of("st", "1")
                            .of("color", "11")
                            .get());
            log.info("-------------------------------------------------------------------------------");
            catalogNewItemsForSaleResult.getList().forEach(fsr -> {
                log.info("{}", fsr);
            });
            log.info("-------------------------------------------------------------------------------");
        }
    }

    @Transactional
    public Item upsertBricklinkItem(BricklinkHtmlClient bricklinkHtmlClient, Item item) {
        List<CatalogItem> catalogItems = getBricklinkItems(bricklinkHtmlClient, item);
        for (CatalogItem catalogItem : catalogItems) {
            double matchPercentage = catalogItem.itemNameMatch(item.getItemName());
            //if ((catalogItems.size() == 1) && (matchPercentage >= 0.5d && matchPercentage < 1.0d)) {
            if (matchPercentage == 1.0d) {
                BricklinkItem bricklinkItem = new BricklinkItem();
                bricklinkItem.setItemId(item.getItemId());
                bricklinkItem.setBlItemNumber(catalogItem.getItemNo());
                bricklinkItem.setBlItemId(catalogItem.getItemId());
                log.info("inserting bricklink_item({}, {}, {}) match % [{}]{} catalog items found [{}]", catalogItem.getItemId(), catalogItem.getItemNo(), item.getItemId(), numberFormat.format(matchPercentage), (matchPercentage < 1) ? "**" : "", catalogItems.size());
                //itemDao.insertBricklinkItem(bricklinkItem);
            } else {
                log.info("(A) itemId [{}], itemNumber [{}], itemName [{}], Bricklink Item Id [{}], Bricklink Item No [{}] Bricklink Item Name [{}], match % [{}]{}", item.getItemId(), item.getItemNumber(), item.getItemName(), catalogItem.getItemId(), catalogItem.getItemNo(), catalogItem.getItemName(), numberFormat.format(matchPercentage), (matchPercentage < 1) ? "**" : "");
            }
        }
        return item;
    }

    private List<CatalogItem> getBricklinkItems(BricklinkHtmlClient bricklinkHtmlClient, Item item) {
        List<CatalogItem> catalogItems = new ArrayList<>();

        try {
            // Get internal BrickLink itemId for a given set number:
            //driver.get("https://www.bricklink.com/v2/catalog/catalogitem.page?S=6390-1");
            int index = 1;
            boolean found = true;
            while (found) {
                Optional<CatalogItem> optionalCatalogItem = bricklinkCatalogItemSearchQuery(bricklinkHtmlClient, item, index);
                if (optionalCatalogItem.isPresent()) {
                    CatalogItem catalogItem = optionalCatalogItem.get();
                    if (catalogItem == CatalogItem.EMPTY) {
                        found = false;
                    } else {
                        found = true;
                        catalogItems.add(catalogItem);
                        double matchPercentage = catalogItem.itemNameMatch(item.getItemName());
                        log.info("(B)------- itemId [{}], itemNumber [{}], itemName [{}], Bricklink Item Id [{}], Bricklink Item No [{}] Bricklink Item Name [{}], match % [{}]{}", item.getItemId(), item.getItemNumber(), item.getItemName(), catalogItem.getItemId(), catalogItem.getItemNo(), catalogItem.getItemName(), numberFormat.format(matchPercentage), (matchPercentage < 1) ? "**" : "");
                        if (!item.getItemTypeCode()
                                 .equals("S")) {
                            break;
                        }
                    }
                    index++;
                } else {
                    found = false;
                    log.warn("itemId [{}], itemNumber [{}], itemName [{}] non-searchable item type [{}]", item.getItemId(), item.getItemNumber(), item.getItemName(), item.getItemTypeCode());
                }
            }
        } catch (NumberFormatException e) {
            log.warn("################################[{}]################################", e.getMessage());
            log.warn("itemId [{}], itemNumber [{}], itemName [{}]", item.getItemId(), item.getItemNumber(), item.getItemName());
        }
        return catalogItems;
    }

    public Optional<CatalogItem> bricklinkCatalogItemSearchQuery(BricklinkHtmlClient bricklinkHtmlClient, Item item, int index) {
        Optional<CatalogItem> catalogItem = Optional.empty();
        String bricklinkItemType = bricklinkItemType(item);
        if (null != bricklinkItemType) {
            if ("S".equals(bricklinkItemType)) {
                catalogItem = Optional.of(bricklinkHtmlClient.getCatalogSetItemId(item.getItemNumber().matches("[0-9]+?-[0-9]+?")?item.getItemNumber():item.getItemNumber() + "-" + index));
            } else if ("B".equals(bricklinkItemType)) {
                catalogItem = Optional.of(bricklinkHtmlClient.getCatalogBookItemId(item.getItemNumber()));
            } else if ("P".equals(bricklinkItemType)) {
                catalogItem = Optional.of(bricklinkHtmlClient.getCatalogPartItemId(item.getItemNumber()));
            } else if ("G".equals(bricklinkItemType)) {
                catalogItem = Optional.of(bricklinkHtmlClient.getCatalogGearItemId(item.getItemNumber()));
            }
        }
        return catalogItem;
    }

    public String bricklinkItemType(Item item) {
        String bricklinkItemType = null;
        if ("S".equals(item.getItemTypeCode())) {
            bricklinkItemType = "S";
        } else if ("I".equals(item.getItemTypeCode())) {
            bricklinkItemType = "B";
        } else if ("K".equals(item.getItemTypeCode())) {
            bricklinkItemType = "G";
        }
        return bricklinkItemType;
    }
}
