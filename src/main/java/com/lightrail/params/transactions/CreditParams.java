package com.lightrail.params.transactions;

import com.google.gson.JsonElement;

import java.util.Map;

public class CreditParams {

    public String id;
    public LightrailTransactionDestination destination;
    public String currency;
    public Integer amount;
    public Integer uses;
    public Boolean simulate;
    public Map<String, JsonElement> metadata;

    public CreditParams() {
    }

    public CreditParams(String id) {
        this.id = id;
    }
}
