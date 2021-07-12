package com.donation.controller;

import com.donation.dto.DonationRequest;
import com.donation.dto.DonationUseRequest;
import com.donation.dto.Response;
import com.donation.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.qldbsession.model.BadRequestException;
import software.amazon.awssdk.services.qldbsession.model.InvalidSessionException;

@RestController
public class DonationController {

    static final Logger logger = LogManager.getLogger(DonationController.class.getName());
    public static final String DONATION_INSERT_START = "Donation insert start";
    public static final String DONATION_INSERT_SUCCESSFULLY = "Donation insert successfully";
    public static final String DONATION_INSERT_ERROR = "Donation insert error";
    public static final String DONATION_USE_START = "Donation use start";
    public static final String DONATION_USE_SUCCESSFULLY = "Donation use successfully";
    public static final String DONATION_USE_ERROR = "Donation use error";

    @Autowired
    private DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @Operation(summary = "Donation Insert")
    @PostMapping("/donation/insert")
    public ResponseEntity<Response>  insertRevenue(@RequestBody DonationRequest donationRequest){
        Response response = new Response();
        logger.info(DONATION_INSERT_START);
        try{
            donationService.insertDonation(donationRequest);
            logger.info(DONATION_INSERT_SUCCESSFULLY);
            response.setMessage(DONATION_INSERT_SUCCESSFULLY);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }catch (BadRequestException | InvalidSessionException | SdkClientException e){
            logger.error(DONATION_INSERT_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "Donation use")
    @PostMapping("/donation/use")
    public ResponseEntity<Response> donationUse(@RequestBody DonationUseRequest donationUseRequest){
        logger.info(DONATION_USE_START);
        Response response = new Response();
        try{
            donationService.useDonation(donationUseRequest);
            logger.info(DONATION_USE_SUCCESSFULLY);
            response.setMessage(DONATION_USE_SUCCESSFULLY);
            return  new ResponseEntity<>(response,HttpStatus.OK);
        }catch (BadRequestException | InvalidSessionException | SdkClientException e){
            logger.error(DONATION_USE_ERROR, e);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

    }
}
