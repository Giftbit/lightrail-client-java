package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.model.api.objects.ValueStore;
import com.lightrail.helpers.*;
import com.lightrail.model.api.objects.Balance;
import com.lightrail.model.api.net.APICore;

import java.io.IOException;
import java.util.*;

public class LightrailValue {

    private Balance balanceResponse;

    public String getCurrency() {
        return balanceResponse.getCurrency();
    }

    public String getBalanceDate() {
        return balanceResponse.getBalanceDate();
    }

    public String getExpires() {
        return balanceResponse.getPrincipal().getExpires();
    }

    public String getStartDate() {
        return balanceResponse.getPrincipal().getStartDate();
    }

    public String getState() {
        return balanceResponse.getPrincipal().getState();
    }

    public String getCardId() {
        return balanceResponse.getCardId();
    }

    Balance getBalanceResponse() {
        return balanceResponse;
    }

    public int getCurrentValue() {
        int currentValue = 0;

        String codeState = balanceResponse.getPrincipal().getState();

        if (!Objects.equals(codeState, LightrailConstants.API.Balance.ACTIVE))
            throw new CardNotActiveException("This gift code is not active at this time.");

        currentValue = balanceResponse.getPrincipal().getCurrentValue();
        List<ValueStore> attachedValues = balanceResponse.getAttached();
        if (attachedValues != null) {
            for (ValueStore attachedValue : attachedValues) {
                String attachedValueState = attachedValue.getState();
                if (Objects.equals(attachedValueState, LightrailConstants.API.Balance.ACTIVE))
                    currentValue += attachedValue.getCurrentValue();
            }
        }
        return currentValue;
    }

    private LightrailValue(Balance balance) {
        this.balanceResponse = balance;
    }

    public static LightrailValue retrieveByCode(String code) throws IOException, BadParameterException, AuthorizationException, CouldNotFindObjectException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(LightrailConstants.Parameters.CODE, code);
        LightrailValue lightrailValue;
        try {
            lightrailValue = retrieve(giftValueParams);
        } catch (CurrencyMismatchException e) { //never happens
            throw new RuntimeException(e);
        }
        return lightrailValue;
    }

    public static LightrailValue retrieveByCardId(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
        LightrailValue lightrailValue;
        try {
            lightrailValue = retrieve(giftValueParams);
        } catch (CurrencyMismatchException e) { //never happens
            throw new RuntimeException(e);
        }
        return lightrailValue;
    }

    public static LightrailValue retrieveByContact(String customerAccountId, String currency) throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(LightrailConstants.Parameters.CONTACT, customerAccountId);
        giftValueParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        return retrieve(giftValueParams);
    }

    public static LightrailValue retrieve(Map<String, Object> giftValueParams) throws IOException, CurrencyMismatchException, BadParameterException, AuthorizationException, CouldNotFindObjectException {
        giftValueParams = ContactHandler.handleContact(giftValueParams);
        String requestedCurrency = (String) giftValueParams.get(LightrailConstants.Parameters.CURRENCY);
        String code = (String) giftValueParams.remove(LightrailConstants.Parameters.CODE);
        String cardId = (String) giftValueParams.remove(LightrailConstants.Parameters.CARD_ID);

        Balance balance;
        try {
            if (code != null) {
                balance = APICore.balanceCheckByCode(code);
            } else if (cardId != null) {
                balance = APICore.balanceCheckByCardId(cardId);
            } else {
                throw new BadParameterException("Must provide a 'code', a 'cardId', or a valid 'contact'.");
            }
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }

        String cardCurrency = balance.getCurrency();
        if (requestedCurrency != null & !Objects.equals(cardCurrency, requestedCurrency))
            throw new CurrencyMismatchException(String.format("Currency mismatch. Seeking %s value on a %s gift code.",
                    giftValueParams.get(LightrailConstants.Parameters.CURRENCY),
                    cardCurrency));
        return new LightrailValue(balance);
    }
}
