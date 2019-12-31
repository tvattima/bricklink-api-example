package com.bricklink.api.example;

import com.bricklink.api.ajax.BricklinkAjaxClient;
import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.bricklink.api.ajax.support.CatalogItemsForSaleResult;
import com.bricklink.api.ajax.support.SearchProductResult;
import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.client.ParamsBuilder;
import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import com.bricklink.api.rest.model.v1.*;
import com.bricklink.web.support.BricklinkWebService;
import com.vattima.lego.sheet.configuration.LegoItemSheetProperties;
import com.vattima.lego.sheet.meta.BooleanCellDescriptor;
import com.vattima.lego.sheet.meta.CellDescriptor;
import com.vattima.lego.sheet.meta.IntegerCellDescriptor;
import com.vattima.lego.sheet.meta.StringCellDescriptor;
import com.vattima.lego.sheet.model.LegoSheetItem;
import com.vattima.lego.sheet.service.LegoItemSheetService;
import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dao.BricklinkInventoryDao;
import net.bricklink.data.lego.dao.ItemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication(scanBasePackages = {"net.bricklink", "com.bricklink", "com.vattima"})
@EnableConfigurationProperties
public class BricklinkTestMain {
    private static Logger logger = LoggerFactory.getLogger(BricklinkTestMain.class);

    public static void main(String[] args) {
        SpringApplication.run(BricklinkTestMain.class, args);
    }

    //@Component
    public static class DatabaseSpreadheetJoiner implements CommandLineRunner {
        @Autowired
        LegoItemSheetProperties legoItemSheetProperties;

        @Autowired
        LegoItemSheetService legoItemSheetService;

        @Autowired
        private ItemDao itemDao;

        @Override
        public void run(String... args) throws Exception {
            List<CellDescriptor<?>> descriptors = Arrays.asList(
                    new IntegerCellDescriptor(),
                    new StringCellDescriptor(),
                    new StringCellDescriptor(),
                    new BooleanCellDescriptor(),
                    new IntegerCellDescriptor(),
                    new StringCellDescriptor());
            List<LegoSheetItem> legoSheetItems = legoItemSheetService.getLegoItems(descriptors);
        }
    }

    //@Component
    @RequiredArgsConstructor
    public static class InventoryQuantityFixer implements CommandLineRunner {
        private final BricklinkRestClient bricklinkRestClient;
        private final BricklinkRestProperties bricklinkRestProperties;
        private final BricklinkWebService bricklinkWebService;
        private final BricklinkInventoryDao bricklinkInventoryDao;

        @Override
        public void run(String... args) throws Exception {
            logger.info("Bricklink Quantity Fixer");
            bricklinkInventoryDao.getAllForSale()
                                 .stream()
//                                 .filter(bi -> bi.getInventoryId()
//                                                 .equals(174931488L))
                                 .forEach(bi -> {
                                     Optional.of(bi.getInventoryId())
                                             .ifPresent(ii -> {
                                                 int inventoryQuantity = bi.getQuantity();

                                                 BricklinkResource<Inventory> inventoryResponse = bricklinkRestClient.getInventories(ii);
                                                 Inventory inventory = inventoryResponse.getData();
                                                 int bricklinkQuantity = inventory.getQuantity();
                                                 int delta = inventoryQuantity - bricklinkQuantity;
                                                 if ((delta < 1) && (bricklinkQuantity != (bricklinkQuantity + delta))) {
                                                     inventory.setQuantity(delta);
                                                     inventory.setRemarks(bi.getUuid());
                                                     inventory.setDate_created(null);
                                                     bricklinkRestClient.updateInventory(ii, inventory);
                                                     logger.info("Updated [{} : {}] from quantity [{}] to new quantity [{}]", bi.getBlItemNo(), bi.getUuid(), bricklinkQuantity, (bricklinkQuantity + delta));
                                                 }
                                             });
                                 });
        }
    }


    @Component
    @RequiredArgsConstructor
    @Slf4j
    public static class BricklinkSoldItemInventoryFixer implements CommandLineRunner {
        private final BricklinkRestClient bricklinkRestClient;
        private final BricklinkRestProperties bricklinkRestProperties;
        private final BricklinkWebService bricklinkWebService;
        private final BricklinkInventoryDao bricklinkInventoryDao;

