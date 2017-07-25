package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.model.api.Balance;
import com.lightrail.model.api.ValueStore;
import com.lightrail.helpers.*;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.*;

public class GiftValue {

    private Balance balanceResponse;

    public String getCurrency() {
        return balanceResponse.getCurrency();
    }

    public String getBalanceDate() {
        return balanceResponse.getBalanceDate();
    }

    public int getCurrentValue() {
        int currentValue = 0;

        String codeState = balanceResponse.getPrincipal().getState();

        if (!Objects.equals(codeState, Constants.LightrailAPI.CodeBalanceCheck.ACTIVE))
            throw new CardNotActiveException("This gift code is not active at this time.");

        currentValue = balanceResponse.getPrincipal().getCurrentValue();
        List<ValueStore> attachedValues = balanceResponse.getAttached();
        if (attachedValues != null) {
            for (ValueStore attachedValue : attachedValues) {
                String attachedValueState = attachedValue.getState();
                if (Objects.equals(attachedValueState, Constants.LightrailAPI.CodeBalanceCheck.ACTIVE))
                    currentValue += attachedValue.getCurrentValue();
            }
        }
        return currentValue;
    }

    private GiftValue(Balance balance) {
        this.balanceResponse = balance;
    }

    public static GiftValue retrieveByCode(String code) throws IOException, CurrencyMismatchException, BadParameterException, AuthorizationException, CouldNotFindObjectException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(Constants.LightrailParameters.CODE, code);
        return retrieve(giftValueParams);
    }

    public static GiftValue retrieveByCardId(String cardId) throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(Constants.LightrailParameters.CARD_ID, cardId);
        return retrieve(giftValueParams);
    }

    public static GiftValue retrieve(Map<String, Object> giftValueParams) throws IOException, CurrencyMismatchException, BadParameterException, AuthorizationException, CouldNotFindObjectException {

        String code = (String) giftValueParams.get(Constants.LightrailParameters.CODE);
        String cardId = (String) giftValueParams.get(Constants.LightrailParameters.CARD_ID);

        if ((code == null || code.isEmpty()) && (cardId == null || cardId.isEmpty()))
            throw new BadParameterException("Must provide either a gift code or a gift card id.");

        String requestedCurrency = (String) giftValueParams.get(Constants.LightrailParameters.CURRENCY);

        Balance balance;
        try {
            if (code != null) {
                balance = APICore.balanceCheckByCode(code);
            } else {
                balance = APICore.balanceCheckByCardId(cardId);
            }
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }

        String codeCurrency = balance.getCurrency();
        if (requestedCurrency != null & !Objects.equals(codeCurrency, requestedCurrency))
            throw new CurrencyMismatchException(String.format("Currency mismatch. Seeking %s value on a %s gift code.",
                    giftValueParams.get(Constants.LightrailParameters.CURRENCY),
                    codeCurrency));
        return new GiftValue(balance);
    }
}
