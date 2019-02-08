package com.lightrail.params.transactions;

public class InternalTransactionSource implements CheckoutSource {

    public String rail;
    public String internalId;
    public Integer balance;
    public Boolean pretax;
    public Boolean beforeLightrail;

    public InternalTransactionSource() {
        this.rail = "internal";
    }
}
