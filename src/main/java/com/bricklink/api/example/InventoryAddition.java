package com.bricklink.api.example;

import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.model.v1.BricklinkResource;
import com.bricklink.api.rest.model.v1.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dao.BricklinkInventoryDao;
import net.bricklink.data.lego.dao.InventoryIndexDao;
import net.bricklink.data.lego.dao.ItemDao;
import net.bricklink.data.lego.dto.BricklinkInventory;
import net.bricklink.data.lego.dto.BricklinkItem;
import net.bricklink.data.lego.dto.InventoryIndex;
import net.bricklink.data.lego.ibatis.mapper.BricklinkItemMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication(scanBasePackages = {"net.bricklink", "com.bricklink", "com.vattima"})
@EnableConfigurationProperties
@Slf4j
public class InventoryAddition {
    public static void main(String[] args) {
        SpringApplication.run(InventoryAddition.class, args);
    }

    @Component
    @RequiredArgsConstructor
    public static class LoadInventoryItems implements CommandLineRunner {
        private final InventoryIndexDao inventoryIndexDao;
        private final BricklinkInventoryDao bricklinkInventoryDao;
        private final BricklinkRestClient bricklinkRestClient;
        private final BricklinkItemMapper bricklinkItemMapper;
        private final ItemDao itemDao;
        Pattern itemIdPattern = Pattern.compile("\\(id=([0-9]+?)\\)");
        Pattern blItemIdPattern = Pattern.compile("\\(bl=([0-9]+?)\\)");
        Pattern blItemNoPattern = Pattern.compile("\\(no=([0-9]+?\\-[0-9])\\)");

        @Override
        public void run(String... args) throws Exception {
            log.info("LoadInventoryItems");
            List<InventoryIndex> inventoryIndexList = inventoryIndexDao.getAllForBox(39);
            inventoryIndexList.forEach(ii -> {
                log.info("{}", ii);
                String blItemNo = ii.getItemNumber() + "-1";

                // Get blItemNo from description if present
                String descripton = ii.getDescription();
                Matcher matcher = blItemNoPattern.matcher(descripton);
                if (matcher.find()) {
                    blItemNo = matcher.group(1);
                }
                log.info("\tUsing bl item number [{}]", blItemNo);

                BricklinkResource<Item> bricklinkItem = bricklinkRestClient.getCatalogItem("SET", blItemNo);
                log.info("\t{}", bricklinkItem);

                // Find in Item table
                List<net.bricklink.data.lego.dto.Item> itemList = itemDao.findItemByNumber(ii.getItemNumber());
                net.bricklink.data.lego.dto.Item item = null;
                if (itemList.size() == 0) {
                    log.warn("\tUnable to find item number [{}]", ii.getItemNumber());
                    item = new net.bricklink.data.lego.dto.Item();
                    item.setItemNumber(ii.getItemNumber());
                    item.setItemName(bricklinkItem.getData().getName());
                    item.setIssueYear(bricklinkItem.getData().getYear_released());
                    itemDao.insertItem(item);
                } else if (itemList.size() == 1) {
                    log.info("\tFound item number [{}]", ii.getItemNumber());
                    item = itemList.get(0);
                    item.setItemName(bricklinkItem.getData().getName());
                    item.setIssueYear(bricklinkItem.getData().getYear_released());
                    itemDao.updateItem(item);
                } else {
                    log.warn("\tItem number [{}] not unique - found [{}] items", ii.getItemNumber(), itemList.size());
                    descripton = ii.getDescription();
                    matcher = itemIdPattern.matcher(descripton);
                    if (matcher.find()) {
                        String id = matcher.group(1);
                        item = itemDao.findItemById(Integer.parseInt(id));
                        log.info("\tFound item number [{}] using id [{}]", ii.getItemNumber(), id);
                    }
                }
                log.info("\t\tBricklink Item Number [{}], Item Id [{}]", bricklinkItem.getData().getNo(), item.getItemId());

                // Find in BricklinkItem table
                Optional<BricklinkItem> bricklinkItemDtoOptional = bricklinkItemMapper.getBricklinkItemForBricklinkItemNumber(bricklinkItem.getData().getNo());
                if (bricklinkItemDtoOptional.isPresent()) {
                    BricklinkItem bricklinkItemDto = bricklinkItemDtoOptional.get();
                    log.info("\t\tBricklink item found [{}]", bricklinkItemDto);
                } else {
                    log.warn("\t\tBricklink item not found [{}]", bricklinkItem.getData().getNo());
                    BricklinkItem bricklinkItemDto = new BricklinkItem();
                    descripton = ii.getDescription();
                    matcher = blItemIdPattern.matcher(descripton);
                    if (matcher.find()) {
                        String blItemId = matcher.group(1);
                        log.info("\t\tUsing Bricklink Item Id  [{}]", blItemId);
                        bricklinkItemDto.setBlItemId(Integer.parseInt(blItemId));
                    } else {
                        log.error("Could not match pattern [{}]", blItemIdPattern);
                    }
                    bricklinkItemDto.setBlItemNumber(bricklinkItem.getData().getNo());
                    bricklinkItemDto.setItemId(item.getItemId());
                    itemDao.insertBricklinkItem(bricklinkItemDto);
                    log.info("\t\tInserted Bricklink Item [{}]", bricklinkItemDto);
                }

                // Insert into bricklink_inventory ONLY if it doesn't exist
                Optional<BricklinkInventory> bricklinkInventoryOptional = bricklinkInventoryDao.findByBoxIdAndBoxIndex(ii.getBoxId(), ii.getBoxIndex());
                if (bricklinkInventoryOptional.isPresent()) {
                    log.info("BricklinkInventory found - no insert needed [{}]", bricklinkInventoryOptional.get());
                } else {
                    BricklinkInventory bricklinkInventory = new BricklinkInventory();
                    bricklinkInventory.setBoxId(ii.getBoxId());
                    bricklinkInventory.setBoxIndex(ii.getBoxIndex());
                    bricklinkInventory.setBlItemNo(bricklinkItem.getData().getNo());
                    bricklinkInventory = bricklinkInventoryDao.insert(bricklinkInventory);
                    log.info("\t\t\tBricklinkInventory inserted [{}]", bricklinkInventory);
                }
            });
        }
    }
}
