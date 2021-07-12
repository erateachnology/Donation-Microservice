package com.donation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinancialOverviewRequest {
    private String ownerId;
    private String currency;
    private String year;
}