        @Override
        public void run(String... args) throws Exception {
            logger.info("Bricklink Sold Item Inventory Fixer");

            // Get all incoming Completed orders' items
            BricklinkResource<List<Order>> ordersResource = bricklinkRestClient.getOrders(new ParamsBuilder().of("direction", "in")
                                                                                                             .of("filed", "true")
                                                                                                             .get(), Arrays.asList("Completed"));
            List<Order> orders = ordersResource.getData();
            orders.stream()
                  .map(o -> bricklinkRestClient.getOrderItems(o.getOrder_id()))
                  .map(BricklinkResource::getData)
                  .map(Collection::stream)
                  .flatMap(s -> s.flatMap(List::stream))
                  .filter(oi -> oi.getItem()
                                  .getType()
                                  .equals("SET"))
                  .map(oi -> {
                      Inventory bi = null;
                      try {
                          bi = bricklinkRestClient.getInventories(oi.getInventory_id())
                                                  .getData();
                      } catch (Exception e) {
                          bi = new Inventory();
                          bi.setInventory_id(oi.getInventory_id());
                          bi.setIs_stock_room(true);
                      }
                      return bi;
                  })
                  .filter(bi -> !bi.getIs_stock_room())
                  .collect(Collectors.toList())
                  .forEach(bi -> {
                      log.info("THIS INVENTORY SHOULD BE SET TO STOCKROOM! --> {}", bi);
                  });
        }
    }

    //@Component
    @RequiredArgsConstructor
    public static class BricklinkFeignTest implements CommandLineRunner {
        private final BricklinkRestClient bricklinkRestClient;
        private final BricklinkRestProperties bricklinkRestProperties;
        private final BricklinkWebService bricklinkWebService;

        @Override
        public void run(String... strings) throws Exception {
            Map<String, Object> params = new HashMap<>();
            logger.info("Bricklink Feign Test!");

//            System.out.println(bricklinkRestClient.getCatalogItem("set", "6390-1"));
//            System.out.println(bricklinkRestClient.getCatalogItem("set", "6391-1"));
//            System.out.println(bricklinkRestClient.getCatalogItem("set", "6392-1"));
//            System.out.println(bricklinkRestClient.getCatalogItem("set", "6393-1"));
//            System.out.println(bricklinkRestClient.getCatalogItem("set", "3305-1"));
//
//            params.clear();
//            params.put("guide_type", "sold");
//            params.put("new_or_used", "U");
//            System.out.println(bricklinkRestClient.getPriceGuide("set", "6391-1", params));
//
//            params.clear();
//            params.put("guide_type", "stock");
//            params.put("new_or_used", "U");
//            System.out.println(bricklinkRestClient.getPriceGuide("set", "7740-1", params));
//
//            params.clear();
//            params.put("guide_type", "stock");
//            params.put("new_or_used", "N");
//            System.out.println(bricklinkRestClient.getPriceGuide("set", "7755-1", params));
//
//            params.clear();
//            params.put("guide_type", "stock");
//            params.put("new_or_used", "U");
//            System.out.println(bricklinkRestClient.getPriceGuide("set", "7755-1", params));
//
//            params.clear();
//            params.put("guide_type", "stock");
//            params.put("new_or_used", "N");
//            System.out.println(bricklinkRestClient.getPriceGuide("set", "30260-1", params));
//
//            params.clear();
//            System.out.println(bricklinkRestClient.getSubsets("set", "6391-1", params));
//
//            params.clear();
//            BricklinkResource<List<Inventory>> inventories = bricklinkRestClient.getInventories(params);
//            System.out.println("---------------------------------------------------------------------------------------------");
//            System.out.println("---------------------------------------------------------------------------------------------");
//            System.out.println(inventories);
//
//
//            for (Inventory inventory : inventories.getData()) {
//                System.out.println(bricklinkRestClient.getInventories(inventory.getInventory_id()));
//                System.out.println("---------------------------------------------------------------------------------------------");
//            }

            Inventory inventory = new Inventory();
// Create a new Inventory item
//            Item item = new Item();
//            item.setNo("30311-1");
//            item.setType("SET");
//            inventory.setItem(item);
//            inventory.setQuantity(2);
//            inventory.setCompleteness("C");
//            inventory.setDescription("This was uploaded with the API");
//            inventory.setMy_cost(1.23);
//            inventory.setNew_or_used("N");
//            inventory.setIs_retain(false);
//            inventory.setIs_stock_room(true);
//            inventory.setRemarks("These are my internal remarks");
//            inventory.setStock_room_id("C");
//            inventory.setUnit_price(2.34);
//            inventory.setColor_id(0);
//            BricklinkResource<Inventory> newInventory = bricklinkRestClient.createInventory(inventory);
//            System.out.println("---------------------------------------------------------------------------------------------");
//            System.out.println("New Inventory ["+newInventory+"]");
//            System.out.println("---------------------------------------------------------------------------------------------");
//
//            Inventory updateInventory = newInventory.getData();

//            BricklinkResource<Inventory> inventory2 = bricklinkRestClient.getInventories(165302802L);
//            if (inventory2.getMeta().getCode().equals(404)) {
//                logger.warn("Not found [{}]", 165302802L);
//            }
//            System.out.println("---------------------------------------------------------------------------------------------");
//            System.out.println("Inventory ["+inventory2+"]");
//            System.out.println("---------------------------------------------------------------------------------------------");

//            // Update Inventory example
//            inventory = new Inventory();
//            inventory.setInventory_id(162625963L);
//            inventory.setRemarks("These are my updated remarks!");
//            inventory.setUnit_price(3.45);
//
//            BricklinkResource<Inventory> updatedInventory = null;
//            updatedInventory = bricklinkRestClient.updateInventory(162625963L, inventory);
//            inventory.setStock_room_id("A");
//            inventory.setIs_stock_room(false);
//            updatedInventory = bricklinkRestClient.updateInventory(162625963L, inventory);
//            System.out.println("---------------------------------------------------------------------------------------------");
//            System.out.println("Updated Inventory ["+updatedInventory+"]");
//            System.out.println("---------------------------------------------------------------------------------------------");

            BricklinkAjaxClient bricklinkAjaxClient = Feign
                    .builder()
                    .client(new ApacheHttpClient(bricklinkWebService.getHttpClient()))
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder())
                    .logger(new Slf4jLogger(BricklinkAjaxClient.class))
                    .logLevel(feign.Logger.Level.FULL)
                    .target(BricklinkAjaxClient.class, "https://www.bricklink.com");

