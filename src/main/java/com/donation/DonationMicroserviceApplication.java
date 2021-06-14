package com.donation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Donation Microservice"))
public class DonationMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonationMicroserviceApplication.class, args);
	}

}
