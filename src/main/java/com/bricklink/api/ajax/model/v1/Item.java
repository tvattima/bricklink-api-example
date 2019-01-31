package com.bricklink.api.ajax.model.v1;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Item {
    private Integer idItem;
    private String typeItem;
    private String strItemNo;
    private String strItemName;
    private Integer idColor;
    private Integer idColorImg;
    private String cItemImgTypeS;
    private Boolean bHasLargeImg;
    private Integer n4NewQty;
    private Integer n4NewSellerCnt;
    private String mNewMinPrice;
    private String mNewMaxPrice;
    private Integer n4UsedQty;
    private Integer n4UsedSellerCnt;
    private String mUsedMinPrice;
    private String mUsedMaxPrice;
    private String strCategory;
    private String strPCC;
    private Integer idBrand;
}
