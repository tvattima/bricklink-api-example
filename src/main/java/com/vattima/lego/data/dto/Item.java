package com.vattima.lego.data.dto;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Item {
    private int itemId;
    private String itemNumber;
    private String itemName;
    private Integer numberOfPieces;
    private Integer issueYear;
    private String issueLocation;
    private Integer themeId;
    private String itemTypeCode;
    private String notes;
    private BricklinkItem brinkLinkitem;
}
