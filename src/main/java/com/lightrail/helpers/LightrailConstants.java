package com.lightrail.helpers;

import com.lightrail.exceptions.BadParameterException;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.objects.RequestParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class LightrailConstants {
    public final class API {

        public static final String apiBaseURL = "https://api.lightrail.com/v1/";

        public final class Endpoints {
            public static final String PING = "ping";

            public static final String CREATE_TRANSACTION_BY_CODE = "codes/%s/transactions";
            public static final String CREATE_TRANSACTION_BY_CARD = "cards/%s/transactions";
            public static final String SIMULATE_TRANSACTION_BY_CARD = "cards/%s/transactions/dryRun";
            public static final String SIMULATE_TRANSACTION_BY_CODE = "codes/%s/transactions/dryRun";

            public static final String ACTION_ON_TRANSACTION = "cards/%s/transactions/%s/%s";

            public static final String RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_USERSUPPLIEDID = "codes/%s/transactions?userSuppliedId=%s";
            public static final String RETRIEVE_TRANSACTION_BASED_ON_CARD_AND_USERSUPPLIEDID = "cards/%s/transactions?userSuppliedId=%s";
            public static final String RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_TRANSACTIONID = "codes/%s/transactions/%s";
            public static final String RETRIEVE_CARD_DETAILS_BASED_ON_CODE = "codes/%s/details";
            public static final String RETRIEVE_TRANSACTION_BASED_ON_CARD_AND_TRANSACTIONID = "cards/%s/transactions/%s";
            public static final String RETRIEVE_CARD_DETAILS_BASED_ON_CARD = "cards/%s/details";

            public static final String CREATE_CONTACT = "contacts";
            public static final String RETRIEVE_CONTACT = "contacts/%s";
            public static final String RETRIEVE_CONTACT_CARDS = "cards?contactId=%s&cardType=ACCOUNT_CARD";

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

        public final class Errors{
            public static final String INSUFFICIENT_VALUE = "InsufficientValue";
        }


        public final class Transactions {
            public static final String CAPTURE = "capture";
            public static final String VOID = "void";
            public static final String REFUND = "refund";

            public static final String TYPE_PENDING = "PENDING_CREATE";
        }
        public final class Cards {
            public static final String FREEZE = "freeze";
            public static final String UNFREEZE = "unfreeze";
            public static final String ACTIVATE = "activate";
        }

        public final class ValueStores {
            public static final String STATE_ACTIVE = "ACTIVE";
            public static final String STATE_FROZEN = "FROZEN";
            public static final String STATE_EXPIRED = "EXPIRED";
            public static final String STATE_CANCELLED = "CANCELLED";

            public static final String TYPE_PRINCIPAL = "PRINCIPAL";
            public static final String TYPE_ATTACHED= "ATTACHED";
        }
    }

    public static class Parameters {
        public static final String CURRENCY = "currency";
        public static final String CODE = "code";
        public static final String VALUE = "value";
        public static final String USER_SUPPLIED_ID = "userSuppliedId";
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
        public static final String CONTACT = "contact";
        public static final String PROGRAM_ID = "programId";
        public static final String EXPIRES = "expires";
        public static final String START_DATE = "startDate";
        public static final String TRANSACTION_ID = "transactionId";
        public static final String NSF = "nsf";

        public static void requireParameters(List<String> requiredParams, Map<String, Object> givenParams) throws BadParameterException {
            if (Lightrail.apiKey == null)
                throw new BadParameterException("API Key is not set. You can set the API key as follows: 'Lightrail.apiKey=...'");
            for (String paramName : requiredParams) {
                if (!givenParams.containsKey(paramName) || givenParams.get(paramName) == null)
                    throw new BadParameterException(String.format("Missing Parameter: %s.", paramName));
            }
        }
        public static RequestParameters addDefaultUserSuppliedIdIfNotProvided(RequestParameters params) {
            RequestParameters paramsCopy = new RequestParameters(params);
            String userSuppliedId = (String) paramsCopy.get(LightrailConstants.Parameters.USER_SUPPLIED_ID);
            if (userSuppliedId == null) {
                userSuppliedId = UUID.randomUUID().toString();
                paramsCopy.put(LightrailConstants.Parameters.USER_SUPPLIED_ID, userSuppliedId);
            }
            return paramsCopy;
        }


    }
}
