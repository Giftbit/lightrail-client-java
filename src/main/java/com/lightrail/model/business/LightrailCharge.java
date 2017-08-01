package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.Transaction;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.*;

public class LightrailCharge extends LightrailTransaction {


    private LightrailCharge(Transaction codeTransactionResponse) {
        this.transactionResponse = codeTransactionResponse;
    }

    public int getAmount() {
        return 0 - transactionResponse.getValue();
    }

    static Map<String, Object> translateToLightrail(Map<String, Object> giftChargeParams) {
        giftChargeParams = LightrailTransaction.translateToLightrail(giftChargeParams);
        if (!giftChargeParams.containsKey(LightrailConstants.Parameters.CAPTURE))
            giftChargeParams.put(LightrailConstants.Parameters.CAPTURE, true);

        Map<String, Object> translatedParams = new HashMap<>();

        for (String paramName : giftChargeParams.keySet()) {
            if (LightrailConstants.Parameters.CAPTURE.equals(paramName)) {
                translatedParams.put(LightrailConstants.Parameters.PENDING, !(Boolean) giftChargeParams.get(paramName));
            } else if (LightrailConstants.Parameters.AMOUNT.equals(paramName)) {
                Integer transactionAmount = (Integer) giftChargeParams.get(paramName);
                Integer lightrailTransactionValue = 0 - transactionAmount;
                translatedParams.put(LightrailConstants.Parameters.VALUE, lightrailTransactionValue);
            } else {
                translatedParams.put(paramName, giftChargeParams.get(paramName));
            }
        }
        return translatedParams;
    }

    String getCapturingIdempotencyKey() {
        return getIdempotencyKey() + "-capture";
    }
    String getCancelingIdempotencyKey() {
        return getIdempotencyKey() + "-void";
    }
    String getRefundingIdempotencyKey() {
        return getIdempotencyKey() + "-refund";
    }

    public void refund () throws IOException, AuthorizationException, CouldNotFindObjectException, InsufficientValueException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, getRefundingIdempotencyKey());
        refund(transactionParams);
    }

    public void capture() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, getCapturingIdempotencyKey());
        capture(transactionParams);
    }

    public void cancel() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, getCancelingIdempotencyKey());
        cancel(transactionParams);
    }

    LightrailActionTransaction refund(Map<String, Object> transactionParams) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Transaction refundTransaction = APICore.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.REFUND,
                transactionParams);
        return new LightrailActionTransaction(refundTransaction);
    }

    LightrailActionTransaction cancel(Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {

        Transaction cancelTransaction = APICore.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.VOID,
                transactionParams);
        return new LightrailActionTransaction(cancelTransaction);
    }

    LightrailActionTransaction capture(Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Transaction captureTransaction = APICore.actionOnTransaction(getCardId(),
                getTransactionId(),
                LightrailConstants.API.Transactions.CAPTURE,
                transactionParams);
        return new LightrailActionTransaction(captureTransaction);
    }

    public static LightrailCharge createPendingByCustomer(String customerAccountId, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCustomer(customerAccountId, amount, currency, false);
    }

    public static LightrailCharge createByCustomer(String customerAccountId, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCustomer(customerAccountId, amount, currency, true);
    }

    private static LightrailCharge createByCustomer(String customerAccountId, int amount, String currency, boolean capture) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(LightrailConstants.Parameters.CUSTOMER, customerAccountId);
        giftChargeParams.put(LightrailConstants.Parameters.AMOUNT, amount);
        giftChargeParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        giftChargeParams.put(LightrailConstants.Parameters.CAPTURE, capture);
        return create(giftChargeParams);
    }

    public static LightrailCharge createPendingByCardId(String cardId, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCardId(cardId, amount, currency, false);
    }

    public static LightrailCharge createByCardId(String cardId, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCardId(cardId, amount, currency, true);
    }

    private static LightrailCharge createByCardId(String cardId, int amount, String currency, boolean capture) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
        giftChargeParams.put(LightrailConstants.Parameters.AMOUNT, amount);
        giftChargeParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        giftChargeParams.put(LightrailConstants.Parameters.CAPTURE, capture);
        return create(giftChargeParams);
    }

    public static LightrailCharge createPendingByCode(String code, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCode(code, amount, currency, false);
    }
    public static LightrailCharge createByCode(String code, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCode(code, amount, currency, true);
    }

    private static LightrailCharge createByCode(String code, int amount, String currency, boolean capture) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(LightrailConstants.Parameters.CODE, code);
        giftChargeParams.put(LightrailConstants.Parameters.AMOUNT, amount);
        giftChargeParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        giftChargeParams.put(LightrailConstants.Parameters.CAPTURE, capture);
        return create(giftChargeParams);
    }

    public static LightrailCharge create(Map<String, Object> giftChargeParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.AMOUNT,
                LightrailConstants.Parameters.CURRENCY
        ), giftChargeParams);

        giftChargeParams = LightrailTransaction.handleCustomer(giftChargeParams);

        String code = (String) giftChargeParams.get(LightrailConstants.Parameters.CODE);
        String cardId = (String) giftChargeParams.get(LightrailConstants.Parameters.CARD_ID);

        giftChargeParams = LightrailTransaction.addDefaultIdempotencyKeyIfNotProvided(giftChargeParams);

        Transaction transaction;
        if (code != null && !code.isEmpty())
            transaction = APICore.postTransactionOnCode(code, translateToLightrail(giftChargeParams));
        else if (cardId != null && !cardId.isEmpty())
            transaction = APICore.postTransactionOnCard(cardId, translateToLightrail(giftChargeParams));
        else
            throw new BadParameterException("Must provide either a 'code', a 'cardId', or a valid 'customer'.");

        return new LightrailCharge(transaction);
    }

    private static LightrailCharge retrieveByCodeAndIdempotencyKey(String code, String idempotencyKey) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        Transaction transaction = APICore.retrieveTransactionByCodeAndUserSuppliedId(code, idempotencyKey);
        if (transaction != null)
            return new LightrailCharge(transaction);
        else
            return null;
    }

    public static LightrailCharge retrieve(Map<String, Object> giftChargeParams) throws AuthorizationException, IOException, CouldNotFindObjectException {
        String code = (String) giftChargeParams.get(LightrailConstants.Parameters.CODE);
        String idempotencyKey = (String) giftChargeParams.get(LightrailConstants.Parameters.USER_SUPPLIED_ID);

        try {
            if (code != null && idempotencyKey != null) {
                return retrieveByCodeAndIdempotencyKey(code, idempotencyKey);
            } else {
                throw new BadParameterException("Not enough information to retrieve the gift charge."); // todo: more ways to retrieve
            }
        } catch (InsufficientValueException e) {//never happens
            throw new RuntimeException(e);
        }
    }
}