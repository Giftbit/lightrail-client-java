package com.lightrail.params.transactions;

import com.google.gson.JsonElement;

import java.util.Map;

public class DebitParams {

    public String id;
    public DebitSource source;
    public String currency;
    public Integer amount;
    public Integer uses;
    public Boolean simulate;
    public Boolean allowRemainder;
    public Boolean pending;
    public Map<String, JsonElement> metadata;

    public DebitParams() {
    }

    public DebitParams(String id) {
        this.id = id;
    }
}
