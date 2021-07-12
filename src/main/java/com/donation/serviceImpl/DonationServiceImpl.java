package com.donation.serviceImpl;

import com.amazon.ion.*;
import com.amazon.ion.system.IonSystemBuilder;
import com.donation.configs.Driver;
import com.donation.dto.DonationRequest;
import com.donation.dto.DonationUseRequest;
import com.donation.dto.Missions;
import com.donation.service.DonationService;
import com.donation.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


@Service
public class DonationServiceImpl implements DonationService {

    public static final String WALLET_TYPE = "walletType";
    public static final String IN_OVERHEADS = "IN_Overheads";
    public static final String AVAILABLE_AMOUNT = "availableAmount";
    public static final String WALLET_ID = "walletId";
    public static final String IN_DEVELOPMENT = "IN_Development";
    public static final String IN_MISSION = "IN_Mission";
    public static final String MISSION_ID = "missionId";
    public static final String REVENUE_ID = "revenueId";
    public static final String OWNER_ID = "ownerId";
    public static final String CRM_OWNER_ID = "crmOwnerId";
    public static final String TO_WALLET_ID = "toWalletId";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String SOURCE = "source";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String TIME_STAMP = "timeStamp";
    public static final String ORIGINAL_AMOUNT = "originalAmount";
    public static final String ORIGINAL_CURRENCY = "originalCurrency";
    public static final String CURRENCY_CONVERSION = "currencyConversion";
    public static final String TOTAL_AMOUNT = "totalAmount";
    public static final String ID = "id";
    public static final String DOCUMENT_ID = "documentId";
    public static final String OVERHEADS = "OVERHEADS";
    public static final String DEVELOPMENT = "DEVELOPMENT";
    public static final String MISSION = "MISSION";
    public static final String OUT_OVERHEADS = "OUT_Overheads";
    public static final String OUT_DEVELOPMENT = "OUT_Development";
    public static final String OUT_MISSION = "OUT_Mission";
    public static final String TRANSACTION_ID1 = "transactionID";
    public static final String SENDER_WALLET_ID = "senderWalletId";
    public static final String RECEIVER_WALLET_ID = "receiverWalletId";
    public static final String EXPENSE_ID = "expenseId";
    public static final String DESCRIPTION_CODE = "descriptionCode";
    private final String GET_WALLETS_BY_OWNER = "SELECT * FROM WALLET AS w WHERE w.ownerId = ?";
    private final String GET_WALLETS_BY_TYPE_AMOUNT = "SELECT * FROM WALLET AS w WHERE w.walletType = ? AND w.availableAmount >= ?";
    private final String GET_ALL_WALLET_BY_TYPE = "SELECT * FROM WALLET AS w WHERE w.walletType = ?";
    private final String GET_WALLET_ID = "SELECT id FROM WALLET AS w By id WHERE w.walletType = ? AND w.ownerId = ?";
    private final String GET_WALLET_DOCID_BY_WALLET_ID = "SELECT id FROM WALLET AS w BY id WHERE w.walletId = ?";
    private final String GET_ZEW_WALLET_DOC_ID = "SELECT id FROM WALLET AS w BY id WHERE w.walletType = ?";
    private final String GET_WALLET_ID_MISSION_ID = "SELECT id FROM WALLET AS w By id WHERE w.walletType = ? AND w.ownerId = ? AND w.missionId = ?";
    private final String GET_ALL_WALLET_BY_MISSION = "SELECT * FROM WALLET AS w WHERE w.walletType = ? AND w.missionId = ? ";
    private final String WALLET_BY_ID_AND_TYPE = "SELECT * FROM WALLET AS w WHERE w.ownerId = ? AND w.walletType = ?";
    private final String WALLET_BY_ID_AND_MISSION = "SELECT * FROM WALLET AS w WHERE w.ownerId = ? AND w.missionId = ?";
    private final String INSERT_INTO_WALLET = "INSERT INTO WALLET ?";
    private final String UPDATE_WALLET = "UPDATE WALLET AS w SET w.availableAmount = ? WHERE w.walletId = ?";
    private final String UPDATE_ZEW_WALLET = "UPDATE WALLET AS w SET w.totalAmount = ? WHERE w.walletId = ?";
    private final String INSERT_INTO_REVENUE = "INSERT INTO REVENUES ?";
    private final String INSERT_INTO_TRANSACTION = "INSERT INTO TRANSACTIONS ?";
    @Autowired
    private Driver qldbDriver;

