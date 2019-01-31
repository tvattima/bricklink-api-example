package com.vattima.lego.data.dto;

import lombok.*;

import java.time.Instant;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class BricklinkSaleItem {
    private Integer blSaleItemId;
    private Long blItemId;
    private Integer inventoryId;
    private Integer quantity;
    private String newOrUsed;
    private String completeness;
    private Double unitPrice;
    private String description;
    private Boolean hasExtendedDescription;
    private Instant dateCreated;
}