package com.donation.serviceImpl;

import com.amazon.ion.*;
import com.amazon.ion.system.IonSystemBuilder;
import com.donation.configs.Driver;
import com.donation.dto.Wallet;
import com.donation.dto.WalletListResponse;
import com.donation.dto.WalletTypeRequest;
import com.donation.dto.WalletUpdateRequest;
import com.donation.exception.InsufficientAvailableAmountException;
import com.donation.exception.NotFoundException;
import com.donation.service.WalletService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

import java.math.BigDecimal;
import java.util.*;

@Service
public class WalletServiceImpl implements WalletService {

    public static final String IN_OVERHEADS = "IN_Overheads";
    public static final String ID = "id";
    public static final String NO_WALLET_UNDER_THE_DOC_ID1 = "No wallet under the doc id";
    public static final String NO_WALLET_UNDER_THE_DOC_ID = NO_WALLET_UNDER_THE_DOC_ID1 + " ";
    public static final String NO_WALLET_TYPE_PROVIDED = "No wallet type provided";
    public static final String IN_DEVELOPMENT = "IN_Development";
    public static final String IN_MISSION = "IN_Mission";
    public static final String SELECT_FROM_WALLET_AS_W_BY_W_ID_WHERE_W_ID = "SELECT * FROM WALLET AS w BY w_id WHERE w_id = ?";
    public static final String INCREMENT = "INCREMENT";
    public static final String MISSION_ID = "missionId";
    public static final String DECREMENT = "DECREMENT";
    public static final String AVAILABLE_AMOUNT = "availableAmount";
    public static final String WALLET_ID = "walletId";
    public static final String OWNER_ID = "ownerId";
    public static final String CRM_OWNER_ID = "crmOwnerId";
    public static final String OWNER_TYPE = "ownerType";
    public static final String WALLET_TYPE = "walletType";
    public static final String TOTAL_AMOUNT = "totalAmount";
    public static final String CURRENCY = "currency";
    public static final String WALLET_INSERT_SUCCESSFULLY = "Wallet insert successfully {}";
    public static final String DOCUMENT_ID = "documentId";
    private static final String INSERT_INTO_WALLET = "INSERT INTO WALLET ?";
    private static final String UPDATE_WALLET = "UPDATE WALLET AS w BY w_id SET w.availableAmount = ?,w.totalAmount = ? WHERE w_id = ?";
    private static final String GET_WALLET_DOC_ID_BY_TYPE = "SELECT id FROM WALLET AS w By id WHERE w.walletType = ?";
    private static final String Get_FROM_WALLET = "SELECT data  FROM _ql_committed_WALLET WHERE metadata.id = ?";
    private static final String WALLETS_BY_WALLET_TYPE_SINGLE_PARAM = "SELECT * FROM  WALLET AS w WHERE w.walletType IN (?)";
    private static final String WALLET_BY_WALLET_TYPE = "SELECT * FROM WALLET AS w WHERE w.walletType = ?";
    private static final String WALLETS_BY_MISSION = "SELECT * FROM WALLET AS w WHERE w.walletType = ? AND w.missionId = ?";
    private static final String WALLETS_BY_WALLET_TYPE_TWO_PARAM = "SELECT * FROM  WALLET AS w WHERE w.walletType IN (?,?)";
    private static final String GET_WALLET_BY_DOC_ID = "SELECT * FROM WALLET AS w BY w_id WHERE w_id = ?";
    public static final String NO_WALLET_UNDER_GIVEN_TYPE = "No wallet under given type";
    public static final String OUT_OVERHEADS = "OUT_Overheads";
    public static final String OUT_MISSION = "OUT_Mission";
    public static final String OUT_DEVELOPMENT = "OUT_Development";
    public static final String INSUFFICIENT_AVAILABLE_FUNDS = "Insufficient Available Funds";
    private final String GET_WALLET_ID_MISSION_ID = "SELECT id FROM WALLET AS w By id WHERE w.walletType = ? AND w.missionId = ?";
    private static final Logger logger = LogManager.getLogger(WalletServiceImpl.class.getName());

