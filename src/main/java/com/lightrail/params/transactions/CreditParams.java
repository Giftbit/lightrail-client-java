package com.lightrail.params.transactions;

import java.util.Map;

public class CreditParams {

    public String id;
    public CreditDestination destination;
    public String currency;
    public Integer amount;
    public Integer uses;
    public Boolean simulate;
    public Map<String, Object> metadata;

    public CreditParams() {
    }

    public CreditParams(String id) {
        this.id = id;
    }
}
