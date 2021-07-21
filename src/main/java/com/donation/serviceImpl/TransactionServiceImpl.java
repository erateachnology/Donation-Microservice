package com.donation.serviceImpl;

import com.amazon.ion.*;
import com.amazon.ion.system.IonSystemBuilder;
import com.donation.configs.Driver;
import com.donation.dto.Transaction;
import com.donation.exception.NotFoundException;
import com.donation.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.qldb.Result;

@Service
public class TransactionServiceImpl implements TransactionService {

    static final Logger logger = LogManager.getLogger(TransactionServiceImpl.class.getName());

    public static final String TIMESTAMP = "timestamp";
    public static final String SENDER_WALLET_ID = "senderWalletId";
    public static final String RECEIVER_WALLET_ID = "receiverWalletId";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String SOURCE = "source";
    public static final String EXPENSE_ID = "expenseId";
    public static final String DESCRIPTION_CODE = "descriptionCode";
    public static final String REVENUE_ID = "revenueId";
    public static final String ORIGINAL_AMOUNT = "originalAmount";
    public static final String ORIGINAL_CURRENCY = "originalCurrency";
    public static final String CURRENCY_CONVERSION = "currencyConversion";
    private static final String INSERT_INTO_REVENUE = "INSERT INTO TRANSACTIONS ?";
    private static final String GET_TRANSACTION_BY_DOC = "SELECT * FROM TRANSACTIONS AS t BY t_id WHERE t_id = ?";

    @Autowired
    private final Driver qldbDriver;
    @Autowired
    private final WalletServiceImpl walletService;

    public TransactionServiceImpl(Driver qldbDriver, WalletServiceImpl walletService) {
        this.qldbDriver = qldbDriver;
        this.walletService = walletService;
    }

    @Override
    public String insertTransaction(Transaction transaction) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        IonStruct transactionData = ionSys.newEmptyStruct();
        logger.info("Transaction add service start");
        transactionData.put(TIMESTAMP).newTimestamp(transaction.getTimestamp());
        transactionData.put(SENDER_WALLET_ID).newString(transaction.getSenderWalletId());
        transactionData.put(RECEIVER_WALLET_ID).newString(transaction.getReceiverWalletId());
        transactionData.put(AMOUNT).newDecimal(transaction.getAmount());
        transactionData.put(CURRENCY).newString(transaction.getCurrency());
        transactionData.put(SOURCE).newString(transaction.getSource());
        transactionData.put(EXPENSE_ID).newString(transaction.getExpenseId());
        transactionData.put(DESCRIPTION_CODE).newString(transaction.getDescriptionCode());
        transactionData.put(REVENUE_ID).newString(transaction.getRevenueId());
        transactionData.put(ORIGINAL_AMOUNT).newDecimal(transaction.getOriginalAmount());
        transactionData.put(ORIGINAL_CURRENCY).newString(transaction.getOriginalCurrency());
        transactionData.put(CURRENCY_CONVERSION).newDecimal(transaction.getCurrencyConversion());

        Result result = qldbDriver.QldbDriver().execute(txn -> {
            return txn.execute(INSERT_INTO_REVENUE, transactionData);
        });

        return walletService.getDocumentId(result);
    }

    @Override
    public Transaction getTransactionByDocIid(String id) {
        Transaction transaction = new Transaction();
        logger.info("Transaction retrieve by doc id service start {}", id);
        return qldbDriver.QldbDriver().execute(txn -> {
            IonStruct ionStruct;
            IonSystem ionSys = IonSystemBuilder.standard().build();
            Result result = txn.execute(GET_TRANSACTION_BY_DOC, ionSys.newString(id));
            if (!result.isEmpty()) {
                for (IonValue ionValue : result) {
                    logger.info("Transactions retrieved by doc id {} ",id);
                    ionStruct = (IonStruct) ionValue;
                    //transaction.setTimestamp();
                    transaction.setSenderWalletId(((IonString) ionStruct.get(SENDER_WALLET_ID)).stringValue());
                    transaction.setReceiverWalletId(((IonString) ionStruct.get(RECEIVER_WALLET_ID)).stringValue());
                    transaction.setAmount(((IonDecimal) ionStruct.get(AMOUNT)).decimalValue());
                    transaction.setCurrency(((IonString) ionStruct.get(CURRENCY)).stringValue());
                    transaction.setSource(((IonString) ionStruct.get(SOURCE)).stringValue());
                    transaction.setExpenseId(((IonString) ionStruct.get(EXPENSE_ID)).stringValue());
                    transaction.setDescriptionCode(((IonString) ionStruct.get(DESCRIPTION_CODE)).stringValue());
                    transaction.setRevenueId(((IonString) ionStruct.get(REVENUE_ID)).stringValue());
                    transaction.setOriginalAmount(((IonDecimal) ionStruct.get(ORIGINAL_AMOUNT)).decimalValue());
                    transaction.setOriginalCurrency(((IonString) ionStruct.get(ORIGINAL_CURRENCY)).stringValue());
                    transaction.setCurrencyConversion(((IonDecimal) ionStruct.get(CURRENCY_CONVERSION)).floatValue());

                }
            } else {
                logger.error("No transaction found under this document id {}", id);
                throw new NotFoundException("No transaction found under this document id" + id);
            }
            return transaction;
        });
    }
}
