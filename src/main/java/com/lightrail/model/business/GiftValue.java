package com.lightrail.model.business;

import com.lightrail.exceptions.*;
import com.lightrail.model.api.CodeBalance;
import com.lightrail.model.api.ValueStore;
import com.lightrail.helpers.*;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GiftValue {

    private CodeBalance codeBalanceResponse;

    public String getCurrency() {
        return codeBalanceResponse.getCurrency();
    }

    public String getTimeStamp() {
        return codeBalanceResponse.getBalanceDate();
    }

    public int getCurrentValue() throws GiftCodeNotActiveException {
        int currentValue = 0;

        String codeState = codeBalanceResponse.getPrincipal().getState();

        if (!Objects.equals(codeState, Constants.LightrailAPI.CodeBalanceCheck.ACTIVE))
            throw new GiftCodeNotActiveException("This gift code is not active at this time.");

        currentValue = codeBalanceResponse.getPrincipal().getCurrentValue();
        List<ValueStore> attachedValues = codeBalanceResponse.getAttached();
        if (attachedValues != null) {
            for (ValueStore attachedValue : attachedValues) {
                String attachedValueState = attachedValue.getState();
                if (Objects.equals(attachedValueState, Constants.LightrailAPI.CodeBalanceCheck.ACTIVE))
                    currentValue += attachedValue.getCurrentValue();
            }
        }
        return currentValue;
    }

    private GiftValue(CodeBalance codeBalance) {
        this.codeBalanceResponse = codeBalance;
    }

    public static GiftValue retrieve(Map<String, Object> giftValueParams) throws IOException, CurrencyMismatchException, BadParameterException, InsufficientValueException, AuthorizationException {
        Constants.LightrailParameters.requireParameters(Arrays.asList(
                Constants.LightrailParameters.CODE,
                Constants.LightrailParameters.CURRENCY
                ),
                giftValueParams);

        String requestedCode = (String) giftValueParams.get(Constants.LightrailParameters.CODE);
        String requestedCurrency = (String) giftValueParams.get(Constants.LightrailParameters.CURRENCY);


        CodeBalance codeBalance = APICore.balanceCheck(requestedCode);
        String codeCurrency = codeBalance.getCurrency();
        if (!Objects.equals(codeCurrency, requestedCurrency))
            throw new CurrencyMismatchException(String.format("Currency mismatch. Seeking %s value on a %s gift code.",
                    giftValueParams.get(Constants.LightrailParameters.CURRENCY),
                    codeCurrency));
        return new GiftValue(codeBalance);
    }
}
