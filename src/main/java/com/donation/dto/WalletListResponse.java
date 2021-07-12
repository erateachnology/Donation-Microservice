package com.donation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletListResponse {

    private String missionId;
    private String walletType;
    private BigDecimal availableAmount;
    private BigDecimal totalAmount;
    private String currency;
    private String id;
}
