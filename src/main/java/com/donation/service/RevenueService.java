package com.donation.service;

import com.donation.dto.FinancialOverviewRequest;
import com.donation.dto.Revenue;

public interface RevenueService {
    public String insertRevenue(Revenue revenue);
    public void financialOverview(FinancialOverviewRequest financialOverviewRequest);
}