    @Autowired
    private WalletService walletService;

    public DonationServiceImpl(Driver qldbDriver, WalletService walletService) {
        this.qldbDriver = qldbDriver;
        this.walletService = walletService;
    }

    @Override
    public void insertDonation(DonationRequest donationRequest) {

        IonSystem ionSys = IonSystemBuilder.standard().build();

        qldbDriver.QldbDriver().execute(txn -> {
            Result result = txn.execute(
                    GET_WALLETS_BY_OWNER, ionSys.newString(donationRequest.getOwnerId()));
            //getDocumentId(result);
            if (!result.isEmpty()) {

                for (IonValue ionValue : result) {
                    IonStruct ionStruct;
                    ionStruct = (IonStruct) ionValue;

                    //Check wallet types and If exist
                    if ((donationRequest.getDistribution().getOverheads() != null)
                            && (((IonString) ionStruct.get(WALLET_TYPE)).stringValue())
                            .equalsIgnoreCase(IN_OVERHEADS)) {

                        BigDecimal updatedAvailable = new BigDecimal("0.00");
                        BigDecimal currentAvailable = ((IonDecimal) ionStruct.get(AVAILABLE_AMOUNT)).decimalValue();
                        String id = ((IonString) ionStruct.get(WALLET_ID)).stringValue();
                        updatedAvailable = currentAvailable.add(ionSys.newDecimal(donationRequest.getDistribution()
                                .getOverheads().getAmount()).decimalValue());
                        List<IonValue> parameters = new ArrayList<>();
                        parameters.add(ionSys.newDecimal(updatedAvailable));
                        parameters.add(ionSys.newString(id));
                        //updateWallet(parameters);
                        txn.execute(UPDATE_WALLET, parameters);

                        //Get wallet doc ID
                        String docId = getWalletDocID(txn, IN_OVERHEADS, donationRequest.getOwnerId());

                        //Revenue Initiate
                        insertRevenue(donationRequest, docId);

                    } else {
                        Result result1 = txn.execute(WALLET_BY_ID_AND_TYPE, ionSys.newString(donationRequest.getOwnerId()),
                                ionSys.newString(IN_OVERHEADS));
                        if (result1.isEmpty()) {
                            BigDecimal availableAmount = donationRequest.getDistribution().getOverheads().getAmount();
                            String docId = createWallet(txn, donationRequest, IN_OVERHEADS, availableAmount);
                            insertRevenue(donationRequest, docId);
                        }
                    }

                    if ((donationRequest.getDistribution().getDevelopment() != null)
                            && (((IonString) ionStruct.get(WALLET_TYPE)).stringValue())
                            .equalsIgnoreCase(IN_DEVELOPMENT)) {

                        BigDecimal updatedAvailable = new BigDecimal("0.00");
                        BigDecimal currentAvailable = ((IonDecimal) ionStruct.get(AVAILABLE_AMOUNT)).decimalValue();
                        updatedAvailable = currentAvailable.add(ionSys.newDecimal(donationRequest.getDistribution()
                                .getDevelopment().getAmount()).decimalValue());
                        String id = ((IonString) ionStruct.get(WALLET_ID)).stringValue();
                        List<IonValue> parameters = new ArrayList<>();
                        parameters.add(ionSys.newDecimal(updatedAvailable));
                        parameters.add(ionSys.newString(id));
                        txn.execute(UPDATE_WALLET, parameters);
                        //Get wallet doc ID
                        String docId = getWalletDocID(txn, IN_DEVELOPMENT, donationRequest.getOwnerId());
                        //Revenue Initiate
                        insertRevenue(donationRequest, docId);

                    } else {
                        Result result1 = txn.execute(WALLET_BY_ID_AND_TYPE, ionSys.newString(donationRequest.getOwnerId()),
                                ionSys.newString(IN_DEVELOPMENT));
                        if (result1.isEmpty()) {
                            BigDecimal availableAmount = donationRequest.getDistribution().getDevelopment().getAmount();
                            String docId = createWallet(txn, donationRequest, IN_DEVELOPMENT, availableAmount);
                            insertRevenue(donationRequest, docId);
                        }
                    }

                    if ((donationRequest.getDistribution().getMissions() != null)
                            && (((IonString) ionStruct.get(WALLET_TYPE)).stringValue())
                            .equalsIgnoreCase(IN_MISSION)) {
                        List<Missions> missions = donationRequest.getDistribution().getMissions();
                        for (Missions m : missions) {
                            if ((((IonString) ionStruct.get(MISSION_ID)).stringValue())
                                    .equalsIgnoreCase(m.getMissionId())) {
                                BigDecimal updatedAvailable = new BigDecimal("0.00");
                                BigDecimal currentAvailable = ((IonDecimal) ionStruct.get(AVAILABLE_AMOUNT)).decimalValue();
                                updatedAvailable = currentAvailable.add(ionSys.newDecimal(m.getAmount()).decimalValue());
                                String id = ((IonString) ionStruct.get(WALLET_ID)).stringValue();
                                List<IonValue> parameters = new ArrayList<>();
                                parameters.add(ionSys.newDecimal(updatedAvailable));
                                parameters.add(ionSys.newString(id));
                                txn.execute(UPDATE_WALLET, parameters);

                                String docId = getWalletDocIDByMissionID(txn, IN_OVERHEADS, donationRequest.getOwnerId(), m.getMissionId());
                                insertRevenue(donationRequest, docId);

                            } else {
                                Result result1 = txn.execute(WALLET_BY_ID_AND_MISSION, ionSys.newString(donationRequest.getOwnerId()),
                                        ionSys.newString(m.getMissionId()));

                                if (result1.isEmpty()) {
                                    BigDecimal availableAmount = m.getAmount();
                                    String missionID = m.getMissionId();
                                    String docId = createWalletWithMissionID(txn, donationRequest, IN_MISSION, availableAmount, missionID);
                                    insertRevenue(donationRequest, docId);
                                }

                            }

                        }

                    } else {
                        Result result1 = txn.execute(WALLET_BY_ID_AND_TYPE, ionSys.newString(donationRequest.getOwnerId()),
                                ionSys.newString(IN_MISSION));
                        if (/*isExecuted.get() && */result1.isEmpty()) {

                            List<Missions> m = donationRequest.getDistribution().getMissions();
                            for (Missions missions : m) {

                                BigDecimal availableAmount = missions.getAmount();
                                String missionID = missions.getMissionId();
                                String docId = createWalletWithMissionID(txn, donationRequest, IN_MISSION, availableAmount, missionID);
                                insertRevenue(donationRequest, docId);
                            }
                        }
                    }
                }
            } else {
                if (donationRequest.getDistribution().getOverheads() != null) {
                    BigDecimal availableAmount = donationRequest.getDistribution().getOverheads().getAmount();
                    String docId = createWallet(txn, donationRequest, IN_OVERHEADS, availableAmount);
                    insertRevenue(donationRequest, docId);

                }

                if (donationRequest.getDistribution().getDevelopment() != null) {
                    BigDecimal availableAmount = donationRequest.getDistribution().getDevelopment().getAmount();
                    String docId = createWallet(txn, donationRequest, IN_DEVELOPMENT, availableAmount);
                    insertRevenue(donationRequest, docId);
                }
                if (!donationRequest.getDistribution().getMissions().isEmpty()) {
                    List<Missions> m = donationRequest.getDistribution().getMissions();
                    for (Missions missions : m) {
                        BigDecimal availableAmount = missions.getAmount();
                        String missionID = missions.getMissionId();
                        String docId = createWalletWithMissionID(txn, donationRequest, IN_MISSION, availableAmount, missionID);
                        insertRevenue(donationRequest, docId);
                    }
                }
            }
        });

    }

