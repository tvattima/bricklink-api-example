package com.bricklink.api.ajax.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemForSale {
    private Integer idInv;
    private String strDesc;
    private String codeNew;
    private String codeComplete;
    private String strInvImgUrl;
    private Integer idInvImg;
    private String typeInvImg;
    private Integer n4Qty;
    private Integer idColorDefault;
    private String typeImgDefault;
    private Integer hasExtendedDescription;
    private Boolean instantCheckout;
    @JsonProperty("mDisplaySalePrice")
    private String mDisplaySalePrice;
    @JsonProperty("mInvSalePrice")
    private String mInvSalePrice;
    private Integer nSalePct;
    private Integer nTier1Qty;
    private Integer nTier2Qty;
    private Integer nTier3Qty;
    private String nTier1DisplayPrice;
    private String nTier2DisplayPrice;
    private String nTier3DisplayPrice;
    private String nTier1InvPrice;
    private String nTier2InvPrice;
    private String nTier3InvPrice;
    private Integer idColor;
    private String strCategory;
    private String strStorename;
    private Integer idCurrencyStore;
    private String mMinBuy;
    private String strSellerUsername;
    private Integer n4SellerFeedbackScore;
    private String strSellerCountryName;
    private String strSellerCountryCode;
    private String strColor;
}
