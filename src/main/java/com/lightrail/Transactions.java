package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.model.PaginatedList;
import com.lightrail.model.transaction.Transaction;
import com.lightrail.params.transactions.*;

import java.io.IOException;
import java.util.Map;

import static com.lightrail.network.NetworkUtils.encodeUriComponent;
import static com.lightrail.network.NetworkUtils.toQueryString;

public class Transactions {

    private final LightrailClient lr;

    public Transactions(LightrailClient lr) {
        this.lr = lr;
    }

    public Transaction getTransaction(String transactionId) throws IOException, LightrailRestException {
        NullArgumentException.check(transactionId, "transactionId");

        return lr.networkProvider.get(String.format("/transactions/%s", encodeUriComponent(transactionId)), Transaction.class);
    }

    public PaginatedList<Transaction> getTransactionChain(String transactionId) throws IOException, LightrailRestException {
        NullArgumentException.check(transactionId, "transactionId");

        return lr.networkProvider.getPaginatedList(String.format("/transactions/%s/chain", encodeUriComponent(transactionId)), Transaction.class);
    }

    public PaginatedList<Transaction> getTransactionChain(Transaction transaction) throws IOException, LightrailRestException {
        NullArgumentException.check(transaction, "transaction");

        return getTransactionChain(transaction.id);
    }

    public PaginatedList<Transaction> listTransactions() throws IOException, LightrailRestException {
        return lr.networkProvider.getPaginatedList("/transactions", Transaction.class);
    }

    public PaginatedList<Transaction> listTransactions(ListTransactionsParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/transactions%s", toQueryString(params)), Transaction.class);
    }

    public PaginatedList<Transaction> listTransactions(Map<String, String> params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/transactions%s", toQueryString(params)), Transaction.class);
    }

    public Transaction debit(DebitParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post("/transactions/debit", params, Transaction.class);
    }

    public Transaction credit(CreditParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post("/transactions/credit", params, Transaction.class);
    }

    public Transaction transfer(TransferParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post("/transactions/transfer", params, Transaction.class);
    }

    public Transaction reverse(String transactionToReverseId, ReverseParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(transactionToReverseId, "transactionToReverseId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post(String.format("/transactions/%s/reverse", encodeUriComponent(transactionToReverseId)), params, Transaction.class);
    }

    public Transaction reverse(Transaction transactionToReverse, ReverseParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(transactionToReverse, "transactionToReverse");
        NullArgumentException.check(params, "params");

        return reverse(transactionToReverse.id, params);
    }

    public Transaction capturePending(String transactionToCaptureId, CapturePendingParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(transactionToCaptureId, "transactionToCaptureId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post(String.format("/transactions/%s/capture", encodeUriComponent(transactionToCaptureId)), params, Transaction.class);
    }

    public Transaction capturePending(Transaction transactionToCapture, CapturePendingParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(transactionToCapture, "transactionToCapture");
        NullArgumentException.check(params, "params");

        return capturePending(transactionToCapture.id, params);
    }

    public Transaction voidPending(String transactionToVoidId, VoidPendingParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(transactionToVoidId, "transactionToVoidId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post(String.format("/transactions/%s/void", encodeUriComponent(transactionToVoidId)), params, Transaction.class);
    }

    public Transaction voidPending(Transaction transactionToVoid, VoidPendingParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(transactionToVoid, "transactionToVoid");
        NullArgumentException.check(params, "params");

        return voidPending(transactionToVoid.id, params);
    }
}