    @Autowired
    private Driver qldbDriver;

    public WalletServiceImpl(Driver qldbDriver) {
        this.qldbDriver = qldbDriver;
    }

    /*
    Insert Wallets
     */
    @Override
    public String insertWallets(Wallet wallet) {
        String uniqueID = UUID.randomUUID().toString();
        IonSystem ionSys = IonSystemBuilder.standard().build();
        IonStruct walletData = ionSys.newEmptyStruct();
        walletData.put(WALLET_ID).newString(uniqueID);
        walletData.put(OWNER_ID).newString(wallet.getOwnerId());
        walletData.put(CRM_OWNER_ID).newString(wallet.getCrmOwnerId());
        walletData.put(OWNER_TYPE).newString(wallet.getOwnerType());
        walletData.put(MISSION_ID).newString(wallet.getMissionId());
        walletData.put(WALLET_TYPE).newString(wallet.getWalletType());
        walletData.put(AVAILABLE_AMOUNT).newDecimal(new BigDecimal(String.valueOf(wallet.getAvailableAmount())));
        walletData.put(TOTAL_AMOUNT).newDecimal(new BigDecimal(String.valueOf(wallet.getTotalAmount())));
        walletData.put(CURRENCY).newString(wallet.getCurrency());
        Result result = qldbDriver.QldbDriver().execute(txn -> {
            return txn.execute(INSERT_INTO_WALLET, walletData);
        });
        logger.info(WALLET_INSERT_SUCCESSFULLY, getDocumentId(result));
        return getDocumentId(result);
    }

    /*
    Get Document ID
     */
    public String getDocumentId(Result result) {
        String documentId = null;
        for (IonValue obj : result) {
            if (obj instanceof IonStruct) {
                IonStruct val = (IonStruct) obj;
                IonString str = (IonString) val.get(DOCUMENT_ID);
                documentId = str.stringValue();
                break;
            }
        }
        return documentId;
    }

