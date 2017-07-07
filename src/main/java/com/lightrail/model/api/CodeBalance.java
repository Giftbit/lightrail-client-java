package com.lightrail.model.api;


import java.util.List;

@JsonObjectRoot("balance")
public class CodeBalance {
    ValueStore principal;
    List<ValueStore> attached;
    String currency;

    public List<ValueStore> getAttached() {
        return attached;
    }

    public String getCurrency() {
        return currency;
    }

    public ValueStore getPrincipal() {
        return principal;
    }
}
