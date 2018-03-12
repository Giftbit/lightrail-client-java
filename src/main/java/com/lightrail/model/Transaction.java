package com.lightrail.model;

import java.util.HashMap;

public class Transaction {
    public String transactionId;
    public int value;
    public String userSuppliedId;
    public String cardId;
    public String currency;
    public String cardType;
    public String dateCreated;
    public String codeLastFour;
    public String transactionType;
    public String parentTransactionId;
    public String transactionAccessMethod;
    public TransactionValue[] transactionBreakdown;
    public HashMap<String, Object> metadata;
}