    public Result getWalletResult(String param) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        return qldbDriver.QldbDriver().execute(txn -> {
            return txn.execute(SELECT_FROM_WALLET_AS_W_BY_W_ID_WHERE_W_ID, ionSys.newString(param));
        });
    }

    @Override
    public Wallet getWalletByDocId(String id) {
        Wallet wallet1 = new Wallet();
        return qldbDriver.QldbDriver().execute(txn -> {
            IonStruct ionStruct;
            IonSystem ionSys = IonSystemBuilder.standard().build();
            Result result = txn.execute(GET_WALLET_BY_DOC_ID, ionSys.newString(id));
            if (!result.isEmpty()) {
                for (IonValue ionValue : result) {
                    ionStruct = (IonStruct) ionValue;
                    wallet1.setOwnerId(((IonString) ionStruct.get(OWNER_ID)).stringValue());
                    wallet1.setCrmOwnerId(((IonString) ionStruct.get(CRM_OWNER_ID)).stringValue());
                    wallet1.setOwnerType(((IonString) ionStruct.get(OWNER_TYPE)).stringValue());
                    wallet1.setMissionId(((IonString) ionStruct.get(MISSION_ID)).stringValue());
                    wallet1.setWalletType(((IonString) ionStruct.get(WALLET_TYPE)).stringValue());
                    wallet1.setAvailableAmount(((IonDecimal) ionStruct.get(AVAILABLE_AMOUNT)).decimalValue());
                    wallet1.setTotalAmount(((IonDecimal) ionStruct.get(TOTAL_AMOUNT)).decimalValue());
                    wallet1.setCurrency(((IonString) ionStruct.get(CURRENCY)).stringValue());

                }
            } else {
                throw new NotFoundException(NO_WALLET_UNDER_THE_DOC_ID1 + " " + id);
            }
            return wallet1;
        });
    }

    @Override
    public void updateWalletValues(WalletUpdateRequest walletUpdateRequest) {
        //Increment
        //get the wallet by doc ID
        IonStruct ionStruct;
        Result result = getWalletResult(walletUpdateRequest.getId());
        IonSystem ionSys = IonSystemBuilder.standard().build();
        //Increment the fileds
        if ((!result.isEmpty())) {
            for (IonValue ionValue : result) {
                ionStruct = (IonStruct) ionValue;
                BigDecimal currentAvailable = ((IonDecimal) ionStruct.get(AVAILABLE_AMOUNT)).decimalValue();
                BigDecimal currentTotalAmount = ((IonDecimal) ionStruct.get(TOTAL_AMOUNT)).decimalValue();
                BigDecimal updatedAvailable = new BigDecimal("0.00");
                BigDecimal updatedTotal = new BigDecimal("0.00");
                if (walletUpdateRequest.getAvailableAmount() != null ||
                        walletUpdateRequest.getTotalAmount() != null) {
                    if ((walletUpdateRequest.getOperation().equalsIgnoreCase(INCREMENT))) {
                        updatedAvailable = currentAvailable.add(ionSys.newDecimal(walletUpdateRequest.getAvailableAmount()).decimalValue());
                        updatedTotal = currentTotalAmount.add(ionSys.newDecimal(walletUpdateRequest.getTotalAmount()).decimalValue());
                        update(updatedAvailable, updatedTotal, walletUpdateRequest.getId());
                    } else if ((walletUpdateRequest.getOperation().equalsIgnoreCase(DECREMENT))) {

                        if (currentAvailable.compareTo(walletUpdateRequest.getAvailableAmount()) > 0) {
                            updatedAvailable = currentAvailable.subtract(ionSys.newDecimal
                                    (walletUpdateRequest.getAvailableAmount()).decimalValue());
                        } else {
                            throw new InsufficientAvailableAmountException(INSUFFICIENT_AVAILABLE_FUNDS);
                        }

                        updatedTotal = ionSys.newDecimal
                                (walletUpdateRequest.getTotalAmount()).decimalValue();

                        update(updatedAvailable, updatedTotal, walletUpdateRequest.getId());
                    }
                }

            }
        } else {
            throw new NotFoundException(NO_WALLET_UNDER_THE_DOC_ID + walletUpdateRequest.getId());
        }

    }

    public void update(BigDecimal updatedAvailable, BigDecimal updatedTotal, String id) {
        qldbDriver.QldbDriver().execute(txn -> {
            IonSystem ionSys = IonSystemBuilder.standard().build();
            List<IonValue> parameters = new ArrayList<>();
            parameters.add(ionSys.newDecimal(updatedAvailable));
            parameters.add(ionSys.newDecimal(updatedTotal));
            parameters.add(ionSys.newString(id));
            txn.execute(UPDATE_WALLET, parameters);

        });
    }

    @Override
    public WalletListResponse getWalletsByType(WalletTypeRequest walletTypeRequest) {
        if (!walletTypeRequest.getWalletType().isEmpty()) {
            List<String> walletTypeList = Arrays.asList(walletTypeRequest.getWalletType().split(","));
            final BigDecimal[] totalAmount = {new BigDecimal(0)};
            final BigDecimal[] totalAvailableAmount = {new BigDecimal(0)};
            WalletListResponse walletListResponse = new WalletListResponse();
            final IonStruct[] ionStruct = new IonStruct[1];
            final Result[] result = new Result[1];
            qldbDriver.QldbDriver().execute(txn -> {
                IonSystem ionSys = IonSystemBuilder.standard().build();
                for (String params : walletTypeList) {

                    if(params.equalsIgnoreCase(IN_MISSION) ||params.equalsIgnoreCase(OUT_MISSION) ){
                        result[0] = txn.execute(WALLETS_BY_MISSION, ionSys.newString(params),
                                ionSys.newString(walletTypeRequest.getMissionId()));
                    }else{
                        result[0] = txn.execute(WALLET_BY_WALLET_TYPE, ionSys.newString(params));
                    }

                   /* //Check wallet type
                    if (params.equalsIgnoreCase(IN_OVERHEADS)) {
                        result[0] = txn.execute(WALLET_BY_WALLET_TYPE, ionSys.newString(params));
                    } else if (params.equalsIgnoreCase(IN_DEVELOPMENT)) {
                        result[0] = txn.execute(WALLET_BY_WALLET_TYPE, ionSys.newString(params));
                    } else if (params.equalsIgnoreCase(IN_MISSION)) {
                        result[0] = txn.execute(WALLETS_BY_MISSION, ionSys.newString(params),
                                ionSys.newString(walletTypeRequest.getMissionId()));
                    } else if (params.equalsIgnoreCase(OUT_OVERHEADS)) {
                        result[0] = txn.execute(WALLET_BY_WALLET_TYPE, ionSys.newString(params));

                    } else if (params.equalsIgnoreCase(OUT_DEVELOPMENT)) {
                        result[0] = txn.execute(WALLET_BY_WALLET_TYPE, ionSys.newString(params));
                    } else if (params.equalsIgnoreCase(OUT_MISSION)) {
                        result[0] = txn.execute(WALLETS_BY_MISSION, ionSys.newString(params),
                                ionSys.newString(walletTypeRequest.getMissionId()));
                    }*/
                    if (!result[0].isEmpty()) {
                        for (IonValue ionValue : result[0]) {
                            ionStruct[0] = (IonStruct) ionValue;
                            BigDecimal currentTotal = ((IonDecimal) ionStruct[0].get(TOTAL_AMOUNT)).decimalValue();
                            BigDecimal currentAvailable = ((IonDecimal) ionStruct[0].get(AVAILABLE_AMOUNT)).decimalValue();
                            totalAmount[0] = totalAmount[0].add(currentTotal);
                            totalAvailableAmount[0] = totalAvailableAmount[0].add(currentAvailable);
                            walletListResponse.setCurrency(((IonString) ionStruct[0].get(CURRENCY)).stringValue());
                            if (params.equalsIgnoreCase(OUT_MISSION)) {
                                String missionID = ((IonString) ionStruct[0].get(MISSION_ID)).stringValue();
                                walletListResponse.setId(getWalletDocIDByMissionID(txn, params, missionID));
                            } else if (params.equalsIgnoreCase(OUT_OVERHEADS) || params.equalsIgnoreCase(OUT_DEVELOPMENT)) {
                                walletListResponse.setId(getWalletDocID(txn, params));
                            }

                        }
                        walletListResponse.setTotalAmount(totalAmount[0]);
                        walletListResponse.setAvailableAmount(totalAvailableAmount[0]);
                        walletListResponse.setWalletType(walletTypeRequest.getWalletType());
                    } else {
                        throw new NotFoundException(NO_WALLET_UNDER_GIVEN_TYPE);
                    }

                }

            });

            return walletListResponse;
        } else {
            throw new NotFoundException(NO_WALLET_TYPE_PROVIDED);
        }

    }

    public String getWalletDocIDByMissionID(TransactionExecutor txn, String walletType, String missionID) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String docId = null;
        Result result1 = txn.execute(GET_WALLET_ID_MISSION_ID, ionSys.newString(walletType), ionSys.newString(missionID));
        for (IonValue ionValues : result1) {
            IonStruct ionStruct1;
            ionStruct1 = (IonStruct) ionValues;
            docId = ((IonString) ionStruct1.get(ID)).stringValue();
        }
        return docId;
    }

    public String getWalletDocID(TransactionExecutor txn, String walletType) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String docId = null;
        Result result1 = txn.execute(GET_WALLET_DOC_ID_BY_TYPE, ionSys.newString(walletType));
        for (IonValue ionValues : result1) {
            IonStruct ionStruct1;
            ionStruct1 = (IonStruct) ionValues;
            docId = ((IonString) ionStruct1.get(ID)).stringValue();
        }
        return docId;
    }
}
