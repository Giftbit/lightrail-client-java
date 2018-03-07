package com.lightrail.params;

public class CreateAccountCardByContactIdParams {
    public String contactId;
    public String currency;
    public String userSuppliedId;
    public int initialValue = 0;
    public String cardType = "ACCOUNT_CARD";

    public CreateAccountCardByContactIdParams(CreateAccountCardByShopperIdParams shopperIdParams, String contactId) {
        this.contactId = contactId;
        this.currency = shopperIdParams.currency;
        this.userSuppliedId = shopperIdParams.userSuppliedId;
        this.initialValue = shopperIdParams.initialValue;
        this.cardType = shopperIdParams.cardType;
    }
}
