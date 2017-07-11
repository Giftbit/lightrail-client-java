package com.lightrail.model.business;

import com.lightrail.model.api.Transaction;

public abstract class GiftTransaction {
    Transaction transactionResponse;

    public String getCardId() {
        return transactionResponse.getCardId();
    }

    public String getTransactionId() {
        return transactionResponse.getTransactionId();
    }

    public String getUserSuppliedId() {
        return transactionResponse.getUserSuppliedId();
    }

    public String getCodeLastFour() {
        return transactionResponse.getCodeLastFour();
    }

    public String getDateCreated() {
        return transactionResponse.getDateCreated();
    }
}
