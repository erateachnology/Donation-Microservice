package com.donation.controller;

import com.donation.dto.FinancialOverviewRequest;
import com.donation.dto.Response;
import com.donation.dto.Revenue;
import com.donation.service.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.qldbsession.model.BadRequestException;
import software.amazon.awssdk.services.qldbsession.model.InvalidSessionException;

@RestController
public class RevenueController {
    static final Logger logger = LogManager.getLogger(RevenueController.class.getName());
    public static final String REVENUE_ADDED_START = "Revenue added start";
    public static final String REVENUE_ADDED_SUCCESSFULLY = "Revenue added successfully {}";
    public static final String REVENUE_ADDED_ERROR = "Revenue added error";

    @Autowired
    private RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @Operation(summary = "Revenue Insert")
    @PostMapping("/revenue/insert")
    public ResponseEntity<Response> insertRevenue(@RequestBody Revenue revenue) {
        logger.info(REVENUE_ADDED_START);
        Response response = new Response();
        try {
            String id = revenueService.insertRevenue(revenue);
            logger.info(REVENUE_ADDED_SUCCESSFULLY, id);
            response.setId(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadRequestException | InvalidSessionException | SdkClientException e) {
            logger.error(REVENUE_ADDED_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "Get financial overview")
    @GetMapping("/financials/overview")
    public void financialOverview(@RequestBody FinancialOverviewRequest financialOverviewRequest) {
        revenueService.financialOverview(financialOverviewRequest);
    }
}
