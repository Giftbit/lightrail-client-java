package com.lightrail.model.api.objects;

import java.util.Map;

@JsonObjectRoot("transaction")
public class Transaction {
    String transactionId;
    Integer value;
    String userSuppliedId;
    String cardId;
    String currency;
    String codeLastFour;
    String dateCreated;
    String transactionType;
    String parentTransactionId;
    String transactionAccessMethod;
    Map<String, Object> metadata;

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

    public Map<String, Object> getMetadata() {return metadata;}

}
