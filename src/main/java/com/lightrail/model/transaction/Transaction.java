package com.lightrail.model.transaction;

import com.lightrail.model.transaction.party.TransactionParty;
import com.lightrail.model.transaction.step.TransactionStep;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Transaction {

    public String id;
    public TransactionTaxProperties tax;
    public String transactionType;
    public String currency;
    public TransactionTotals totals;
    public List<LineItem> lineItems;
    public List<TransactionStep> steps;
    public List<TransactionParty> paymentSources;
    public Map<String, Object> metadata;
    public Date createdDate;
    public String createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(tax, that.tax) &&
                Objects.equals(transactionType, that.transactionType) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(totals, that.totals) &&
                Objects.equals(lineItems, that.lineItems) &&
                Objects.equals(steps, that.steps) &&
                Objects.equals(paymentSources, that.paymentSources) &&
                Objects.equals(metadata, that.metadata) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tax, transactionType, currency, totals, lineItems, steps, paymentSources, metadata, createdDate, createdBy);
    }
}
