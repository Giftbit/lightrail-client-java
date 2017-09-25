package com.lightrail.model.api.objects;


import com.google.gson.Gson;

public class TransactionSearchResult extends LightrailObject{
    public Transaction[] transactions;
    public Pagination pagination;

    public Transaction[] getTransactions() {
        return transactions;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public Transaction getOneTransaction () {
        if (transactions.length > 0)
            return transactions[0];
        else
            return null;
    }

    public TransactionSearchResult (String jsonObject) {
        super(jsonObject);
        for (Transaction transaction: transactions) {
            String transactionJsonString = new Gson().toJson(transaction);
            Class<? extends LightrailObject> myClass = Transaction.class;
            JsonObjectRoot jsonRootAnnotation = myClass.getAnnotation(JsonObjectRoot.class);
            if (jsonRootAnnotation != null) {
                String jsonRootName = jsonRootAnnotation.value();
                transactionJsonString = String.format("{\""+jsonRootName + "\":%s}", transactionJsonString);
            }
            transaction.setRawJson(transactionJsonString);
        }
    }
}
