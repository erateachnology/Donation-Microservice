package com.donation.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Total {
    private BigDecimal amount;
    private String currency;
    private String year;
}
