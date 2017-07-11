package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
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

    public GiftCharge capture() throws IOException, InsufficientValueException, AuthorizationException {
        return capture(new HashMap<String, Object>());
    }

    public GiftCharge capture(Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException {
        if (!transactionParams.containsKey(Constants.LightrailParameters.USER_SUPPLIED_ID))
            transactionParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, UUID.randomUUID().toString());

        Transaction captureTransaction = APICore.finalizeTransaction(getCardId(),
                getTransactionId(),
                Constants.LightrailAPI.Transactions.CAPTURE,
                transactionParams);
        history.add(captureTransaction);

        return this;
    }

    public GiftCharge cancel() throws IOException, InsufficientValueException, AuthorizationException {
        return cancel(new HashMap<String, Object>());
    }

    public GiftCharge cancel(Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException {
        if (!transactionParams.containsKey(Constants.LightrailParameters.USER_SUPPLIED_ID))
            transactionParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, UUID.randomUUID().toString());

        Transaction cancelTransaction = APICore.finalizeTransaction(getCardId(),
                getTransactionId(),
                Constants.LightrailAPI.Transactions.VOID,
                transactionParams);
        history.add(cancelTransaction);

        return this;
    }

    private static Map<String, Object> traslateToLightrail(Map<String, Object> giftChargeParams) {
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

    public static GiftCharge create(Map<String, Object> giftChargeParams) throws IOException, InsufficientValueException, AuthorizationException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.CODE,
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), giftChargeParams);

        String code = (String) giftChargeParams.get(Constants.LightrailParameters.CODE);

        if (!giftChargeParams.containsKey(Constants.LightrailParameters.USER_SUPPLIED_ID))
            giftChargeParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, UUID.randomUUID().toString());

        if (!giftChargeParams.containsKey(Constants.LightrailParameters.CAPTURE))
            giftChargeParams.put(Constants.LightrailParameters.CAPTURE, true);


        Transaction codeTransaction = APICore.postTransactionOnCode(code, traslateToLightrail(giftChargeParams));

        return new GiftCharge(codeTransaction);
    }
}
