package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.Constants;
import com.lightrail.model.api.Transaction;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.*;

public class GiftCharge extends GiftTransaction {


    private GiftCharge(Transaction codeTransactionResponse) {
        this.transactionResponse = codeTransactionResponse;
    }

    public int getAmount() {
        return 0 - transactionResponse.getValue();
    }

    static Map<String, Object> translateToLightrail(Map<String, Object> giftChargeParams) {
        giftChargeParams = GiftTransaction.translateToLightrail(giftChargeParams);
        Map<String, Object> translatedParams = new HashMap<>();

        for (String paramName : giftChargeParams.keySet()) {
            if (Constants.LightrailParameters.CAPTURE.equals(paramName)) {
                translatedParams.put(Constants.LightrailParameters.PENDING, !(Boolean) giftChargeParams.get(paramName));
            } else if (Constants.LightrailParameters.AMOUNT.equals(paramName)) {
                Integer transactionAmount = (Integer) giftChargeParams.get(paramName);
                Integer lightrailTransactionValue = 0 - transactionAmount;
                translatedParams.put(Constants.LightrailParameters.VALUE, lightrailTransactionValue);
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


    public void capture() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, getCapturingIdempotencyKey());
        capture(transactionParams);
    }

    public void cancel() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, getCancelingIdempotencyKey());
        cancel(transactionParams);
    }

    void cancel(Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {

        Transaction cancelTransaction = APICore.finalizeTransaction(getCardId(),
                getTransactionId(),
                Constants.LightrailAPI.Transactions.VOID,
                transactionParams);
        history.add(cancelTransaction);
    }

    void capture(Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Transaction captureTransaction = APICore.finalizeTransaction(getCardId(),
                getTransactionId(),
                Constants.LightrailAPI.Transactions.CAPTURE,
                transactionParams);
        history.add(captureTransaction);
    }


    public static GiftCharge createPendingByCardId(String cardId, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCardId(cardId, amount, currency, false);
    }
    public static GiftCharge createByCardId(String cardId, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCardId(cardId, amount, currency, true);
    }

    private static GiftCharge createByCardId(String cardId, int amount, String currency, boolean capture) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(Constants.LightrailParameters.CARD_ID, cardId);
        giftChargeParams.put(Constants.LightrailParameters.AMOUNT, amount);
        giftChargeParams.put(Constants.LightrailParameters.CURRENCY, currency);
        giftChargeParams.put(Constants.LightrailParameters.CAPTURE, capture);
        return create(giftChargeParams);
    }


    public static GiftCharge createPendingByCode(String code, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCode(code, amount, currency, false);
    }
    public static GiftCharge createByCode(String code, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return createByCode(code, amount, currency, true);
    }

    private static GiftCharge createByCode(String code, int amount, String currency, boolean capture) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(Constants.LightrailParameters.CODE, code);
        giftChargeParams.put(Constants.LightrailParameters.AMOUNT, amount);
        giftChargeParams.put(Constants.LightrailParameters.CURRENCY, currency);
        giftChargeParams.put(Constants.LightrailParameters.CAPTURE, capture);
        return create(giftChargeParams);
    }

    public static GiftCharge create(Map<String, Object> giftChargeParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), giftChargeParams);

        String code = (String) giftChargeParams.get(Constants.LightrailParameters.CODE);
        String cardId = (String) giftChargeParams.get(Constants.LightrailParameters.CARD_ID);

        if ((code == null || code.isEmpty())
                && (cardId == null || cardId.isEmpty()))
            throw new BadParameterException("Must provide either a gift code or a gift card id.");

        String idempotencyKey = (String) giftChargeParams.get(Constants.LightrailParameters.USER_SUPPLIED_ID);
        if (idempotencyKey == null) {
            idempotencyKey = UUID.randomUUID().toString();
            giftChargeParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, idempotencyKey);
        }

        if (!giftChargeParams.containsKey(Constants.LightrailParameters.CAPTURE))
            giftChargeParams.put(Constants.LightrailParameters.CAPTURE, true);

        Transaction transaction;
        if (code != null && !code.isEmpty())
            transaction = APICore.postTransactionOnCode(code, translateToLightrail(giftChargeParams));
        else
            transaction = APICore.postTransactionOnCard(cardId, translateToLightrail(giftChargeParams));

        return new GiftCharge(transaction);
    }

    private static GiftCharge retrieveByCodeAndIdempotencyKey(String code, String idempotencyKey) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        Transaction transaction = APICore.retrieveTransactionByCodeAndUserSuppliedId(code, idempotencyKey);
        if (transaction != null)
            return new GiftCharge(transaction);
        else
            return null;
    }

    public static GiftCharge retrieve(Map<String, Object> giftChargeParams) throws AuthorizationException, IOException, CouldNotFindObjectException {
        String code = (String) giftChargeParams.get(Constants.LightrailParameters.CODE);
        String idempotencyKey = (String) giftChargeParams.get(Constants.LightrailParameters.USER_SUPPLIED_ID);

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