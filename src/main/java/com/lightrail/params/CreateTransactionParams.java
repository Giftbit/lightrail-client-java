package com.lightrail.params;

public class CreateTransactionParams {
    public String cardId;
    public String currency;
    public String userSuppliedId;
    public int value;
    public boolean pending = false;
    public boolean dryRun = false;
    public boolean nsf = true;

    public CreateTransactionParams(CreateAccountTransactionByContactIdParams contactIdParams, String cardId) {
        this.cardId = cardId;
        this.currency = contactIdParams.currency;
        this.userSuppliedId = contactIdParams.userSuppliedId;
        this.value = contactIdParams.value;
        this.pending = contactIdParams.pending;
        this.dryRun = contactIdParams.dryRun;
        this.nsf = contactIdParams.nsf;
    }
}
