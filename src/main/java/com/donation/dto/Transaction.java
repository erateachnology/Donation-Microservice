package com.donation.dto;

import com.amazon.ion.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    private Timestamp timestamp;
    private String senderWalletId;
    private String receiverWalletId;
    private BigDecimal amount;
    private String currency;
    private String source;
    private String expenseId;
    private String descriptionCode;
    private String revenueId;
    private BigDecimal originalAmount;
    private String originalCurrency;
    private float currencyConversion;
}
