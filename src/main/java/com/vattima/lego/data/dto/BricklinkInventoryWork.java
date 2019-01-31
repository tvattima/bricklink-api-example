package com.vattima.lego.data.dto;

import lombok.*;

import java.time.Instant;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class BricklinkInventoryWork {
    private Integer blInventoryId;
    private Integer boxId;
    private Integer boxIndex;
    private Integer itemId;
    private String itemName;
    private String itemNumber;
    private Long blItemId;
    private String blItemNo;
    private Integer inventoryId;
    private Integer orderId;
    private String itemType;
    private Integer colorId;
    private String colorName;
    private Integer quantity;
    private String newOrUsed;
    private String completeness;
    private Double unitPrice;
    private Integer bindId;
    private String description;
    private String remarks;
    private Integer bulk;
    private Boolean isRetain;
    private Boolean isStockRoom;
    private String stockRoomId;
    private Instant dateCreated;
    private Double myCost;
    private Integer saleRate;
    private Integer tierQuantity1;
    private Integer tierQuantity2;
    private Integer tierQuantity3;
    private Double tierPrice1;
    private Double tierPrice2;
    private Double tierPrice3;
    private Double myWeight;
    private Boolean sealed;
    private Boolean builtOnce;
    private Integer boxConditionId;
    private Integer instructionsConditionId;
    private String internalComments;
    private Instant updateTimestamp;
    private Instant lastSynchronizedTimestamp;
}