            params.clear();
            params.put("q", "1252");
            SearchProductResult searchProductResult = bricklinkAjaxClient.searchProduct(params);
            System.out.println(searchProductResult);

            params.clear();
            params.put("itemid", "4042");
            CatalogItemsForSaleResult catalogItemsForSaleResult = bricklinkAjaxClient.catalogItemsForSale(params);
            System.out.println(catalogItemsForSaleResult);
            for (ItemForSale itemForSale : catalogItemsForSaleResult.getList()) {
                logger.info("{}", itemForSale);
            }

            BricklinkResource<List<Color>> colors = bricklinkRestClient.getColors();
            logger.info("Metadata [{}]", colors.getMeta());
            colors.getData().forEach(c -> {
                if (c.getColor_name().equalsIgnoreCase("reddish brown")) {
                    logger.info("-----------------------------------------------------------");
                }
                logger.info("Colors [{}]", c);
            });

            BricklinkResource<Color> color = bricklinkRestClient.getColor(12);
            logger.info("Metadata [{}]", color.getMeta());
            logger.info("Single Color [{}]", color.getData());

            BricklinkResource<com.bricklink.api.rest.model.v1.Item> item = bricklinkRestClient.getCatalogItem("PART", "59230");
            logger.info("Item [{}]", item);

            BricklinkResource<List<Category>> categories = bricklinkRestClient.getCategories();
            logger.info("Metadata [{}]", categories.getMeta());
            categories.getData().forEach(c -> {
                logger.info("Category [{}]", c);
            });

            BricklinkResource<List<ItemMapping>> itemMapping = bricklinkRestClient.getItemMapping("3666", 7);
            logger.info("Metadata [{}]", itemMapping.getMeta());
            logger.info("ItemMapping [{}]", itemMapping.getData());

            params.clear();
            params.put("direction", "in");
            params.put("filed", "false");
            BricklinkResource<List<Order>> currentOrders = bricklinkRestClient.getOrders(params);
            params.clear();
            params.put("direction", "in");
            params.put("filed", "true");
            BricklinkResource<List<Order>> filedOrders = bricklinkRestClient.getOrders(params);
            Stream.concat(currentOrders.getData()
                                       .stream(), filedOrders.getData()
                                                             .stream())
                  .filter(o -> o.getDate_ordered()
                                .getYear() == 2019)
                  .forEach(o -> {
                      logger.info("Order [{}]", o);
                  });

            String bogusOrderId = "10000008";
            try {
                BricklinkResource<Order> bricklinkOrder = bricklinkRestClient.getOrder(bogusOrderId);
                Order order = Optional.ofNullable(bricklinkOrder.getData()).orElseThrow(() -> new Exception(String.format("Order Id [%1$s] was not found", bogusOrderId)));
                logger.info("Order Id [{}] = [{}]", bogusOrderId, order);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            String orderId = "11648048";
            BricklinkResource<Order> order = bricklinkRestClient.getOrder(orderId);
            logger.info("Order Id [{}] = [{}]", orderId, order.getData());

            BricklinkResource<List<List<OrderItem>>> orderItemBatches = bricklinkRestClient.getOrderItems(orderId);
            AtomicInteger i = new AtomicInteger();
            orderItemBatches.getData().forEach(oib -> {
                logger.info("\tBatch [{}] -------------------------------------------------------------------------------------------------", i.incrementAndGet());
                oib.forEach(oi -> {
                    logger.info("\t\tOrderItem [{}]", oi);
                });
            });
        }
    }
}
