package com.lightrail.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightrail.exceptions.CurrencyMismatchException;
import com.lightrail.exceptions.GiftCodeNotActiveException;
import com.lightrail.net.APICore;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GiftValue {

    private static String CODE_PARAM_NAME = "code";
    private static String CURRENCY_PARAM_NAME = "currency";

    private static String BALANCE_OBJECT_NAME = "balance";
    private static String CURRENCY_OBJECT_NAME = "currency";

    private static String PRINCIPAL_OBJECT_NAME = "principal";
    private static String STATE_OBJECT_NAME = "state";
    private static String STATE_OBJECT_VALUE_ACTIVE = "ACTIVE";
    private static String CURRENT_VALUE_OBJECT_NAME = "currentValue";


    private static String ATTACHED_ARRAY_NAME = "attached";

    private JsonObject valueCheckResponse;

    public String getCurrency() {
        return valueCheckResponse.get(BALANCE_OBJECT_NAME).getAsJsonObject()
                .get(CURRENCY_OBJECT_NAME).getAsString();
    }

    public float getValue() throws GiftCodeNotActiveException {
        int currentValue = 0;
        JsonObject principalObject = valueCheckResponse
                .get(BALANCE_OBJECT_NAME).getAsJsonObject()
                .get(PRINCIPAL_OBJECT_NAME).getAsJsonObject();

        String codeState = principalObject.get(STATE_OBJECT_NAME).getAsString();

        if (!Objects.equals(codeState, STATE_OBJECT_VALUE_ACTIVE))
            throw new GiftCodeNotActiveException("This gift code is not active at this time.");

        currentValue = principalObject.get(CURRENT_VALUE_OBJECT_NAME).getAsInt();

        JsonArray attachedValues = valueCheckResponse
                .get(BALANCE_OBJECT_NAME).getAsJsonObject()
                .get(ATTACHED_ARRAY_NAME).getAsJsonArray();
        if (attachedValues != null) {
            for (JsonElement attachedValueElement : attachedValues) {
                JsonObject attachedValueObject = attachedValueElement.getAsJsonObject();

                String attachmentState = attachedValueObject.get(STATE_OBJECT_NAME).getAsString();
                if (Objects.equals(attachmentState, STATE_OBJECT_VALUE_ACTIVE))
                    currentValue += attachedValueObject.get(CURRENT_VALUE_OBJECT_NAME).getAsInt();
            }
        }
        return Currency.minorToMajor(currentValue, getCurrency());
    }

    private GiftValue(JsonObject valueCheckResponse) {
        this.valueCheckResponse = valueCheckResponse;
    }

    public static GiftValue retrieve(Map<String, Object> giftValueParams) throws IOException, CurrencyMismatchException {
        JsonObject valueCheckResponse = APICore.balanceCheck((String) giftValueParams.get(CODE_PARAM_NAME));
        String codeCurrency = valueCheckResponse.get(BALANCE_OBJECT_NAME).getAsJsonObject().get(CURRENCY_OBJECT_NAME).getAsString();
        if (!Objects.equals(codeCurrency, giftValueParams.get(CURRENCY_PARAM_NAME)))
            throw new CurrencyMismatchException("Currency mismatch");
        return new GiftValue(valueCheckResponse);
    }
}
