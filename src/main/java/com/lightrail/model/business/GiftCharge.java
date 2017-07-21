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

    List<Transaction> history = new ArrayList<>();

    private GiftCharge(Transaction codeTransactionResponse) {
        this.transactionResponse = codeTransactionResponse;
    }

    public int getAmount() {
        return 0 - transactionResponse.getValue();
    }

    public Map<String, Object> getMetadata() {
        return transactionResponse.getMetadata();
    }

    public String getIdempotencyKey() {
        return transactionResponse.getUserSuppliedId();
    }

    private String getCapturingIdempotencyKey() {
        return getIdempotencyKey() + "-capture";
    }


    public GiftCharge capture() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, getCapturingIdempotencyKey());
        return capture(transactionParams);
    }

    private GiftCharge capture(Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Transaction captureTransaction = APICore.finalizeTransaction(getCardId(),
                getTransactionId(),
                Constants.LightrailAPI.Transactions.CAPTURE,
                transactionParams);
        history.add(captureTransaction);
        return this;
    }

    private String getCancelingIdempotencyKey() {
        return getIdempotencyKey() + "-void";
    }

    public GiftCharge cancel() throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {
        Map<String, Object> transactionParams = new HashMap<>();
        transactionParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, getCancelingIdempotencyKey());
        return cancel(transactionParams);
    }

    private GiftCharge cancel(Map<String, Object> transactionParams) throws IOException, AuthorizationException, InsufficientValueException, CouldNotFindObjectException {

        Transaction cancelTransaction = APICore.finalizeTransaction(getCardId(),
                getTransactionId(),
                Constants.LightrailAPI.Transactions.VOID,
                transactionParams);
        history.add(cancelTransaction);
        return this;
    }

    private static Map<String, Object> translateToLightrail(Map<String, Object> giftChargeParams) {
        Map<String, Object> translatedParams = new HashMap<>();
        for (String paramName : giftChargeParams.keySet()) {
            if (Constants.LightrailParameters.CAPTURE.equals(paramName)) {
                translatedParams.put(Constants.LightrailParameters.PENDING, !(Boolean) giftChargeParams.get(paramName));
            } else if (Constants.LightrailParameters.AMOUNT.equals(paramName)) {
                Integer transactionAmount = (Integer) giftChargeParams.get(paramName);
                Integer lightrailTransactionValue = 0 - transactionAmount;
                translatedParams.put(Constants.LightrailParameters.VALUE, lightrailTransactionValue);
            } else if (!Constants.LightrailParameters.CODE.equals(paramName)) {
                translatedParams.put(paramName, giftChargeParams.get(paramName));
            }
        }
        return translatedParams;
    }

    public static GiftCharge create(String code, int amount, String currency) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        return create(code, amount, currency, true);
    }

    public static GiftCharge create(String code, int amount, String currency, boolean capture) throws AuthorizationException, CouldNotFindObjectException, InsufficientValueException, IOException {
        Map<String, Object> giftChargeParams = new HashMap<>();
        giftChargeParams.put(Constants.LightrailParameters.CODE, code);
        giftChargeParams.put(Constants.LightrailParameters.AMOUNT, amount);
        giftChargeParams.put(Constants.LightrailParameters.CURRENCY, currency);
        giftChargeParams.put(Constants.LightrailParameters.CAPTURE, capture);
        return create(giftChargeParams);
    }

    public static GiftCharge create(Map<String, Object> giftChargeParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.CODE,
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), giftChargeParams);

        String code = (String) giftChargeParams.get(Constants.LightrailParameters.CODE);
        String idempotencyKey = (String) giftChargeParams.get(Constants.LightrailParameters.USER_SUPPLIED_ID);

        if (idempotencyKey == null) {
            idempotencyKey = UUID.randomUUID().toString();
            giftChargeParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, idempotencyKey);
        }

        if (!giftChargeParams.containsKey(Constants.LightrailParameters.CAPTURE))
            giftChargeParams.put(Constants.LightrailParameters.CAPTURE, true);

        Transaction codeTransaction = APICore.postTransactionOnCode(code, translateToLightrail(giftChargeParams));

        return new GiftCharge(codeTransaction);
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