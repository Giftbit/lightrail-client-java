package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.Constants;
import com.lightrail.model.api.Transaction;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GiftFund extends GiftTransaction {

    private GiftFund(Transaction cardTransactionResponse) {
        this.transactionResponse = cardTransactionResponse;
    }

    public int getAmount() {
        return transactionResponse.getValue();
    }

    public static GiftFund createByCardId(String cardId, int amount, String currency) throws BadParameterException, IOException, AuthorizationException, CouldNotFindObjectException {
        Map<String, Object> giftFundParams = new HashMap<>();
        giftFundParams.put(Constants.LightrailParameters.CARD_ID, cardId);
        giftFundParams.put(Constants.LightrailParameters.AMOUNT, amount);
        giftFundParams.put(Constants.LightrailParameters.CURRENCY, currency);
        return create(giftFundParams);
    }

    public static GiftFund create(Map<String, Object> giftFundParams) throws BadParameterException, IOException, AuthorizationException, CouldNotFindObjectException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.CARD_ID,
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), giftFundParams);

        String cardId = (String) giftFundParams.get(Constants.LightrailParameters.CARD_ID);
        if (!giftFundParams.containsKey(Constants.LightrailParameters.USER_SUPPLIED_ID))
            giftFundParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, UUID.randomUUID().toString());
        Transaction cardTransaction;
        try {
            cardTransaction = APICore.postTransactionOnCard(cardId, translateToLightrail(giftFundParams));
        } catch (InsufficientValueException e) {
            throw new RuntimeException(e);
        }
        return new GiftFund(cardTransaction);
    }

    static Map<String, Object> translateToLightrail(Map<String, Object> giftFundParams) {
        giftFundParams = GiftTransaction.translateToLightrail(giftFundParams);
        Map<String, Object> translatedParams = new HashMap<>();
        for (String paramName : giftFundParams.keySet()) {
            if (Constants.LightrailParameters.AMOUNT.equals(paramName)) {
                Integer transactionAmount = (Integer) giftFundParams.get(paramName);
                translatedParams.put(Constants.LightrailParameters.VALUE, transactionAmount);
            } else {
                translatedParams.put(paramName, giftFundParams.get(paramName));
            }
        }
        return translatedParams;
    }
}
