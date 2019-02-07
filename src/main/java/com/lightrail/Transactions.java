package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.transaction.Transaction;
import com.lightrail.params.transactions.CreditParams;
import com.lightrail.params.transactions.DebitParams;
import com.lightrail.params.transactions.TransferParams;

import java.io.IOException;

import static com.lightrail.network.NetworkUtils.encodeUriComponent;

public class Transactions {

    private final LightrailClient lr;

    public Transactions(LightrailClient lr) {
        this.lr = lr;
    }

    public Transaction getTransaction(String transactionId) throws IOException, LightrailRestException {
        return lr.networkProvider.get(String.format("/transactions/%s", encodeUriComponent(transactionId)), Transaction.class);
    }

    public Transaction debit(DebitParams params) throws IOException, LightrailRestException {
        return lr.networkProvider.post("/transactions/debit", params, Transaction.class);
    }

    public Transaction credit(CreditParams params) throws IOException, LightrailRestException {
        return lr.networkProvider.post("/transactions/credit", params, Transaction.class);
    }

    public Transaction transfer(TransferParams params) throws IOException, LightrailRestException {
        return lr.networkProvider.post("/transactions/transfer", params, Transaction.class);
    }
}
