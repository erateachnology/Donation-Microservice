package com.donation.controller;

import com.donation.dto.Response;
import com.donation.dto.Transaction;
import com.donation.service.TransactionService;
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
public class TransactionController {

    static final Logger logger = LogManager.getLogger(TransactionController.class.getName());
    public static final String TRANSACTION_ADDED_START = "Transaction added start";
    public static final String TRANSACTION_ADDED_SUCCESSFULLY = "Transaction added successfully {}";
    public static final String REVENUE_ADDED_ERROR = "Revenue added error";
    public static final String TRANSACTION_RETRIEVED_START = "Transaction retrieved start {}";
    public static final String TRANSACTION_RETRIEVED_SUCCESSFULLY = "Transaction retrieved successfully";
    public static final String TRANSACTION_RETRIEVED_SUCCESSFULLY1 = "Transaction retrieved successfully {}";
    public static final String TRANSACTION_RETRIEVED_ERROR = "Transaction retrieved error";

    @Autowired
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Insert into Transaction")
    @PostMapping("/transaction/insert")
    public ResponseEntity<Response> insertTransactions(@RequestBody Transaction transaction) {
        logger.info(TRANSACTION_ADDED_START);
        Response response = new Response();
        try {
            String id = transactionService.insertTransaction(transaction);
            logger.info(TRANSACTION_ADDED_SUCCESSFULLY, id);
            response.setId(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadRequestException | InvalidSessionException | SdkClientException e) {
            logger.error(REVENUE_ADDED_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "Get Transactions by ID")
    @GetMapping("/transaction/{id} ")
    public ResponseEntity<Response> getTransactionById(@PathVariable String id) {
        Response response = new Response();
        logger.info(TRANSACTION_RETRIEVED_START, id);
        try {
            Transaction transaction = transactionService.getTransactionByDocIid(id);
            response.setTransaction(transaction);
            response.setMessage(TRANSACTION_RETRIEVED_SUCCESSFULLY);
            logger.info(TRANSACTION_RETRIEVED_SUCCESSFULLY1, id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadRequestException | InvalidSessionException | SdkClientException e) {
            logger.error(TRANSACTION_RETRIEVED_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
