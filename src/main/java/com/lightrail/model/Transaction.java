package com.lightrail.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Transaction {
    public String transactionId;
    public int value;
    public String userSuppliedId;
    public String cardId;
    public String currency;
    public String cardType;
    public String dateCreated;
    public String codeLastFour;
    public String transactionType;
    public String parentTransactionId;
    public String transactionAccessMethod;
    public TransactionValue[] transactionBreakdown;
    public HashMap<String, Object> metadata;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return value == that.value &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(userSuppliedId, that.userSuppliedId) &&
                Objects.equals(cardId, that.cardId) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(cardType, that.cardType) &&
                Objects.equals(dateCreated, that.dateCreated) &&
                Objects.equals(codeLastFour, that.codeLastFour) &&
                Objects.equals(transactionType, that.transactionType) &&
                Objects.equals(parentTransactionId, that.parentTransactionId) &&
                Objects.equals(transactionAccessMethod, that.transactionAccessMethod) &&
                Arrays.equals(transactionBreakdown, that.transactionBreakdown) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(transactionId, value, userSuppliedId, cardId, currency, cardType, dateCreated, codeLastFour, transactionType, parentTransactionId, transactionAccessMethod, metadata);
        result = 31 * result + Arrays.hashCode(transactionBreakdown);
        return result;
    }
}
