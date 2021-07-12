package com.donation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletUpdateRequest {
    private String Id;
    private BigDecimal availableAmount;
    private BigDecimal totalAmount;
    private String operation;
}
