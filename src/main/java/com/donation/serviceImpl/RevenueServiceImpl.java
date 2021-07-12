package com.donation.serviceImpl;

import com.amazon.ion.IonStruct;
import com.amazon.ion.IonSystem;
import com.amazon.ion.IonValue;
import com.amazon.ion.Timestamp;
import com.amazon.ion.system.IonSystemBuilder;
import com.donation.configs.Driver;
import com.donation.dto.FinancialOverviewRequest;
import com.donation.dto.Revenue;
import com.donation.service.RevenueService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.qldb.Result;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class RevenueServiceImpl implements RevenueService {

    static final Logger logger = LogManager.getLogger(RevenueServiceImpl.class.getName());

    public static final String REVENUE_ID = "revenueId";
    public static final String OWNER_ID = "ownerId";
    public static final String CRM_OWNER_ID = "crmOwnerId";
    public static final String TO_WALLET_ID = "toWalletId";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String SOURCE = "source";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String REVENUE_ADDED_TIME = "revenueAddedTime";
    public static final String INVOICE_ID = "invoiceId";
    public static final String ORIGINAL_AMOUNT = "originalAmount";
    public static final String ORIGINAL_CURRENCY = "originalCurrency";
    public static final String CURRENCY_CONVERSION = "currencyConversion";
    private final String INSERT_INTO_REVENUE = "INSERT INTO REVENUES ?";
    private final String GET_REVENUE = "SELECT * FROM REVENUES AS r WHERE r.ownerId = ? AND r.revenueAddedTime >= ? AND r.revenueAddedTime < ?";

    @Autowired
    private Driver qldbDriver;

    @Autowired
    private WalletServiceImpl walletService;

    public RevenueServiceImpl(Driver qldbDriver, WalletServiceImpl walletService) {
        this.qldbDriver = qldbDriver;
        this.walletService = walletService;
    }

    @Override
    public String insertRevenue(Revenue revenue) {
        String uniqueID = UUID.randomUUID().toString();
        IonSystem ionSys = IonSystemBuilder.standard().build();
        logger.info("Revenue add service start {}", revenue.toString());
        IonStruct revenueData = ionSys.newEmptyStruct();
        revenueData.put(REVENUE_ID).newString(uniqueID);
        revenueData.put(OWNER_ID).newString(revenue.getOwnerId());
        revenueData.put(CRM_OWNER_ID).newString(revenue.getCrmOwnerId());
        revenueData.put(TO_WALLET_ID).newString(revenue.getToWalletId());
        revenueData.put(AMOUNT).newDecimal(revenue.getAmount());
        revenueData.put(CURRENCY).newString(revenue.getCurrency());
        revenueData.put(SOURCE).newString(revenue.getSource());
        revenueData.put(TRANSACTION_ID).newString(revenue.getTransactionId());
        revenueData.put(REVENUE_ADDED_TIME).newTimestamp(revenue.getTimestamp());
        revenueData.put(INVOICE_ID).newString(revenue.getInvoiceId());
        revenueData.put(ORIGINAL_AMOUNT).newDecimal(revenue.getOriginalAmount());
        revenueData.put(ORIGINAL_CURRENCY).newString(revenue.getOriginalCurrency());
        revenueData.put(CURRENCY_CONVERSION).newDecimal(revenue.getCurrencyConversion());
        Result result = qldbDriver.QldbDriver().execute(txn -> {
            return txn.execute(INSERT_INTO_REVENUE, revenueData);
        });
        return walletService.getDocumentId(result);
    }

    @Override
    public void financialOverview(FinancialOverviewRequest financialOverviewRequest) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        Date parsedDate = null;
        Timestamp startTimestamp;
        Timestamp endTimestamp;
        try {
            parsedDate = dateFormat.parse(financialOverviewRequest.getYear());
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);
            startTimestamp = new Timestamp(cal);
            cal.add(Calendar.YEAR, 1);
            endTimestamp = new Timestamp(cal);
            qldbDriver.QldbDriver().execute(txn -> {
                Result result = txn.execute(
                        GET_REVENUE, ionSys.newString(financialOverviewRequest.getOwnerId()), ionSys.newTimestamp(startTimestamp), ionSys.newTimestamp(endTimestamp));
                //getDocumentId(result);
                if (!result.isEmpty()) {
                    for (IonValue ionValue : result) {
                        IonStruct ionStruct;
                        ionStruct = (IonStruct) ionValue;
                    }

                }
            });

            // Timestamp endTimeStamp = new Timestamp(cal.add(Calendar.YEAR, 1));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


}
