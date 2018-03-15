package com.lightrail.params;

public class HandlePendingTransactionParams {
    public String transactionId;
    public String cardId;
    public String userSuppliedId;
    public boolean captureTransaction = false;
    public boolean voidTransaction = false;


    public HandlePendingTransactionParams(HandleAccountPendingByContactId params, String cardId, boolean capture) {
        this.transactionId = params.transactionId;
        this.cardId = cardId;
        this.userSuppliedId = params.userSuppliedId;
        this.captureTransaction = capture;
        this.voidTransaction = !capture;
    }

    public HandlePendingTransactionParams(HandleAccountPendingByShopperId params, String cardId, boolean capture) {
        this.transactionId = params.transactionId;
        this.cardId = cardId;
        this.userSuppliedId = params.userSuppliedId;
        this.captureTransaction = capture;
        this.voidTransaction = !capture;
    }
}