    public void insertRevenue(DonationRequest donationRequest, String docId) {
        String uniqueID = UUID.randomUUID().toString();
        IonSystem ionSys = IonSystemBuilder.standard().build();
        IonStruct revenueData = ionSys.newEmptyStruct();
        revenueData.put(REVENUE_ID).newString(uniqueID);
        revenueData.put(OWNER_ID).newString(donationRequest.getOwnerId());
        revenueData.put(CRM_OWNER_ID).newString(donationRequest.getCrmOwnerId());
        revenueData.put(TO_WALLET_ID).newString(docId);
        revenueData.put(AMOUNT).newDecimal(donationRequest.getTotalAmount());
        revenueData.put(CURRENCY).newString(donationRequest.getCurrency());
        revenueData.put(SOURCE).newString(donationRequest.getSource());
        revenueData.put(TRANSACTION_ID).newString(donationRequest.getTransactionId());
        revenueData.put(TIME_STAMP).newTimestamp(donationRequest.getTransactionTimestamp());
        revenueData.put(ORIGINAL_AMOUNT).newDecimal(donationRequest.getOriginalAmount());
        revenueData.put(ORIGINAL_CURRENCY).newString(donationRequest.getOriginalCurrency());
        revenueData.put(CURRENCY_CONVERSION).newDecimal(donationRequest.getCurrencyConversion());
        Result result = qldbDriver.QldbDriver().execute(txn -> {
            return txn.execute(INSERT_INTO_REVENUE, revenueData);
        });
    }


