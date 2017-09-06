package com.lightrail.helpers;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.model.Lightrail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class LightrailConstants {
    public final class API {

        public static final String apiBaseURL = "https://api.lightrail.com/v1/";

        public final class Endpoints {
            public static final String PING = "ping";
            public static final String CODES_BALANCE_DETAILS = "codes/%s/balance/details";
            public static final String CARDS_BALANCE = "cards/%s/balance";

            public static final String CODES_TRANSACTION = "codes/%s/transactions";
            public static final String ACTION_ON_TRANSACTION = "cards/%s/transactions/%s/%s";
            public static final String RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_USERSUPPLIEDID = "codes/%s/transactions?userSuppliedId=%s";

            public static final String CREATE_CONTACT = "contacts";
            public static final String RETRIEVE_CONTACT = "contacts/%s";
            public static final String RETRIEVE_CONTACT_CARDS = "cards?contactId=%s&cardType=ACCOUNT_CARD";

            public static final String FUND_CARD = "cards/%s/transactions";
            public static final String CREATE_CARD = "cards";
            public static final String RETRIEVE_CARD = "cards/%s";
            public static final String CANCEL_CARD = "cards/%s/cancel";
            public static final String RETRIEVE_FULL_CODE = "cards/%s/fullcode";
            public static final String ACTION_ON_CARD = "cards/%s/%s";

        }

        public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
        public static final String AUTHORIZATION_TOKEN_TYPE = "Bearer";

        public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
        public static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";

        public static final String REQUEST_METHOD_GET = "GET";
        public static final String REQUEST_METHOD_POST = "POST";
        public static final String REQUEST_METHOD_DELETE = "DELETE";

        public final class Balance {
            public static final String ACTIVE = "ACTIVE";
            public static final String FROZEN = "FROZEN";
        }

        public final class Transactions {
            public static final String CAPTURE = "capture";
            public static final String VOID = "void";
            public static final String REFUND = "refund";
        }
        public final class Cards {
            public static final String FREEZE = "freeze";
            public static final String UNFREEZE = "unfreeze";
            public static final String ACTIVATE = "activate";
        }
    }

    public static class Parameters {
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
        public static final String CARD_TYPE_GIFT_CARD = "GIFT_CARD";
        public static final String CUSTOMER = "lightrailCustomer";
        public static final String PROGRAM_ID = "programId";
        public static final String EXPIRES = "expires";
        public static final String START_DATE = "startDate";

        public static void requireParameters(List<String> requiredParams, Map<String, Object> givenParams) throws BadParameterException {
            if (Lightrail.apiKey == null)
                throw new BadParameterException("API Key is not set. You can set the API key as follows: 'Lightrail.apiKey=...'");
            for (String paramName : requiredParams) {
                if (!givenParams.containsKey(paramName) || givenParams.get(paramName) == null)
                    throw new BadParameterException(String.format("Missing Parameter: %s.", paramName));
            }
        }
        public static Map<String, Object> addDefaultUserSuppliedIdIfNotProvided(Map<String, Object> params) {
            Map<String, Object> paramsCopy = new HashMap<>(params);
            String idempotencyKey = (String) paramsCopy.get(LightrailConstants.Parameters.USER_SUPPLIED_ID);
            if (idempotencyKey == null) {
                idempotencyKey = UUID.randomUUID().toString();
                paramsCopy.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, idempotencyKey);
            }
            return paramsCopy;
        }
    }
}
