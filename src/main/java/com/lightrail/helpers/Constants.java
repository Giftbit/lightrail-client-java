package com.lightrail.helpers;

public final class Constants {
    public final class LightrailAPI {
        public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
        public static final String AUTHORIZATION_TOKEN_TYPE = "Bearer";

        public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
        public static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";


        public static final String REQUEST_METHOD_GET = "GET";
        public static final String REQUEST_METHOD_POST = "POST";

        public final class CodeBalanceCheck {
            public static final String ACTIVE = "ACTIVE";
        }

        public final class Transactions {
            public static final String CAPTURE = "capture";
            public static final String VOID = "void";
        }
    }

    public final class LightrailEcommerce {
        public final class PaymentSummary {
            public static final String GIFT_CODE_SHARE = "Gift code";
            public static final String CREDIT_CARD_SHARE = "Credit card";
        }
    }
}
