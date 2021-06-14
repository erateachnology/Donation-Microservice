package com.donation.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DonationController {

    static final Logger logger = LogManager.getLogger(DonationController.class.getName());

    @Operation(summary = "donation microservicve sample API")
    @GetMapping("/")
    public String testApi(){
        logger.info("First API Called");
        logger.debug("Debug Logs Called");
        return "Hello Donation";
    }

}
