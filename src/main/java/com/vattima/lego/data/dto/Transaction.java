package com.vattima.lego.data.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Transaction {
    private int transactionId;
    private LocalDate transactionDate;
    private String notes;
    private Double shippingAmount;
    private Integer fromPartyId;
    private Integer toPartyId;
    private List<TransactionItem> transactionItems;
    private Party fromParty;
    private Party toParty;
}
