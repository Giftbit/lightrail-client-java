package com.lightrail.params.transactions;

public class LightrailTransactionDestination implements CreditDestination, TransferDestination {

    public String rail;
    public String code;
    public String valueId;

    public LightrailTransactionDestination() {
        rail = "lightrail";
    }

}
