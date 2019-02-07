package com.lightrail.model.transaction.party;

import java.util.Objects;

public class StripeTransactionParty extends TransactionParty {

    public String source;
    public String customer;
    public Integer maxAmount;
    public Integer priority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StripeTransactionParty that = (StripeTransactionParty) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(customer, that.customer) &&
                Objects.equals(maxAmount, that.maxAmount) &&
                Objects.equals(priority, that.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, customer, maxAmount, priority);
    }
}
