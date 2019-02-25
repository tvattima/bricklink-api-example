package com.vattima.lego.inventory.configuration;

import com.bricklink.api.ajax.BricklinkAjaxClient;
import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.bricklink.api.ajax.support.CatalogItemsForSaleResult;
import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.model.v1.PriceGuide;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.bricklink.data.lego.dao.BricklinkInventoryDao;
import net.bricklink.data.lego.dao.BricklinkSaleItemDao;
import net.bricklink.data.lego.dto.BricklinkInventory;
import net.bricklink.data.lego.dto.BricklinkSaleItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class WorkerConfiguration {
    private static final Integer ONE = 1;

    @Bean
    CommandLineRunner inventoryCrawler(BricklinkInventoryDao bricklinkInventoryDao, BricklinkRestClient bricklinkRestClient, BricklinkAjaxClient bricklinkAjaxClient, BricklinkSaleItemDao bricklinkSaleItemDao) {
        return args -> {
            List<InventoryWorkHolder> inventoryWorkHolderList = bricklinkInventoryDao.getInventoryWork()
                                 .stream()
                                 .filter(BricklinkInventory::shouldSynchronize)
                                 .filter(bi -> bi.getBlItemNo().equals("4559-1"))
                                 .map(iwh -> Stream.of(
                                         new InventoryWorkHolder(iwh.getItemType(), "stock", iwh.getNewOrUsed(), iwh),
                                         new InventoryWorkHolder(iwh.getItemType(), "sold", iwh.getNewOrUsed(), iwh)
                                 ))
                                 .flatMap(s -> s.parallel()
                                                .peek(iwh -> {
                                                    if (iwh.getGuideType().equals("stock")) {
                                                        CatalogItemsForSaleResult catalogItemsForSaleResult = bricklinkAjaxClient.catalogItemsForSale(
                                                                new ParamsBuilder()
                                                                        .of("itemid", iwh
                                                                                .getBricklinkInventory()
                                                                                .getBlItemId())
                                                                        .of("cond", iwh.getNewUsed())
                                                                        .get());
                                                        iwh.setItemsForSale(catalogItemsForSaleResult.getList());
                                                        iwh.getItemsForSale()
                                                           .forEach(ifs -> {
                                                               BricklinkSaleItem bricklinkSaleItem = iwh.buildBricklinkSaleItem(ifs);
                                                               bricklinkSaleItemDao.upsert(bricklinkSaleItem);
                                                               log.info("{}", bricklinkSaleItem);
                                                           });
                                                    }
                                                    PriceGuide pg = bricklinkRestClient.getPriceGuide(iwh.getType(),
                                                            iwh.getBricklinkInventory()
                                                               .getBlItemNo(),
                                                            new ParamsBuilder()
                                                                    .of("type", iwh.getType())
                                                                    .of("guide_type", iwh.getGuideType())
                                                                    .of("new_or_used", iwh.getNewUsed())
                                                                    .get()).getData();
                                                    iwh.setPriceGuide(pg);
                                                })
                                 )
                                 .peek(iwh -> {
                                     PriceGuide pg = iwh.getPriceGuide();
                                     log.info("[{}::#{} Stock/Sold:{} New/Used: {} min:{} avg:{} max:{}]",
                                             iwh.getBricklinkInventory()
                                                .getBlItemId(),
                                             pg.getItem()
                                               .getNo(),
                                             iwh.getGuideType(),
                                             pg.getNew_or_used(),
                                             pg.getMin_price(),
                                             pg.getAvg_price(),
                                             pg.getMax_price());
                                 }).collect(Collectors.toList());
            inventoryWorkHolderList.parallelStream().peek(iwh -> log.info("{}", iwh)).collect(Collectors.toList());
        };
    }

    @Setter
    @Getter
    @RequiredArgsConstructor
    @ToString
    private class InventoryWorkHolder {
        private final String type;
        private final String guideType;
        private final String newUsed;
        private final BricklinkInventory bricklinkInventory;
        private PriceGuide priceGuide = new PriceGuide();
        private List<ItemForSale> itemsForSale = new ArrayList<>();
        
        BricklinkSaleItem buildBricklinkSaleItem(ItemForSale itemForSale) {
            BricklinkSaleItem bricklinkSaleItem = new BricklinkSaleItem();
            bricklinkSaleItem.setBlItemId(getBricklinkInventory().getBlItemId());
            bricklinkSaleItem.setInventoryId(itemForSale.getIdInv());
            bricklinkSaleItem.setCompleteness(itemForSale.getCodeComplete());
            bricklinkSaleItem.setDateCreated(Instant.now());
            bricklinkSaleItem.setDescription(StringUtils.trim(itemForSale.getStrDesc()));
            bricklinkSaleItem.setHasExtendedDescription(ONE.equals(itemForSale.getHasExtendedDescription()));
            bricklinkSaleItem.setNewOrUsed(itemForSale.getCodeNew());
            bricklinkSaleItem.setQuantity(itemForSale.getN4Qty());
            bricklinkSaleItem.setUnitPrice(itemForSale.getSalePrice());            
            return bricklinkSaleItem;
        }
    }

    private static class ParamsBuilder {
        private Map<String, Object> params = new HashMap<>();

        public ParamsBuilder of(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public Map<String, Object> get() {
            return params;
        }
    }
}
