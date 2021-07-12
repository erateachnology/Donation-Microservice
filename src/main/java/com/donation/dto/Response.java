package com.donation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private String id;
    private String message;
    private Wallet wallet;
    private WalletListResponse walletListResponse;
    private Transaction transaction;
}
