package com.donation.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LastDonation {
    private BigDecimal amount;
    private String currency;
}
