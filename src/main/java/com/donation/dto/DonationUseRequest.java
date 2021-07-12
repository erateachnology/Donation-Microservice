package com.donation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DonationUseRequest {
    private String costCenter;
    private String missionId;
    private BigDecimal amount;
    private String currency;
    private String expenseId;
    private String revenueId;
    private String descriptionCode;
    private String source;
}
