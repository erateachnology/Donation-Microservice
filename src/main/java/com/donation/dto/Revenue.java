package com.donation.dto;

import com.amazon.ion.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Revenue {
    private String ownerId;
    private String crmOwnerId;
    private String toWalletId;
    private BigDecimal amount;
    private String currency;
    private String source;
    private String transactionId;
    private Timestamp timestamp;
    private String invoiceId;
    private BigDecimal originalAmount;
    private String originalCurrency;
    private float currencyConversion;
}
