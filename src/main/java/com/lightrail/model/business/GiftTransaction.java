package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.Constants;
import com.lightrail.model.api.Transaction;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GiftTransaction {
    Transaction transactionResponse;
    List<Transaction> history = new ArrayList<>();


    public String getCardId() {
        return transactionResponse.getCardId();
    }

    public String getTransactionId() {
        return transactionResponse.getTransactionId();
    }

    public String getUserSuppliedId() {
        return transactionResponse.getUserSuppliedId();
    }

    public String getCodeLastFour() {
        return transactionResponse.getCodeLastFour();
    }

    public String getDateCreated() {
        return transactionResponse.getDateCreated();
    }

    public String getIdempotencyKey() {
        return transactionResponse.getUserSuppliedId();
    }

    public Map<String, Object> getMetadata() {
        return transactionResponse.getMetadata();
    }

    static Map<String, Object> translateToLightrail(Map<String, Object> giftChargeParams) {
        Map<String, Object> translatedParams = new HashMap<>();

        for (String paramName : giftChargeParams.keySet()) {
            if (!Constants.LightrailParameters.CODE.equals(paramName)
                    && ! Constants.LightrailParameters.CARD_ID.equals(paramName)) {
                translatedParams.put(paramName, giftChargeParams.get(paramName));
            }
        }
        return translatedParams;
    }
}
