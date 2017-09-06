package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.api.objects.Transaction;
import com.lightrail.model.api.net.APICore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LightrailFund extends LightrailTransaction {

    private LightrailFund(Transaction cardTransactionResponse) {
        this.transactionResponse = cardTransactionResponse;
    }

    public int getAmount() {
        return transactionResponse.getValue();
    }

    public static LightrailFund createByCardId(String cardId, int amount, String currency) throws BadParameterException, IOException, AuthorizationException, CouldNotFindObjectException {
        Map<String, Object> giftFundParams = new HashMap<>();
        giftFundParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
        giftFundParams.put(LightrailConstants.Parameters.AMOUNT, amount);
        giftFundParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        return create(giftFundParams);
    }

    public static LightrailFund createByCustomer(String customerAccountId, int amount, String currency) throws BadParameterException, IOException, AuthorizationException, CouldNotFindObjectException {
        Map<String, Object> giftFundParams = new HashMap<>();
        giftFundParams.put(LightrailConstants.Parameters.CUSTOMER, customerAccountId);
        giftFundParams.put(LightrailConstants.Parameters.AMOUNT, amount);
        giftFundParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        return create(giftFundParams);
    }

    public static LightrailFund create(Map<String, Object> giftFundParams) throws BadParameterException, IOException, AuthorizationException, CouldNotFindObjectException {
        LightrailConstants.Parameters.requireParameters(Arrays.asList(
                LightrailConstants.Parameters.AMOUNT,
                LightrailConstants.Parameters.CURRENCY
        ), giftFundParams);
        giftFundParams = LightrailTransaction.handleCustomer(giftFundParams);

        String cardId = (String) giftFundParams.get(LightrailConstants.Parameters.CARD_ID);
        if ((cardId == null || cardId.isEmpty()))
            throw new BadParameterException("Must provide either a 'cardId' or a valid 'customer'.");

        giftFundParams = LightrailConstants.Parameters.addDefaultUserSuppliedIdIfNotProvided(giftFundParams);
        Transaction cardTransaction;
        try {
            cardTransaction = APICore.postTransactionOnCard(cardId, translateToLightrail(giftFundParams));
        } catch (InsufficientValueException e) {
            throw new RuntimeException(e);
        }
        return new LightrailFund(cardTransaction);
    }

    static Map<String, Object> translateToLightrail(Map<String, Object> giftFundParams) {
        giftFundParams = LightrailTransaction.translateToLightrail(giftFundParams);
        Map<String, Object> translatedParams = new HashMap<>();
        for (String paramName : giftFundParams.keySet()) {
            if (LightrailConstants.Parameters.AMOUNT.equals(paramName)) {
                Integer transactionAmount = (Integer) giftFundParams.get(paramName);
                translatedParams.put(LightrailConstants.Parameters.VALUE, transactionAmount);
            } else {
                translatedParams.put(paramName, giftFundParams.get(paramName));
            }
        }
        return translatedParams;
    }
}
