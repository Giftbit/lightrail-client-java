package com.lightrail.params.transactions;

public class LightrailTransactionSource implements TransferSource {

    public String rail;
    public String code;
    public String contactId;
    public String valueId;

    public LightrailTransactionSource() {
        rail = "lightrail";
    }
}
