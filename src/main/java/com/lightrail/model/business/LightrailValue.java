package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.model.api.Balance;
import com.lightrail.model.api.ValueStore;
import com.lightrail.helpers.*;
import com.lightrail.net.APICore;

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

    public int getCurrentValue() {
        int currentValue = 0;

        String codeState = balanceResponse.getPrincipal().getState();

        if (!Objects.equals(codeState, LightrailConstants.API.CodeBalanceCheck.ACTIVE))
            throw new CardNotActiveException("This gift code is not active at this time.");

        currentValue = balanceResponse.getPrincipal().getCurrentValue();
        List<ValueStore> attachedValues = balanceResponse.getAttached();
        if (attachedValues != null) {
            for (ValueStore attachedValue : attachedValues) {
                String attachedValueState = attachedValue.getState();
                if (Objects.equals(attachedValueState, LightrailConstants.API.CodeBalanceCheck.ACTIVE))
                    currentValue += attachedValue.getCurrentValue();
            }
        }
        return currentValue;
    }

    private LightrailValue(Balance balance) {
        this.balanceResponse = balance;
    }

    public static LightrailValue retrieveByCode(String code) throws IOException, CurrencyMismatchException, BadParameterException, AuthorizationException, CouldNotFindObjectException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(LightrailConstants.Parameters.CODE, code);
        return retrieve(giftValueParams);
    }

    public static LightrailValue retrieveByCardId(String cardId) throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(LightrailConstants.Parameters.CARD_ID, cardId);
        return retrieve(giftValueParams);
    }

    public static LightrailValue retrieveByCustomer(String customerAccountId, String currency) throws AuthorizationException, CurrencyMismatchException, CouldNotFindObjectException, IOException {
        Map<String, Object> giftValueParams = new HashMap<>();
        giftValueParams.put(LightrailConstants.Parameters.CUSTOMER, customerAccountId);
        giftValueParams.put(LightrailConstants.Parameters.CURRENCY, currency);
        return retrieve(giftValueParams);
    }

    public static LightrailValue retrieve(Map<String, Object> giftValueParams) throws IOException, CurrencyMismatchException, BadParameterException, AuthorizationException, CouldNotFindObjectException {
        giftValueParams = LightrailTransaction.handleCustomer(giftValueParams);
        String requestedCurrency = (String) giftValueParams.get(LightrailConstants.Parameters.CURRENCY);
        String code = (String) giftValueParams.get(LightrailConstants.Parameters.CODE);
        String cardId = (String) giftValueParams.get(LightrailConstants.Parameters.CARD_ID);

        Balance balance;
        try {
            if (code != null) {
                balance = APICore.balanceCheckByCode(code);
            } else if (cardId != null) {
                balance = APICore.balanceCheckByCardId(cardId);
            } else {
                throw new BadParameterException("Must provide a 'code', a 'cardId', or a valid 'customer'.");
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
