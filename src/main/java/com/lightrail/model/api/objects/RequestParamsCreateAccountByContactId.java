package com.lightrail.model.api.objects;

import com.lightrail.helpers.LightrailConstants;

public class RequestParamsCreateAccountByContactId extends LightrailObject {
    public String contactId;
    public String currency;
    public String userSuppliedId;
    public String cardType = LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD;
    public Integer initialValue = 0;

    public RequestParamsCreateAccountByContactId(String jsonObject) {
        super(jsonObject);
    }
}
