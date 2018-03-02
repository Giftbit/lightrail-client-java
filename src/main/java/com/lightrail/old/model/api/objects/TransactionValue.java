package com.lightrail.old.model.api.objects;

public class TransactionValue {
    public Integer value;
    public Integer valueAvailableAfterTransaction;
    public String valueStoreId;

    public Integer getValue() {
        return value;
    }

    public Integer getValueAvailableAfterTransaction() {
        return valueAvailableAfterTransaction;
    }

    public String getValueStoreId() {
        return valueStoreId;
    }
}
