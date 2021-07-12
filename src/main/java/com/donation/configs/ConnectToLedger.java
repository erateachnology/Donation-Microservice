/*
package com.donation.configs;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.qldbsession.QldbSessionClient;
import software.amazon.awssdk.services.qldbsession.QldbSessionClientBuilder;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.RetryPolicy;
public class ConnectToLedger {

    public static AwsCredentialsProvider credentialsProvider;
    public static String endpoint = null;
    public static String ledgerName = "demo";
    public static String region = null;
    public static QldbDriver driver;

    private ConnectToLedger() {
    }

    */
/**
     * Create a pooled driver for creating sessions.
     *
     * @param retryAttempts How many times the transaction will be retried in
     * case of a retryable issue happens like Optimistic Concurrency Control exception,
     * server side failures or network issues.
     * @return The pooled driver for creating sessions.
     *//*

    public static QldbDriver createQldbDriver(int retryAttempts) {
        QldbSessionClientBuilder builder = getAmazonQldbSessionClientBuilder();
        return QldbDriver.builder()
                .ledger(ledgerName)
                .transactionRetryPolicy(RetryPolicy
                        .builder()
                        .maxRetries(retryAttempts)
                        .build())
                .sessionClientBuilder(builder)
                .build();
    }

    */
/**
     * Create a pooled driver for creating sessions.
     *
     * @return The pooled driver for creating sessions.
     *//*

    public static QldbDriver createQldbDriver() {
        QldbSessionClientBuilder builder = getAmazonQldbSessionClientBuilder();
        return QldbDriver.builder()
                .ledger(ledgerName)
                .transactionRetryPolicy(RetryPolicy.builder()
                        .maxRetries(3).build())
                .sessionClientBuilder(builder)
                .build();
    }

    */
/**
     * Creates a QldbSession builder that is passed to the QldbDriver to connect to the Ledger.
     *
     * @return An instance of the AmazonQLDBSessionClientBuilder
     *//*

    public static QldbSessionClientBuilder getAmazonQldbSessionClientBuilder() {
       */
/* AwsCredentialsProvider awsCreds = AwsCredentialsProvider.(
                "AKIAZN2RRW67EKJRTBUS",
                "u/LWg8VfyOowUCaAWZQVZQM8Rdh03+nzV+o3knDG");*//*

        QldbSessionClientBuilder builder = QldbSessionClient.builder()
                .region(Region.US_EAST_2);
        if (null != endpoint && null != region) {
            try {
                builder.endpointOverride(new URI(endpoint));
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (null != credentialsProvider) {
            //builder.credentialsProvider(awsCreds);
           builder.credentialsProvider(credentialsProvider);
        }
        return builder;
    }

    */
/**
     * Create a pooled driver for creating sessions.
     *
     * @return The pooled driver for creating sessions.
     *//*

    public static QldbDriver getDriver() {
        if (driver == null) {
            driver = createQldbDriver();
        }
        return driver;
    }


    public static void main(final String... args) {
        Iterable<String> tables = ConnectToLedger.getDriver().getTableNames();
        //log.info("Existing tables in the ledger:");
        for (String table : tables) {
            System.out.println(table);
        }
    }
}
*/
