package com.donation.service;

import com.donation.dto.Transaction;
import com.donation.dto.Wallet;

public interface TransactionService {

    String insertTransaction(Transaction transaction);
    Transaction getTransactionByDocIid(String id);
}
