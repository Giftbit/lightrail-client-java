package com.lightrail.model.transaction.step;

import java.util.Objects;

public class StripeTransactionStep extends TransactionStep {

    public Integer amount;
    public String chargeId;
    public Object charge;   // TODO make a stripe charge

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StripeTransactionStep that = (StripeTransactionStep) o;
        return Objects.equals(amount, that.amount) &&
                Objects.equals(chargeId, that.chargeId) &&
                Objects.equals(charge, that.charge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, chargeId, charge);
    }
}
