package com.lightrail.params.transactions;

import java.util.Map;

public class TransferParams {

    public String id;
    public TransferSource source;
    public TransferDestination destination;
    public String currency;
    public Integer amount;
    public Boolean simulate;
    public Boolean allowRemainder;
    public Map<String, Object> metadata;

    public TransferParams() {
    }

    public TransferParams(String id) {
        this.id = id;
    }

}
