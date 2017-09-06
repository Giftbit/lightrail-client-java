package com.lightrail.model.api.objects;


import java.util.List;

@JsonObjectRoot("balance")
public class Balance {
    ValueStore principal;
    List<ValueStore> attached;
    String currency;
    String balanceDate;
    String cardId;

    public List<ValueStore> getAttached() {
        return attached;
    }

    public String getCurrency() {
        return currency;
    }

    public ValueStore getPrincipal() {
        return principal;
    }

    public String getBalanceDate() {
        return balanceDate;
    }

    public String getCardId() {
        return cardId;
    }
}
