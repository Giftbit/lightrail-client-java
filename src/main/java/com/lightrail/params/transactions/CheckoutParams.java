package com.lightrail.params.transactions;

import com.lightrail.model.transaction.LineItem;

import java.util.List;
import java.util.Map;

public class CheckoutParams {

    public String id;
    public String currency;
    public List<LineItem> lineItems;
    public List<CheckoutSource> sources;
    public Boolean simulate;
    public Boolean allowRemainder;
    public Boolean pending;
    public Map<String, Object> metadata;

    public CheckoutParams() {
    }

    public CheckoutParams(String id) {
        this.id = id;
    }
}
