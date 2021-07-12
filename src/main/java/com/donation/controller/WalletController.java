package com.donation.controller;

import com.donation.dto.*;
import com.donation.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.qldbsession.model.BadRequestException;
import software.amazon.awssdk.services.qldbsession.model.InvalidSessionException;


@RestController
public class WalletController {
    static final Logger logger = LogManager.getLogger(WalletController.class.getName());
    public static final String INSERT_WALLET_START_TO_OWNER_ID = "Insert wallet start to ownerId: {}";
    public static final String INSERT_WALLET_SUCCESSFUL = "Insert wallet successful";
    public static final String INSERT_WALLET_ERROR = "Insert wallet error";
    public static final String GET_WALLET_BY_DOCUMENT_ID_START = "Get Wallet by document id start: {}";
    public static final String GET_WALLET_BY_DOCUMENT_ID_SUCCESS = "Get Wallet by document id success: {}";
    public static final String GET_WALLET_BY_DOCUMENT_ID_ERROR = "Get Wallet by document id error";
    public static final String WALLET_UPDATED_START = "Wallet updated start: {}";
    public static final String WALLET_UPDATE_SUCCESS = "Wallet update success: {}";
    public static final String WALLET_UPDATE_SUCCESS1 = "Wallet update success";
    public static final String WALLET_UPDATE_ERROR = "Wallet update error";
    public static final String WALLET_FETCH_BY_TYPE_START = "Wallet fetch by type start";
    public static final String WALLET_FETCH_BY_TYPE_SUCCESS = "Wallet fetch by type success";
    public static final String WALLET_FETCH_BY_TYPE_ERROR = "Wallet fetch by type error";

    @Autowired
    private WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @Operation(summary = "donation microservicve sample API")
    @GetMapping("/")
    public String testApi() {
        logger.info("First API Called");
        return "Hello Donation";
    }

    @Operation(summary = "Insert into Wallet")
    @PostMapping("/wallet/insert")
    private ResponseEntity<Response> insertWallet(@RequestBody Wallet wallet) {
        logger.info(INSERT_WALLET_START_TO_OWNER_ID, wallet.getOwnerId());
        Response response = new Response();
        try {
            String Val = walletService.insertWallets(wallet);
            response.setId(Val);
            logger.info(INSERT_WALLET_SUCCESSFUL);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadRequestException | InvalidSessionException | SdkClientException e) {
            logger.error(INSERT_WALLET_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "Get Wallet By Document ID")
    @GetMapping("/wallet/{id}")
    public ResponseEntity<Response> getWalletByDocId(@PathVariable String id) {
        logger.info(GET_WALLET_BY_DOCUMENT_ID_START, id);
        Response response = new Response();
        try {
            Wallet wallet = walletService.getWalletByDocId(id);
            response.setWallet(wallet);
            logger.info(GET_WALLET_BY_DOCUMENT_ID_SUCCESS, id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadRequestException | InvalidSessionException | SdkClientException e) {
            logger.error(GET_WALLET_BY_DOCUMENT_ID_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "Update a wallet")
    @PutMapping("/wallet/updateAmounts")
    public ResponseEntity<Response> updateWallet(@RequestBody WalletUpdateRequest walletUpdateRequest) {
        logger.info(WALLET_UPDATED_START, walletUpdateRequest.getId());
        Response response = new Response();
        try {
            walletService.updateWalletValues(walletUpdateRequest);
            logger.info(WALLET_UPDATE_SUCCESS, walletUpdateRequest.getId());
            response.setMessage(WALLET_UPDATE_SUCCESS1);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadRequestException | InvalidSessionException | SdkClientException e) {
            logger.error(WALLET_UPDATE_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "Get wallet by wallet type")
    @GetMapping("/wallet/list")
    public ResponseEntity<Response> getWalletsByType(@RequestBody WalletTypeRequest walletTypeRequest) {
        logger.info(WALLET_FETCH_BY_TYPE_START);
        Response response = new Response();
        try {
            WalletListResponse walletList = walletService.getWalletsByType(walletTypeRequest);
            logger.info(WALLET_FETCH_BY_TYPE_SUCCESS);
            response.setWalletListResponse(walletList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadRequestException | InvalidSessionException | SdkClientException e) {
            logger.error(WALLET_FETCH_BY_TYPE_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


    }

}
