package com.lightrail.model.transaction.step;

import java.util.Objects;

public class InternalTransactionStep extends TransactionStep {

    public String internalId;
    public Integer balanceBefore;
    public Integer balanceAfter;
    public Integer balanceChange;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalTransactionStep that = (InternalTransactionStep) o;
        return Objects.equals(internalId, that.internalId) &&
                Objects.equals(balanceBefore, that.balanceBefore) &&
                Objects.equals(balanceAfter, that.balanceAfter) &&
                Objects.equals(balanceChange, that.balanceChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalId, balanceBefore, balanceAfter, balanceChange);
    }
}
