package com.lightrail.model.api;

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
}
