package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.net.APICore;
import com.lightrail.model.api.objects.Transaction;

import java.io.IOException;
import java.util.*;

public class LightrailTransaction extends Transaction {

    public LightrailTransaction(Transaction transaction) {
        super(transaction.getRawJson());
    }

    String getDefaultCaptureUserSuppliedId() {
        return getUserSuppliedId() + "-capture";
    }
    String getDefaultVoidUserSuppliedId() {
        return getUserSuppliedId() + "-void";
    }
    String getDefaultRefundUserSuppliedId() {
        return getUserSuppliedId() + "-refund";
    }

    public LightrailTransaction refund () throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, getDefaultRefundUserSuppliedId());
        return refund(transactionParams);
    }

    public LightrailTransaction capture() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, getDefaultCaptureUserSuppliedId());
        return capture(transactionParams);
    }

    public LightrailTransaction capture(Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Transaction captureTransaction = APICore.Transactions.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.CAPTURE,
                transactionParams);
        return new LightrailTransaction(captureTransaction);
    }

    public LightrailTransaction doVoid() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, getDefaultVoidUserSuppliedId());
        return doVoid(transactionParams);
    }

    public LightrailTransaction doVoid (Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Transaction cancelTransaction = APICore.Transactions.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.VOID,
                transactionParams);
        return new LightrailTransaction(cancelTransaction);
    }

    public LightrailTransaction refund(Map<String, Object> transactionParams) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Transaction refundTransaction = APICore.Transactions.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.REFUND,
                transactionParams);
        return new LightrailTransaction(refundTransaction);
    }

    public static LightrailTransaction createPendingByContact(String contactId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByContact(contactId, value, currency, true);
    }

    public static LightrailTransaction createByContact(String contactId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByContact(contactId, value, currency, false);
    }

    private static LightrailTransaction createByContact(String contactId, int value, String currency, boolean pending) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.CONTACT, contactId);
        transactionParams.put(LightrailConstants.Parameters.VALUE, value);
        transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
        return create(transactionParams);
    }

    public static LightrailTransaction createPendingByCardId(String cardId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCardId(cardId, value, currency, true);
    }

    public static LightrailTransaction createByCardId(String cardId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCardId(cardId, value, currency, false);
    }

    public static LightrailTransaction simulateByCardId(String cardId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
        transactionParams.put(LightrailConstants.Parameters.VALUE, value);
        transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        return simulate(transactionParams);
    }

    private static LightrailTransaction createByCardId(String cardId, int value, String currency, boolean pending) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
        transactionParams.put(LightrailConstants.Parameters.VALUE, value);
        transactionParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        transactionParams.put(LightrailConstants.Parameters.PENDING, pending);
        return create(transactionParams);
    }

    public static LightrailTransaction createPendingByCode(String code, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCode(code, value, currency, true);
    }

    public static LightrailTransaction createByCode(String code, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCode(code, value, currency, false);
    }

    public static LightrailTransaction simulateByCode(String code, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(LightrailConstants.Parameters.CODE, code);
        giftChargeParams.put(LightrailConstants.Parameters.VALUE, value);
        giftChargeParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        return simulate(giftChargeParams);
    }

    private static LightrailTransaction createByCode(String code, int value, String currency, boolean pending) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(LightrailConstants.Parameters.CODE, code);
        giftChargeParams.put(LightrailConstants.Parameters.VALUE, value);
        giftChargeParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        giftChargeParams.put(LightrailConstants.Parameters.PENDING, pending);
        return create(giftChargeParams);
    }

    public static LightrailTransaction simulate(Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        if (!transactionParams.containsKey(LightrailConstants.Parameters.NSF)) {
            HashMap<String, Object> newParams = new HashMap<>(transactionParams);
            newParams.put(LightrailConstants.Parameters.NSF, false);
            return create(newParams, true);
        } else {
            return create(transactionParams, true);
        }
    }

    public static LightrailTransaction create(Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        return create(transactionParams, false);
    }

    private static LightrailTransaction create(Map<String, Object> transactionParams, boolean simulate) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.VALUE,
                LightrailConstants.Parameters.CURRENCY
        ), transactionParams);

        transactionParams = LightrailCustomerAccount.handleContact(transactionParams);

        String code = (String) transactionParams.remove(LightrailConstants.Parameters.CODE);
        String cardId = (String) transactionParams.remove(LightrailConstants.Parameters.CARD_ID);

        transactionParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(transactionParams);

        LightrailTransaction transaction;
        if (code != null && !code.isEmpty()) {
            if (simulate) {
                transaction = new LightrailTransaction(APICore.Transactions.simulateTransactionByCode(code, transactionParams));
            } else {
                transaction = new LightrailTransaction(APICore.Transactions.createTransactionByCode(code, transactionParams));
            }
        }
        else if (cardId != null && !cardId.isEmpty()) {
            if (simulate) {
                transaction = new LightrailTransaction(APICore.Transactions.simulateTransactionByCard(cardId, transactionParams));
            } else {
                transaction = new LightrailTransaction(APICore.Transactions.createTransactionByCard(cardId, transactionParams));
            }
        }
        else
            throw new BadParameterException("Must provide either a 'code', a 'cardId', or a valid 'contact'.");

        return transaction;
    }

    public static LightrailTransaction retrieveByCardIdAndUserSuppliedId(String cardId, String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        return new LightrailTransaction(APICore.Transactions.retrieveTransactionByCardIdAndUserSuppliedId(cardId, userSuppliedId));
    }

    public static LightrailTransaction retrieveByCardIdAndTransactionId(String cardId, String transactionId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        return new LightrailTransaction(APICore.Transactions.retrieveTransactionByCardIdAndTransactionId(cardId, transactionId));
    }

    public static LightrailTransaction retrieveByCodeAndUserSuppliedId(String code, String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        return new LightrailTransaction(APICore.Transactions.retrieveTransactionByCodeAndUserSuppliedId(code, userSuppliedId));
    }
    public static LightrailTransaction retrieveByCodeAndTransactionId(String code, String transactionId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        return new LightrailTransaction(APICore.Transactions.retrieveTransactionByCodeAndTransactionId(code, transactionId));
    }
    public static LightrailTransaction retrieve(Map<String, Object> transactionParams) throws AuthorizationException, IOException, CouldNotFindObjectException {
        String code = (String) transactionParams.get(LightrailConstants.Parameters.CODE);
        String cardId = (String) transactionParams.get(LightrailConstants.Parameters.CARD_ID);
        String transactionId = (String) transactionParams.get(LightrailConstants.Parameters.TRANSACTION_ID);
        String userSuppliedId = (String) transactionParams.get(LightrailConstants.Parameters.USER_SUPPLIED_ID);

        try {
            if (code != null && userSuppliedId != null) {
                return retrieveByCodeAndUserSuppliedId(code, userSuppliedId);
            } else if (cardId != null && userSuppliedId != null){
                return retrieveByCardIdAndUserSuppliedId(cardId, userSuppliedId);
            } else if (code != null && transactionId != null){
                return retrieveByCardIdAndTransactionId(cardId, transactionId);
            }else if (cardId != null && transactionId != null){
                return retrieveByCardIdAndTransactionId(cardId, transactionId);
            }else {
                throw new BadParameterException("Not enough information to retrieve the transaction."); // todo: more ways to retrieve
            }
        } catch (InsufficientValueException e) {//never happens
            throw new RuntimeException(e);
        }
    }

}
