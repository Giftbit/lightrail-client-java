package com.lightrail.model;

import java.util.Objects;

public class TransactionValue {
    public Integer value;
    public Integer valueAvailableAfterTransaction;
    public String valueStoreId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionValue that = (TransactionValue) o;
        return Objects.equals(value, that.value) &&
                Objects.equals(valueAvailableAfterTransaction, that.valueAvailableAfterTransaction) &&
                Objects.equals(valueStoreId, that.valueStoreId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, valueAvailableAfterTransaction, valueStoreId);
    }
}
