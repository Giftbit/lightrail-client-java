package com.lightrail.old.model.api.objects;

@JsonObjectRoot("transaction")
public class Transaction extends LightrailObject {
    public String transactionId;
    public Integer value;
    public String userSuppliedId;
    public String cardId;
    public String currency;
    public String codeLastFour;
    public String dateCreated;
    public String transactionType;
    public String parentTransactionId;
    public String transactionAccessMethod;
    public TransactionValue[] transactionBreakdown;
    public Metadata metadata;

    public String getTransactionId() {
        return transactionId;
    }

    public Integer getValue() {
        return value;
    }

    public String getUserSuppliedId() {
        return userSuppliedId;
    }

    public String getCardId() {
        return cardId;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCodeLastFour() {
        return codeLastFour;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTransactionAccessMethod() {
        return transactionAccessMethod;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public TransactionValue[] getTransactionBreakdown() {
        return transactionBreakdown;
    }

    public Transaction(String jsonObject) {
        super(jsonObject);
    }

    public void setValue(int value) {
        this.value = value;
    }
}
