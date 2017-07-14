package com.lightrail.model.api;

import java.util.Map;

@JsonObjectRoot("transaction")
public class Transaction {
    String transactionId;
    Integer value;
    String userSuppliedId;
    String cardId;
    String currency;
    String codeLastFour;
    String giftbitUserId;
    String dateCreated;
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

    public String getGiftbitUserId() {
        return giftbitUserId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public Map<String, Object> getMetadata() {return metadata;}
}
