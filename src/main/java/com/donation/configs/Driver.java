package com.donation.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.qldbsession.QldbSessionClient;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.RetryPolicy;

@Configuration
public class Driver {
    @Value("${ledger.name}")
    private String ledgerName;

    @Bean
    public QldbDriver QldbDriver(){

            return QldbDriver.builder()
                    .ledger(ledgerName)
                    .transactionRetryPolicy(RetryPolicy
                            .builder()
                            .maxRetries(3)
                            .build())
                    .sessionClientBuilder(QldbSessionClient.builder()
                            .region(Region.US_EAST_2)

                    )
                    .build();

        }

}
