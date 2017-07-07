package com.lightrail.model.business;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.helpers.Constants;
import com.lightrail.model.api.Transaction;
import com.lightrail.helpers.Currency;
import com.lightrail.helpers.LightrailParameters;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.*;

public class GiftCharge {
    Transaction codeTransactionResponse;

    List<Transaction> history = new ArrayList<>();

    private GiftCharge(Transaction codeTransactionResponse) {
        this.codeTransactionResponse = codeTransactionResponse;
    }

    public String getCardId() {
        return codeTransactionResponse.getCardId();
    }

    public String getTransactionId() {
        return codeTransactionResponse.getTransactionId();
    }

    public String getUserSuppliedId() {
        return codeTransactionResponse.getUserSuppliedId();
    }

    public int getAmount() {
        return 0 - codeTransactionResponse.getValue();
    }

    public GiftCharge capture() throws IOException {
        return capture(new HashMap<String, Object>());
    }

    public GiftCharge capture(Map<String, Object> transactionParams) throws IOException {
        if (!transactionParams.containsKey(LightrailParameters.USER_SUPPLIED_ID))
            transactionParams.put(LightrailParameters.USER_SUPPLIED_ID, UUID.randomUUID().toString());

        Transaction captureTransaction = APICore.finalizeTransaction(getCardId(),
                getTransactionId(),
                Constants.LightrailAPI.Transactions.CAPTURE,
                transactionParams);
        history.add(codeTransactionResponse);
        codeTransactionResponse = captureTransaction;

        return this;
    }

    public GiftCharge cancel() throws IOException {
        return cancel(new HashMap<String, Object>());
    }

    public GiftCharge cancel(Map<String, Object> transactionParams) throws IOException {
        if (!transactionParams.containsKey(LightrailParameters.USER_SUPPLIED_ID))
            transactionParams.put(LightrailParameters.USER_SUPPLIED_ID, UUID.randomUUID().toString());

        Transaction cancelTransaction = APICore.finalizeTransaction(getCardId(),
                getTransactionId(),
                Constants.LightrailAPI.Transactions.VOID,
                transactionParams);
        history.add(codeTransactionResponse);
        codeTransactionResponse = cancelTransaction;

        return this;
    }

    private static Map<String, Object> traslateToLightrail(Map<String, Object> giftChargeParams) {
        Map<String, Object> translatedParams = new HashMap<>();
        for (String paramName : giftChargeParams.keySet()) {
            if (LightrailParameters.CAPTURE.equals(paramName)) {
                translatedParams.put(LightrailParameters.PENDING, !(Boolean) giftChargeParams.get(paramName));
            } else if (LightrailParameters.AMOUNT.equals(paramName)) {
                Integer transactionAmount = (Integer) giftChargeParams.get(paramName);
                Integer lightrailTransactionValue = 0 - transactionAmount;
                translatedParams.put(LightrailParameters.VALUE, lightrailTransactionValue);
            } else if (!LightrailParameters.CODE.equals(paramName)) {
                translatedParams.put(paramName, giftChargeParams.get(paramName));
            }
        }

        return translatedParams;
    }

    public static GiftCharge create(Map<String, Object> giftChargeParams) throws BadParameterException, IOException {
        LightrailParameters.requireParameters(Arrays.asList(
                LightrailParameters.CODE,
                LightrailParameters.AMOUNT,
                LightrailParameters.CURRENCY
                ), giftChargeParams);

        String code = (String) giftChargeParams.get(LightrailParameters.CODE);

        if (!giftChargeParams.containsKey(LightrailParameters.USER_SUPPLIED_ID))
            giftChargeParams.put(LightrailParameters.USER_SUPPLIED_ID, UUID.randomUUID().toString());

        if (!giftChargeParams.containsKey(LightrailParameters.CAPTURE))
            giftChargeParams.put(LightrailParameters.CAPTURE, true);


        Transaction codeTransaction = APICore.postTransactionOnCode(code, traslateToLightrail(giftChargeParams));

        return new GiftCharge(codeTransaction);
    }
}
