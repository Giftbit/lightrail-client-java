package com.lightrail.model.api.objects;

import com.lightrail.helpers.LightrailConstants;

public class RequestParamsCreateAccountByShopperId extends LightrailObject {
    public String shopperId;
    public String currency;
    public String userSuppliedId;
    public String cardType = LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD;
    public Integer initialValue = 0;

    public RequestParamsCreateAccountByShopperId(String jsonObject) {
        super(jsonObject);
    }
}
