package com.vattima.lego.inventory.configuration;

import com.bricklink.api.ajax.BricklinkAjaxClient;
import com.bricklink.api.ajax.model.v1.ItemForSale;
import com.bricklink.api.ajax.support.CatalogItemsForSaleResult;
import com.bricklink.api.rest.client.BricklinkRestClient;
import com.bricklink.api.rest.model.v1.PriceGuide;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
import java.util.stream.Stream;

@Configuration
@Slf4j
public class WorkerConfiguration {
    private static final Integer ONE = 1;

    @Bean
    CommandLineRunner inventoryCrawler(BricklinkInventoryDao bricklinkInventoryDao, BricklinkRestClient bricklinkRestClient, BricklinkAjaxClient bricklinkAjaxClient, BricklinkSaleItemDao bricklinkSaleItemDao) {
        return args -> {
            bricklinkInventoryDao.getInventoryWork()
                                 .stream()
                                 .map(biw -> Stream.of(
                                         new InventoryWorkHolder(biw.getItemType(), "stock", biw.getNewOrUsed(), biw),
                                         new InventoryWorkHolder(biw.getItemType(), "sold", biw.getNewOrUsed(), biw)
                                 ))
                                 .flatMap(s -> s.parallel()
                                                .peek(iwh -> {
                                                    if (iwh.getGuideType()
                                                           .equals("stock")) {
                                                        CatalogItemsForSaleResult catalogItemsForSaleResult = bricklinkAjaxClient.catalogItemsForSale(
                                                                new ParamsBuilder()
                                                                        .of("itemid", iwh.getBricklinkInventory()
                                                                                         .getBlItemId())
                                                                        .of("cond", iwh.getNewUsed())
                                                                        .get());
                                                        iwh.setItemsForSale(catalogItemsForSaleResult.getList());
                                                    }
                                                    PriceGuide pg = bricklinkRestClient.getPriceGuide(iwh.getType(),
                                                            iwh.getBricklinkInventory()
                                                               .getBlItemNo(),
                                                            new ParamsBuilder()
                                                                    .of("type", iwh.getType())
                                                                    .of("guide_type", iwh.getGuideType())
                                                                    .of("new_or_used", iwh.getNewUsed())
                                                                    .get())
                                                                                       .getData();
                                                    iwh.setPriceGuide(pg);
                                                })
                                 )
                                 .forEach(iwh -> {
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
                                     iwh.getItemsForSale()
                                        .forEach(ifs -> {
                                            log.info("\t\tBricklinkSaleItem(blSaleItemId={}, blItemId={}, inventoryId={}, quantity={}, newOrUsed={}, completeness={}, unitPrice={}, description={}, hasExtendedDescription={}, dateCreated={})",
                                                    "0", iwh.getBricklinkInventory()
                                                            .getBlItemId(), ifs.getIdInv(), ifs.getN4Qty(), ifs.getCodeNew(), ifs.getCodeComplete(), ifs.getSalePrice(), StringUtils.trim(ifs.getStrDesc()), ifs.getHasExtendedDescription(), Instant.now());
                                            BricklinkSaleItem bricklinkSaleItem = new BricklinkSaleItem();
                                            bricklinkSaleItem.setBlItemId(iwh.getBricklinkInventory().getBlItemId());
                                            bricklinkSaleItem.setInventoryId(ifs.getIdInv());
                                            bricklinkSaleItem.setCompleteness(ifs.getCodeComplete());
                                            bricklinkSaleItem.setDateCreated(Instant.now());
                                            bricklinkSaleItem.setDescription(StringUtils.trim(ifs.getStrDesc()));
                                            bricklinkSaleItem.setHasExtendedDescription(ONE.equals(ifs.getHasExtendedDescription()));
                                            bricklinkSaleItem.setNewOrUsed(ifs.getCodeNew());
                                            bricklinkSaleItem.setQuantity(ifs.getN4Qty());
                                            bricklinkSaleItem.setUnitPrice(ifs.getSalePrice());
                                            bricklinkSaleItemDao.insert(bricklinkSaleItem);
                                        });
                                 });
        };
    }

    @Setter
    @Getter
    @RequiredArgsConstructor
    private class InventoryWorkHolder {
        private final String type;
        private final String guideType;
        private final String newUsed;
        private final BricklinkInventory bricklinkInventory;
        private PriceGuide priceGuide = new PriceGuide();
        private List<ItemForSale> itemsForSale = new ArrayList<>();
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
