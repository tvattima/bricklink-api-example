package com.bricklink.api.example;

import com.bricklink.api.ajax.BricklinkAjaxClient;
import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.bricklink.api.ajax.support.CatalogItemsForSaleResult;
import com.bricklink.api.ajax.support.SearchProductResult;
import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.configuration.BricklinkRestProperties;
import com.bricklink.api.rest.model.v1.BricklinkResource;
import com.bricklink.api.rest.model.v1.Inventory;
import com.vattima.lego.sheet.configuration.LegoItemSheetProperties;
import com.vattima.lego.sheet.meta.BooleanCellDescriptor;
import com.vattima.lego.sheet.meta.CellDescriptor;
import com.vattima.lego.sheet.meta.IntegerCellDescriptor;
import com.vattima.lego.sheet.meta.StringCellDescriptor;
import com.vattima.lego.sheet.model.LegoSheetItem;
import com.vattima.lego.sheet.service.LegoItemSheetService;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;
import net.bricklink.data.lego.dao.ItemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static class BricklinkFeignTest implements CommandLineRunner {
        private final BricklinkRestClient bricklinkRestClient;
        private final BricklinkRestProperties bricklinkRestProperties;

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
                    .client(new OkHttpClient())
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
        }
    }
}