    public String createWalletWithMissionID(TransactionExecutor txn, DonationRequest donationRequest, String walletType,
                                            BigDecimal availableAmount, String missionID) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String uniqueID = UUID.randomUUID().toString();
        IonStruct walletData = ionSys.newEmptyStruct();
        walletData.put(WALLET_ID).newString(uniqueID);
        walletData.put(OWNER_ID).newString(donationRequest.getOwnerId());
        walletData.put(CRM_OWNER_ID).newString(donationRequest.getCrmOwnerId());
        walletData.put(WALLET_TYPE).newString(walletType);
        walletData.put(MISSION_ID).newString(missionID);
        walletData.put(AVAILABLE_AMOUNT).newDecimal(availableAmount);
        walletData.put(TOTAL_AMOUNT).newDecimal(new BigDecimal(String.valueOf(donationRequest.getTotalAmount())));
        walletData.put(CURRENCY).newString(donationRequest.getCurrency());
        Result result = txn.execute(INSERT_INTO_WALLET, walletData);
        return getDocumentId(result);
    }

    public String createWallet(TransactionExecutor txn, DonationRequest donationRequest, String walletType, BigDecimal availableAmount) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String uniqueID = UUID.randomUUID().toString();
        IonStruct walletData = ionSys.newEmptyStruct();
        walletData.put(WALLET_ID).newString(uniqueID);
        walletData.put(OWNER_ID).newString(donationRequest.getOwnerId());
        walletData.put(CRM_OWNER_ID).newString(donationRequest.getCrmOwnerId());
        walletData.put(WALLET_TYPE).newString(walletType);
        walletData.put(AVAILABLE_AMOUNT).newDecimal(availableAmount);
        walletData.put(TOTAL_AMOUNT).newDecimal(new BigDecimal(String.valueOf(donationRequest.getTotalAmount())));
        walletData.put(CURRENCY).newString(donationRequest.getCurrency());
        Result result = txn.execute(INSERT_INTO_WALLET, walletData);
        return getDocumentId(result);
    }

    public String getWalletDocIDByMissionID(TransactionExecutor txn, String walletType, String ownerId, String missionID) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String docId = null;
        Result result1 = txn.execute(GET_WALLET_ID_MISSION_ID, ionSys.newString(walletType), ionSys.newString(ownerId), ionSys.newString(missionID));
        for (IonValue ionValues : result1) {
            IonStruct ionStruct1;
            ionStruct1 = (IonStruct) ionValues;
            docId = ((IonString) ionStruct1.get(ID)).stringValue();
        }
        return docId;
    }

    public String getZewWalletDocIdByType(TransactionExecutor txn, String walletType) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String docId = null;
        Result result1 = txn.execute(GET_ZEW_WALLET_DOC_ID, ionSys.newString(walletType));
        for (IonValue ionValues : result1) {
            IonStruct ionStruct1;
            ionStruct1 = (IonStruct) ionValues;
            docId = ((IonString) ionStruct1.get(ID)).stringValue();
        }
        return docId;
    }

    public String getWalletDocID(TransactionExecutor txn, String walletType, String ownerId) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String docId = null;
        Result result1 = txn.execute(GET_WALLET_ID, ionSys.newString(walletType), ionSys.newString(ownerId));
        for (IonValue ionValues : result1) {
            IonStruct ionStruct1;
            ionStruct1 = (IonStruct) ionValues;
            docId = ((IonString) ionStruct1.get(ID)).stringValue();
        }
        return docId;
    }

    public String getWalletDocIDByWalletId(TransactionExecutor txn, String walletID) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String docId = null;
        Result result1 = txn.execute(GET_WALLET_DOCID_BY_WALLET_ID, ionSys.newString(walletID));
        for (IonValue ionValues : result1) {
            IonStruct ionStruct1;
            ionStruct1 = (IonStruct) ionValues;
            docId = ((IonString) ionStruct1.get(ID)).stringValue();
        }
        return docId;
    }

    public String getDocumentId(Result result) {
        String documentId = null;
        Iterator<IonValue> iter = result.iterator();
        while (iter.hasNext()) {
            IonValue obj = iter.next();
            if (obj instanceof IonStruct) {
                IonStruct val = (IonStruct) obj;
                IonString str = (IonString) val.get(DOCUMENT_ID);
                documentId = str.stringValue();
                break;
            }
        }
        return documentId;
    }


    @Override
    public void useDonation(DonationUseRequest donationUseRequest) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        final BigDecimal[] avaAmount = {new BigDecimal(0)};
        final BigDecimal[] amount = {donationUseRequest.getAmount()};
        final BigDecimal[] updatedAvailable = {new BigDecimal("0.00")};
        final BigDecimal[] transactionAmount = {new BigDecimal("0.00")};
        final BigDecimal[] updatedZew = {new BigDecimal("0.00")};
        final String[] missionId = new String[1];

        qldbDriver.QldbDriver().execute(txn -> {
            String walletType = null;
            if (donationUseRequest.getCostCenter().equalsIgnoreCase(OVERHEADS)) {
                walletType = IN_OVERHEADS;
            } else if (donationUseRequest.getCostCenter().equalsIgnoreCase(DEVELOPMENT)) {
                walletType = IN_DEVELOPMENT;
            } else if (donationUseRequest.getCostCenter().equalsIgnoreCase(MISSION)) {
                walletType = IN_MISSION;
            }

            Result result = txn.execute(GET_ALL_WALLET_BY_TYPE, ionSys.newString(walletType));
            if (!result.isEmpty()) {
                for (IonValue ionValue : result) {
                    IonStruct ionStruct;
                    ionStruct = (IonStruct) ionValue;
                    //if(available amount < amount)
                    if (!("In_Mission".equalsIgnoreCase(walletType))) {
                        //Available 100, Amount 50

                        if (amount[0].compareTo(avaAmount[0]) > 0) {
                            avaAmount[0] = ((IonDecimal) ionStruct.get(AVAILABLE_AMOUNT)).decimalValue();
                            if (avaAmount[0].compareTo(amount[0]) > 0) {
                                updatedAvailable[0] = avaAmount[0].subtract(amount[0]);
                                updatedZew[0] = updatedZew[0].add(amount[0]);
                                amount[0] = amount[0].subtract(avaAmount[0]);
                                transactionAmount[0] = updatedAvailable[0];
                            } else {
                                amount[0] = amount[0].subtract(avaAmount[0]); // -50
                                updatedZew[0] = updatedZew[0].add(avaAmount[0]); // 50
                                transactionAmount[0] = avaAmount[0];
                                avaAmount[0] = new BigDecimal(0); //
                                updatedAvailable[0] = new BigDecimal(0);
                            }
                            String id = ((IonString) ionStruct.get(WALLET_ID)).stringValue();
                            List<IonValue> parameters = new ArrayList<>();
                            parameters.add(ionSys.newDecimal(updatedAvailable[0]));
                            parameters.add(ionSys.newString(id));
                            //updateWallet(parameters);
                            txn.execute(UPDATE_WALLET, parameters);

                            //Get In wallet Doc ID
                            String senderId = getWalletDocIDByWalletId(txn, id);

                            //Get Out wallet doc ID
                            String receiverDocID = getZewWalletDocIdByType(txn, walletType);

                            //Transaction  insert

                            createTransaction(txn, donationUseRequest, senderId, receiverDocID, transactionAmount[0]);


                        } else {
                            break;
                        }
                    } else {
                        Result resultMission = txn.execute(GET_ALL_WALLET_BY_MISSION, ionSys.newString(walletType), ionSys.newString(donationUseRequest.getMissionId()));

                        if (!resultMission.isEmpty()) {
                            for (IonValue ionValueMission : resultMission) {
                                IonStruct ionStructMission;
                                ionStructMission = (IonStruct) ionValueMission;
                                if (amount[0].compareTo(avaAmount[0]) > 0) {
                                    avaAmount[0] = ((IonDecimal) ionStructMission.get(AVAILABLE_AMOUNT)).decimalValue();
                                    //100, 50
                                    // 50, 100
                                    if (avaAmount[0].compareTo(amount[0]) > 0) {
                                        updatedAvailable[0] = avaAmount[0].subtract(amount[0]); //50
                                        updatedZew[0] = updatedZew[0].add(amount[0]); // 50
                                        amount[0] = amount[0].subtract(avaAmount[0]); // -50
                                        transactionAmount[0] = updatedAvailable[0];
                                    } else {
                                        amount[0] = amount[0].subtract(avaAmount[0]); // 50
                                        updatedZew[0] = updatedZew[0].add(avaAmount[0]); //50
                                        transactionAmount[0] = avaAmount[0];
                                        avaAmount[0] = new BigDecimal(0);
                                        updatedAvailable[0] = new BigDecimal(0);
                                    }
                                    String id = ((IonString) ionStructMission.get(WALLET_ID)).stringValue();
                                    missionId[0] = ((IonString) ionStructMission.get(MISSION_ID)).stringValue();
                                    List<IonValue> parameters = new ArrayList<>();
                                    parameters.add(ionSys.newDecimal(updatedAvailable[0]));
                                    parameters.add(ionSys.newString(id));
                                    //updateWallet(parameters);
                                    txn.execute(UPDATE_WALLET, parameters);

                                    String senderId = getWalletDocIDByWalletId(txn, id);

                                    //Get Out wallet doc ID
                                    String receiverDocID = getZewWalletDocIdByType(txn, walletType);

                                    //Transaction  insert

                                    createTransaction(txn, donationUseRequest, senderId, receiverDocID, transactionAmount[0]);

                                } else {
                                    break;
                                }
                            }
                        }
                    }

                }
            }
            updateZEW(txn, walletType, updatedZew[0], missionId[0]);
        });

    }

    public void updateZEW(TransactionExecutor txn, String walletType, BigDecimal updateZew, String missionId) {
        IonSystem ionSys = IonSystemBuilder.standard().build();

        String outWalletType = null;
        if (walletType.equalsIgnoreCase(IN_OVERHEADS)) {
            outWalletType = OUT_OVERHEADS;
        } else if (walletType.equalsIgnoreCase(IN_DEVELOPMENT)) {
            outWalletType = OUT_DEVELOPMENT;

        } else if (walletType.equalsIgnoreCase(IN_MISSION)) {
            outWalletType = OUT_MISSION;
        }
        if (!(OUT_MISSION.equalsIgnoreCase(outWalletType))) {
            Result result = txn.execute(GET_ALL_WALLET_BY_TYPE, ionSys.newString(outWalletType));

            if (!result.isEmpty()) {
                for (IonValue ionValue : result) {
                    IonStruct ionStruct;
                    ionStruct = (IonStruct) ionValue;
                    BigDecimal currentTotal = ((IonDecimal) ionStruct.get(TOTAL_AMOUNT)).decimalValue();
                    String id = ((IonString) ionStruct.get(WALLET_ID)).stringValue();
                    List<IonValue> parameters = new ArrayList<>();
                    parameters.add(ionSys.newDecimal(updateZew.add(currentTotal)));
                    parameters.add(ionSys.newString(id));
                    //updateWallet(parameters);
                    txn.execute(UPDATE_ZEW_WALLET, parameters);
                }
            }
        } else {

            Result resultMission = txn.execute(GET_ALL_WALLET_BY_MISSION, ionSys.newString(outWalletType), ionSys.newString(missionId));

            if (!resultMission.isEmpty()) {
                for (IonValue ionValueMissionU : resultMission) {
                    IonStruct ionStructU;
                    ionStructU = (IonStruct) ionValueMissionU;
                    BigDecimal currentTotal = ((IonDecimal) ionStructU.get(TOTAL_AMOUNT)).decimalValue();
                    String id = ((IonString) ionStructU.get(WALLET_ID)).stringValue();
                    List<IonValue> parameters = new ArrayList<>();
                    parameters.add(ionSys.newDecimal(updateZew.add(currentTotal)));
                    parameters.add(ionSys.newString(id));
                    //updateWallet(parameters);
                    txn.execute(UPDATE_ZEW_WALLET, parameters);
                }
            }
        }

    }

    public void createTransaction(TransactionExecutor txn, DonationUseRequest donationUseRequest, String senderId, String receiverDocID, BigDecimal amount) {
        IonSystem ionSys = IonSystemBuilder.standard().build();
        String uniqueID = UUID.randomUUID().toString();
        IonStruct transactionData = ionSys.newEmptyStruct();
        transactionData.put(TRANSACTION_ID1).newString(uniqueID);
        //transactionData.put("timestamp").newTimestamp();
        // In Wallet DOC ID
        transactionData.put(SENDER_WALLET_ID).newString(senderId);
        // ZEW WALLET Doc ID
        transactionData.put(RECEIVER_WALLET_ID).newString(receiverDocID);
        transactionData.put(AMOUNT).newDecimal(amount);
        transactionData.put(CURRENCY).newString(donationUseRequest.getCurrency());
        transactionData.put(SOURCE).newString(donationUseRequest.getSource());
        transactionData.put(EXPENSE_ID).newString(donationUseRequest.getExpenseId());
        transactionData.put(DESCRIPTION_CODE).newString(donationUseRequest.getDescriptionCode());
        transactionData.put(REVENUE_ID).newString(donationUseRequest.getRevenueId());
        transactionData.put(ORIGINAL_AMOUNT).newDecimal(donationUseRequest.getAmount());
        transactionData.put(ORIGINAL_CURRENCY).newString(donationUseRequest.getCurrency());
        //transactionData.put("currencyConversion").newDecimal(transaction.getCurrencyConversion());
        txn.execute(INSERT_INTO_TRANSACTION, transactionData);
    }

}
