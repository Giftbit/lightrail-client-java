package com.lightrail.old.model.api.objects;

import com.lightrail.old.helpers.LightrailConstants;

public class RequestParamsCreateAccountByContactId extends LightrailObject {
    public String contactId;
    public String currency;
    public String userSuppliedId;
    public String cardType = LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD;
    public Integer initialValue = 0;

    public RequestParamsCreateAccountByContactId(String jsonObject) {
        super(jsonObject);
    }

    public RequestParamsCreateAccountByContactId(RequestParamsCreateAccountByShopperId shopperIdParams, String contactId) {
        this.contactId = contactId;
        this.currency = shopperIdParams.currency;
        this.userSuppliedId = shopperIdParams.userSuppliedId;
        this.cardType = shopperIdParams.cardType != null ? shopperIdParams.cardType : LightrailConstants.Parameters.CARD_TYPE_ACCOUNT_CARD;
        this.initialValue = shopperIdParams.initialValue != null ? shopperIdParams.initialValue : 0;

    }
}
