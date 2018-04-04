package com.lightrail.params;

public class CompletePendingTransactionParams {
    public String transactionId;
    public String cardId;
    public String userSuppliedId;
    public boolean captureTransaction;

    public boolean voidTransaction() {
        return !captureTransaction;
    }


    public CompletePendingTransactionParams(HandleAccountPendingByContactId params, String cardId, boolean capture) {
        this.transactionId = params.transactionId;
        this.cardId = cardId;
        this.userSuppliedId = params.userSuppliedId;
        this.captureTransaction = capture;
    }

    public CompletePendingTransactionParams(HandleAccountPendingByShopperId params, String cardId, boolean capture) {
        this.transactionId = params.transactionId;
        this.cardId = cardId;
        this.userSuppliedId = params.userSuppliedId;
        this.captureTransaction = capture;
    }
}
