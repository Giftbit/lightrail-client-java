package com.lightrail.model.transaction;

import java.util.Objects;

public class TransactionTaxProperties {

    public String roundingMode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTaxProperties that = (TransactionTaxProperties) o;
        return Objects.equals(roundingMode, that.roundingMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roundingMode);
    }
}
