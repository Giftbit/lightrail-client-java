package com.lightrail.params.transactions;

import java.util.Map;

public class StripeTransactionSource implements CheckoutSource, TransferSource {

    public String rail;
    public String source;
    public String customer;
    public Integer maxAmount;
    public Map<String, Object> additionalStripeParams;

    public StripeTransactionSource() {
        this.rail = "stripe";
    }
}
