package com.lightrail.net;

public class Lightrail {
    public static String apiKey;


    static String apiBaseURL = "https://www.lightrail.com/v1/";
    static String PING_ENDPOINT = "ping";
    static String CODES_BALANCE_DETAILS_ENDPOINT = "codes/%s/balance/details";
    static String CODES_TRANSACTION_ENDPOINT = "codes/%s/transactions";
}
