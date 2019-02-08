package com.lightrail.model.transaction.step;

import java.util.Objects;

public class LightrailTransactionStep extends TransactionStep {

    public String valueId;
    public String contactId;
    public String code;
    public Integer balanceBefore;
    public Integer balanceAfter;
    public Integer balanceChange;
    public Integer usesRemainingBefore;
    public Integer usesRemainingAfter;
    public Integer usesRemainingChange;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LightrailTransactionStep that = (LightrailTransactionStep) o;
        return Objects.equals(valueId, that.valueId) &&
                Objects.equals(contactId, that.contactId) &&
                Objects.equals(code, that.code) &&
                Objects.equals(balanceBefore, that.balanceBefore) &&
                Objects.equals(balanceAfter, that.balanceAfter) &&
                Objects.equals(balanceChange, that.balanceChange) &&
                Objects.equals(usesRemainingBefore, that.usesRemainingBefore) &&
                Objects.equals(usesRemainingAfter, that.usesRemainingAfter) &&
                Objects.equals(usesRemainingChange, that.usesRemainingChange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueId, contactId, code, balanceBefore, balanceAfter, balanceChange, usesRemainingBefore, usesRemainingAfter, usesRemainingChange);
    }
}
