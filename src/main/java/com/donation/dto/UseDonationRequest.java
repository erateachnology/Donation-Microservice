package com.donation.dto;

import com.amazon.ion.IonStruct;
import com.amazon.ion.IonSystem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import software.amazon.qldb.TransactionExecutor;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UseDonationRequest {
    private  DonationUseRequest donationUseRequest;
    private  IonSystem ionSys;
    private  BigDecimal[] avaAmount;
    private  BigDecimal[] amount;
    private  BigDecimal[] updatedAvailable;
    private  BigDecimal[] transactionAmount;
    private  BigDecimal[] updatedZew;
    private  TransactionExecutor txn;
    private  String walletType;
    private  IonStruct ionStruct;

}
