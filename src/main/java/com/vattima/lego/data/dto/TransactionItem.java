package com.vattima.lego.data.dto;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class TransactionItem {
    private Integer itemTransactionId;
    private Integer itemId;
    private Integer transactionId;
    private String transactionTypeCode;
    private Double price;
    private String notes;
    private Integer boxConditionId;
    private Integer instructionsConditionId;
}
