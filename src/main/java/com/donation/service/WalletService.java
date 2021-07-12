package com.donation.service;

import com.donation.dto.Wallet;
import com.donation.dto.WalletListResponse;
import com.donation.dto.WalletTypeRequest;
import com.donation.dto.WalletUpdateRequest;


public interface WalletService {

    String insertWallets(Wallet wallet);
    Wallet getWalletByDocId(String id);
    void updateWalletValues(WalletUpdateRequest walletUpdateRequest);
    WalletListResponse getWalletsByType(WalletTypeRequest walletTypeRequest);
}
