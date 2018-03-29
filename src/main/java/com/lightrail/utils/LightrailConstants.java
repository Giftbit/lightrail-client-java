package com.lightrail.utils;

public final class LightrailConstants {
    public final class API {

        public static final String apiBaseURL = "https://api.lightrail.com/v1/";

        public final class Endpoints {
            public static final String PING = "ping";

            public static final String CREATE_TRANSACTION = "cards/%s/transactions";
            public static final String ACTION_ON_TRANSACTION = "cards/%s/transactions/%s/%s";

            public static final String CREATE_CONTACT = "contacts";
            public static final String RETRIEVE_CONTACT = "contacts/%s";
            public static final String RETRIEVE_CONTACT_BY_UID = "contacts?userSuppliedId=%s";

            public static final String CREATE_CARD = "cards";
            public static final String SEARCH_CARDS = "cards?";

            public static final String CREATE_PROGRAM = "programs";
            public static final String RETRIEVE_PROGRAM = "programs/";
        }

        public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
        public static final String AUTHORIZATION_TOKEN_TYPE = "Bearer";

        public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
        public static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";

        public static final String REQUEST_METHOD_GET = "GET";
        public static final String REQUEST_METHOD_POST = "POST";

        public final class Transactions {
            public static final String CAPTURE = "capture";
            public static final String VOID = "void";
            public static final String REFUND = "refund";

            public static final String DRYRUN = "/dryRun";
        }
    }
}
