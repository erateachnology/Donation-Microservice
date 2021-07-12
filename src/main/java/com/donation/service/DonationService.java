package com.donation.service;

import com.donation.dto.DonationRequest;
import com.donation.dto.DonationUseRequest;

public interface DonationService {
    void insertDonation(DonationRequest donationRequest);
    void useDonation(DonationUseRequest donationUseRequest);
}
