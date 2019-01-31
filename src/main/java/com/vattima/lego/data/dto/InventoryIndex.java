package com.vattima.lego.data.dto;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class InventoryIndex {
    private Integer boxIndex;
    private String boxNumber;
    private String itemNumber;
    private String sealed;
    private Integer quantity;
    private String description;
}
