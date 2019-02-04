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
import net.bricklink.data.lego.dto.BricklinkInventoryWork;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class WorkerConfiguration {
    @Bean
    CommandLineRunner inventoryCrawler(BricklinkInventoryDao bricklinkInventoryDao, BricklinkRestClient bricklinkRestClient, BricklinkAjaxClient bricklinkAjaxClient) {
        return args -> {
            bricklinkInventoryDao.getInventoryWork()
                                 .stream()
                                 .limit(5)
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
                                                                        .of("itemid", iwh.getBricklinkInventoryWork()
                                                                                         .getBlItemId())
                                                                        .of("cond", iwh.getNewUsed())
                                                                        .get());
                                                        iwh.setItemsForSale(catalogItemsForSaleResult.getList());
                                                    }
                                                    PriceGuide pg = bricklinkRestClient.getPriceGuide(iwh.getType(),
                                                            iwh.getBricklinkInventoryWork()
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
                        log.info("[#{} Stock/Sold:{} New/Used: {} min:{} avg:{} max:{}]",
                                pg.getItem().getNo(),
                                iwh.getGuideType(),
                                pg.getNew_or_used(),
                                pg.getMin_price(),
                                pg.getAvg_price(),
                                pg.getMax_price());
                        iwh.getItemsForSale().forEach(ifs -> log.info("\t\t{}", ifs));
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
        private final BricklinkInventoryWork bricklinkInventoryWork;
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
