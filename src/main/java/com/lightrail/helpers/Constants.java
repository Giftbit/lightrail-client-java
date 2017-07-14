package com.lightrail.helpers;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.model.Lightrail;

import java.util.List;
import java.util.Map;

public final class Constants {
    public final class LightrailAPI {

        public static final String apiBaseURL = "https://api.lightrail.com/v1/";
        public static final String PING_ENDPOINT = "ping";
        public static final String CODES_BALANCE_DETAILS_ENDPOINT = "codes/%s/balance/details";
        public static final String CODES_TRANSACTION_ENDPOINT = "codes/%s/transactions";
        public static final String FINALIZE_TRANSACTION_ENDPOINT = "cards/%s/transactions/%s/%s";
        public static final String FUND_CARD_ENDPOINT = "cards/%s/code/transactions";
        public static final String RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_USERSUPPLIEDID_ENDPOINT = "codes/%s/transactions?userSuppliedId=%s";

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
            public static final String CAPTURE="capture";
            public static final String VOID="void";
        }
    }

    public static final class LightrailEcommerce {
        public static final String HYBRID_TRANSACTION_TOTAL_METADATA_KEY = "_hybrid_transaction_total";
        public static final int STRIPE_MINIMUM_TRANSACTION_VALUE = 50;


        public static final class PaymentSummary {
            public static final String GIFT_CODE_SHARE = "Gift code";
            public static final String CREDIT_CARD_SHARE = "Credit card";
        }
    }
    public static class StripeParameters {
        public static final String AMOUNT = "amount";
        public static final String CURRENCY = "currency";
        public static final String TOKEN = "source";
        public static final String CUSTOMER = "customer";
    }
    public static class LightrailParameters {

        public static final String CURRENCY = "currency";
        public static final String CODE = "code";
        public static final String AMOUNT = "amount";
        public static final String VALUE = "value";
        public static final String USER_SUPPLIED_ID = "userSuppliedId";
        public static final String CAPTURE = "capture";
        public static final String PENDING = "pending";
        public static final String CARD_ID = "cardId";
        public static final String METADATA = "metadata";

        public static void requireParameters(List<String> requiredParams, Map<String, Object> givenParams) throws BadParameterException {
            if (Lightrail.apiKey == null)
                throw new BadParameterException("API Key is not set. You can set the API key as follows: 'Lightrail.apiKey=...'");
            for (String paramName : requiredParams) {
                if (!givenParams.containsKey(paramName) || givenParams.get(paramName) == null)
                    throw new BadParameterException(String.format("Missing Parameter: %s.", paramName));
            }
        }
    }
}
