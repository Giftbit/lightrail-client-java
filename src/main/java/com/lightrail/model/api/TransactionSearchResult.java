package com.lightrail.model.api;


import java.util.List;

public class TransactionSearchResult {
    List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction getOneTransaction () {
        if (transactions.size() > 0)
            return transactions.get(0);
        else
            return null;
    }
}
