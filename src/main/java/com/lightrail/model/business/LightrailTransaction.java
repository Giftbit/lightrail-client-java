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

public abstract class LightrailTransaction {
    Transaction transactionResponse;

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

    public String getTransactionType() {
        return transactionResponse.getTransactionType();
    }

    public Map<String, Object> getMetadata() {
        return transactionResponse.getMetadata();
    }

    static Map<String, Object> translateToLightrail(Map<String, Object> giftChargeParams) {
        Map<String, Object> translatedParams = new HashMap<>();

        for (String paramName : giftChargeParams.keySet()) {
            if (!LightrailConstants.Parameters.CODE.equals(paramName)
                    && ! LightrailConstants.Parameters.CARD_ID.equals(paramName)) {
                translatedParams.put(paramName, giftChargeParams.get(paramName));
            }
        }
        return translatedParams;
    }

    public static Map<String, Object> handleCustomer(Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> chargeParamsCopy = new HashMap<>(params);

        String customerAccountId = (String) chargeParamsCopy.get(LightrailConstants.Parameters.CUSTOMER);
        String requestedCurrency = (String) chargeParamsCopy.get(LightrailConstants.Parameters.CURRENCY);

        if (customerAccountId != null) {
            if (requestedCurrency != null && !requestedCurrency.isEmpty()) {
                CustomerAccount account = CustomerAccount.retrieve(customerAccountId);
                String cardId = account.getCardFor(requestedCurrency).getCardId();
                chargeParamsCopy.remove(LightrailConstants.Parameters.CUSTOMER);
                chargeParamsCopy.put(LightrailConstants.Parameters.CARD_ID, cardId);
            } else {
                throw new BadParameterException("Must provide a valid 'currency' when using 'lightrailCustomer'.");
            }
        }
        return chargeParamsCopy;
    }

    public static Map<String, Object> addDefaultIdempotencyKeyIfNotProvided(Map<String, Object> params) {
        Map<String, Object> paramsCopy = new HashMap<>(params);
        String idempotencyKey = (String) paramsCopy.get(LightrailConstants.Parameters.USER_SUPPLIED_ID);
        if (idempotencyKey == null) {
            idempotencyKey = UUID.randomUUID().toString();
            paramsCopy.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, idempotencyKey);
        }
        return paramsCopy;
    }

}
