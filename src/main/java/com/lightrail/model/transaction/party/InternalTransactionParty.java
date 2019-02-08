package com.lightrail.model.transaction.party;

import java.util.Objects;

public class InternalTransactionParty extends TransactionParty {

    public String internalId;
    public Integer balance;
    public Boolean pretax;
    public Boolean beforeLightrail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalTransactionParty that = (InternalTransactionParty) o;
        return Objects.equals(internalId, that.internalId) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(pretax, that.pretax) &&
                Objects.equals(beforeLightrail, that.beforeLightrail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalId, balance, pretax, beforeLightrail);
    }
}
