package com.lightrail.params.transactions;

public class LightrailTransactionSource implements CheckoutSource, DebitSource, TransferSource {

    public String rail;
    public String code;
    public String contactId;
    public String valueId;

    public LightrailTransactionSource() {
        rail = "lightrail";
    }
}
