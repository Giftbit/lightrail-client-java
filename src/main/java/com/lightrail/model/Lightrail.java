package com.lightrail.model;

public class Lightrail {
    public static String apiKey;


    public static final String apiBaseURL = "https://www.lightrail.com/v1/";
    public static final String PING_ENDPOINT = "ping";
    public static final String CODES_BALANCE_DETAILS_ENDPOINT = "codes/%s/balance/details";
    public static final String CODES_TRANSACTION_ENDPOINT = "codes/%s/transactions";
    public static final String FINALIZE_TRANSACTION_ENDPOINT = "cards/%s/transactions/%s/%s";
}
