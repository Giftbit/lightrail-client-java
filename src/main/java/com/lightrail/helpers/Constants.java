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
        public static final String RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_USERSUPPLIEDID_ENDPOINT = "codes/%s/transactions?userSuppliedId=%s";

        public static final String CREATE_CONTACT_ENDPOINT = "contacts";
        public static final String RETRIEVE_CONTACT_ENDPOINT = "contacts/%s";

        public static final String FUND_CARD_ENDPOINT = "cards/%s/code/transactions";
        public static final String CREATE_CARD_ENDPOINT = "cards";
        public static final String RETRIEVE_CARD_ENDPOINT = "cards/%s";


        public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
        public static final String AUTHORIZATION_TOKEN_TYPE = "Bearer";

        public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
        public static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";

        public static final String REQUEST_METHOD_GET = "GET";
        public static final String REQUEST_METHOD_POST = "POST";
        public static final String REQUEST_METHOD_DELETE = "DELETE";


        public final class CodeBalanceCheck {
            public static final String ACTIVE = "ACTIVE";
        }

        public final class Transactions {
            public static final String CAPTURE = "capture";
            public static final String VOID = "void";
        }
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
        public static final String EMAIL = "email";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String CONTACT_ID = "contactId";
        public static final String INITIAL_VALUE = "initialValue";
        public static final String CARD_TYPE = "cardType";
        public static final String CARD_TYPE_ACCOUNT_CARD = "ACCOUNT_CARD";
        public static final String CARD_CATEGORIES = "categories";

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
