package com.donation.dto;

import com.amazon.ion.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DonationRequest {
    private Timestamp transactionTimestamp;
    private String transactionId;
    private String ownerId;
    private String crmOwnerId;
    private String source;
    private BigDecimal totalAmount;
    private String currency;
    private BigDecimal originalAmount;
    private String originalCurrency;
    private float currencyConversion;
    private Distribution distribution;
}
