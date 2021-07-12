package com.donation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Wallet {
    private String ownerId;
    private String crmOwnerId;
    private String ownerType;
    private String missionId;
    private String walletType;
    private BigDecimal availableAmount;
    private BigDecimal totalAmount;
    private String currency;
}
