package com.bricklink.api.ajax.support;

import com.bricklink.api.ajax.model.v1.AjaxResult;
import com.bricklink.api.ajax.model.v1.ItemForSale;
import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
public class CatalogItemsForSaleResult extends AjaxResult {
    private Integer total_count;
    private Integer idColor;
    private Integer rpp;
    private Integer pi;
    private List<ItemForSale> list;
}
