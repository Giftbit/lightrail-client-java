package com.lightrail.model.transaction.party;

import java.util.Objects;

public class LightrailTransactionParty extends TransactionParty {

    public String contactId;
    public String code;
    public String valueId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LightrailTransactionParty that = (LightrailTransactionParty) o;
        return Objects.equals(contactId, that.contactId) &&
                Objects.equals(code, that.code) &&
                Objects.equals(valueId, that.valueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactId, code, valueId);
    }
}
