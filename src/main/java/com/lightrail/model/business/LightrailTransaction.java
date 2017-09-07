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

    public LightrailTransaction() {}

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

    public LightrailTransaction doVoid() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, getDefaultVoidUserSuppliedId());
        return doVoid(transactionParams);
    }

    public LightrailTransaction doVoid (Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        LightrailTransaction cancelTransaction = APICore.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.VOID,
                transactionParams,
                LightrailTransaction.class);
        return cancelTransaction;
    }

    public LightrailTransaction capture(Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        LightrailTransaction captureTransaction = APICore.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.CAPTURE,
                transactionParams,
                LightrailTransaction.class);
        return captureTransaction;
    }

    public LightrailTransaction refund(Map<String, Object> transactionParams) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        LightrailTransaction refundTransaction = APICore.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.REFUND,
                transactionParams,
                LightrailTransaction.class);
        return refundTransaction;
    }

    public static LightrailTransaction createPendingByContact(String contactId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByContact(contactId, value, currency, true);
    }

    public static LightrailTransaction createByContact(String contactId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByContact(contactId, value, currency, false);
    }

    private static LightrailTransaction createByContact(String contactId, int value, String currency, boolean pending) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(LightrailConstants.Parameters.CONTACT, contactId);
        giftChargeParams.put(LightrailConstants.Parameters.VALUE, value);
        giftChargeParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        giftChargeParams.put(LightrailConstants.Parameters.PENDING, pending);
        return create(giftChargeParams);
    }

    public static LightrailTransaction createPendingByCardId(String cardId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCardId(cardId, value, currency, true);
    }

    public static LightrailTransaction createByCardId(String cardId, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCardId(cardId, value, currency, false);
    }

    private static LightrailTransaction createByCardId(String cardId, int value, String currency, boolean pending) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
        giftChargeParams.put(LightrailConstants.Parameters.VALUE, value);
        giftChargeParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        giftChargeParams.put(LightrailConstants.Parameters.PENDING, pending);
        return create(giftChargeParams);
    }

    public static LightrailTransaction createPendingByCode(String code, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCode(code, value, currency, true);
    }
    public static LightrailTransaction createByCode(String code, int value, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCode(code, value, currency, false);
    }

    private static LightrailTransaction createByCode(String code, int value, String currency, boolean pending) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(LightrailConstants.Parameters.CODE, code);
        giftChargeParams.put(LightrailConstants.Parameters.VALUE, value);
        giftChargeParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        giftChargeParams.put(LightrailConstants.Parameters.PENDING, pending);
        return create(giftChargeParams);
    }

    public static LightrailTransaction create(Map<String, Object> giftChargeParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.VALUE,
                LightrailConstants.Parameters.CURRENCY
        ), giftChargeParams);

        giftChargeParams = ContactHandler.handleContact(giftChargeParams);

        String code = (String) giftChargeParams.remove(LightrailConstants.Parameters.CODE);
        String cardId = (String) giftChargeParams.remove(LightrailConstants.Parameters.CARD_ID);

        giftChargeParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(giftChargeParams);

        LightrailTransaction transaction;
        if (code != null && !code.isEmpty())
            transaction = APICore.postTransactionOnCode(code, giftChargeParams, LightrailTransaction.class);
        else if (cardId != null && !cardId.isEmpty())
            transaction = APICore.postTransactionOnCard(cardId, giftChargeParams, LightrailTransaction.class);
        else
            throw new BadParameterException("Must provide either a 'code', a 'cardId', or a valid 'contact'.");

        return transaction;
    }

    public static Transaction retrieveByCardAndUserSuppliedId(String cardId, String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        return APICore.retrieveTransactionByCardAndUserSuppliedId(cardId, userSuppliedId);
    }

    public static Transaction retrieveByCodeAndUserSuppliedId(String code, String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        return APICore.retrieveTransactionByCodeAndUserSuppliedId(code, userSuppliedId);
    }

    public static Transaction retrieve(Map<String, Object> giftChargeParams) throws AuthorizationException, IOException, CouldNotFindObjectException {
        String code = (String) giftChargeParams.get(LightrailConstants.Parameters.CODE);
        String userSuppliedId = (String) giftChargeParams.get(LightrailConstants.Parameters.USER_SUPPLIED_ID);

        try {
            if (code != null && userSuppliedId != null) {
                return retrieveByCodeAndUserSuppliedId(code, userSuppliedId);
            } else {
                throw new BadParameterException("Not enough information to retrieve the gift charge."); // todo: more ways to retrieve
            }
        } catch (InsufficientValueException e) {//never happens
            throw new RuntimeException(e);
        }
    }

}
