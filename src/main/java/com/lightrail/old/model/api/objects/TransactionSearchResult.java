package com.lightrail.old.model.api.objects;


import com.google.gson.Gson;
import com.lightrail.old.exceptions.BadParameterException;
import com.lightrail.old.exceptions.CouldNotFindObjectException;

public class TransactionSearchResult extends LightrailObject {
    public Transaction[] transactions;
    public Pagination pagination;

    public Transaction[] getTransactions() {
        return transactions;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public Transaction getOneTransaction() throws CouldNotFindObjectException {
        if (transactions.length == 1)
            return transactions[0];
        else if (transactions.length > 1)
            throw new BadParameterException("Search results include more than one transaction.");
        else
            throw new CouldNotFindObjectException("Transaction does not exists.");
    }

    public TransactionSearchResult(String jsonObject) {
        super(jsonObject);
        for (Transaction transaction : transactions) {
            String transactionJsonString = new Gson().toJson(transaction);
            Class<? extends LightrailObject> myClass = Transaction.class;
            JsonObjectRoot jsonRootAnnotation = myClass.getAnnotation(JsonObjectRoot.class);
            if (jsonRootAnnotation != null) {
                String jsonRootName = jsonRootAnnotation.value();
                transactionJsonString = String.format("{\"" + jsonRootName + "\":%s}", transactionJsonString);
            }
            transaction.setRawJson(transactionJsonString);
        }
    }
}
