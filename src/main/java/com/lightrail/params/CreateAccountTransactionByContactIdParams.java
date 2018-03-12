package com.lightrail.params;

public class CreateAccountTransactionByContactIdParams {
    public String contactId;
    public String currency;
    public String userSuppliedId;
    public int value;
    public boolean pending = false;
    public boolean dryRun = false;
    public boolean nsf = true;

    public CreateAccountTransactionByContactIdParams(CreateAccountTransactionByShopperIdParams shopperIdParams, String contactId) {
        this.contactId = contactId;
        this.currency = shopperIdParams.currency;
        this.userSuppliedId = shopperIdParams.userSuppliedId;
        this.value = shopperIdParams.value;
        this.pending = shopperIdParams.pending;
        this.dryRun = shopperIdParams.dryRun;
        this.nsf = shopperIdParams.nsf;
    }
}
