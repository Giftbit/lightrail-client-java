package com.lightrail.params.transactions;

public class StripeTransactionSource implements CheckoutSource, TransferSource {

    public String rail;
    public String source;
    public String customer;
    public Integer maxAmount;

    public StripeTransactionSource() {
        this.rail = "stripe";
    }
}
