package com.lightrail.model.business;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
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

    public static GiftFund create(Map<String, Object> giftFundParams) throws BadParameterException, IOException, InsufficientValueException, AuthorizationException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.CARD_ID,
                Constants.LightrailParameters.AMOUNT,
                Constants.LightrailParameters.CURRENCY
        ), giftFundParams);

        String cardId = (String) giftFundParams.get(Constants.LightrailParameters.CARD_ID);
        if (!giftFundParams.containsKey(Constants.LightrailParameters.USER_SUPPLIED_ID))
            giftFundParams.put(Constants.LightrailParameters.USER_SUPPLIED_ID, UUID.randomUUID().toString());

        Transaction cardTransaction = APICore.postTransactionOnCard(cardId, traslateToLightrail(giftFundParams));
        return new GiftFund(cardTransaction);
    }


    private static Map<String, Object> traslateToLightrail(Map<String, Object> giftChargeParams) {
        Map<String, Object> translatedParams = new HashMap<>();
        for (String paramName : giftChargeParams.keySet()) {
            if (Constants.LightrailParameters.AMOUNT.equals(paramName)) {
                Integer transactionAmount = (Integer) giftChargeParams.get(paramName);
                translatedParams.put(Constants.LightrailParameters.VALUE, transactionAmount);
            } else {
                translatedParams.put(paramName, giftChargeParams.get(paramName));
            }
        }
        return translatedParams;
    }

}
