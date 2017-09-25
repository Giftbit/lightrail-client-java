package com.lightrail.model.api.net;

import com.google.gson.Gson;
import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.objects.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class APICore {

    public static final class Core {
        private static String getRawAPIResponse(String urlSuffix, String requestMethod, String body) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            URL requestURL = new URL(LightrailConstants.API.apiBaseURL + urlSuffix);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) requestURL.openConnection();
            httpsURLConnection.setRequestProperty(
                    LightrailConstants.API.AUTHORIZATION_HEADER_NAME,
                    LightrailConstants.API.AUTHORIZATION_TOKEN_TYPE + " " + Lightrail.apiKey);
            httpsURLConnection.setRequestMethod(requestMethod);

            if (body != null) {
                httpsURLConnection.setRequestProperty(LightrailConstants.API.CONTENT_TYPE_HEADER_NAME, LightrailConstants.API.CONTENT_TYPE_JSON_UTF8);
                httpsURLConnection.setDoOutput(true);
                OutputStream wr = httpsURLConnection.getOutputStream();
                wr.write(body.getBytes(StandardCharsets.UTF_8));
                wr.flush();
                wr.close();
            }
            int responseCode = httpsURLConnection.getResponseCode();

            InputStream responseInputStream;
            if (httpsURLConnection.getResponseCode() < HttpsURLConnection.HTTP_BAD_REQUEST) {
                responseInputStream = httpsURLConnection.getInputStream();
            } else {
                responseInputStream = httpsURLConnection.getErrorStream();
            }

            BufferedReader responseReader = new BufferedReader(new InputStreamReader(responseInputStream, StandardCharsets.UTF_8));
            StringBuilder responseStringBuffer = new StringBuilder();
            String inputLine;
            while ((inputLine = responseReader.readLine()) != null)
                responseStringBuffer.append(inputLine).append('\n');
            responseReader.close();

            String responseString = responseStringBuffer.toString();

            if (responseCode > 204) {
                handleErrors(responseCode, responseString);
            }

            return responseString;
        }

        private static void handleErrors(int responseCode, String responseString) throws AuthorizationException, InsufficientValueException, IOException, CouldNotFindObjectException {
            APIError error = new APIError(responseString);
            String errorMessage = responseString;
            String errorMessageCode = "";
            if (error != null) {
                if (error.getMessage() != null)
                    errorMessage = error.getMessage();
                if (error.getMessageCode() != null)
                    errorMessageCode = error.getMessageCode();
            }
            switch (responseCode) {
                case 401:
                case 403:
                    throw new AuthorizationException(String.format("Authorization error (%d): %s ", responseCode, errorMessage));
                case 404:
                    throw new CouldNotFindObjectException(errorMessage);
                case 409:
                    throw new BadParameterException(String.format("Idempotency error (%d): %s", responseCode, errorMessage));
                case 400:
                    if (LightrailConstants.API.Errors.INSUFFICIENT_VALUE.equals(errorMessageCode)) {
                        throw new InsufficientValueException();
                    } else {
                        throw new BadParameterException(errorMessage);
                    }
                default:
                    throw new IOException(String.format("Server responded with %d : %s", responseCode, errorMessage));
            }
        }

        public static String ping() throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            return getRawAPIResponse(LightrailConstants.API.Endpoints.PING, LightrailConstants.API.REQUEST_METHOD_GET, null);
        }
    }

    public static final class Transactions {

        public static Transaction createTransactionByCode(String code, Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_TRANSACTION_BY_CODE, code);
            String bodyJsonString = new Gson().toJson(transactionParams);
            String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction createTransactionByCard(String cardId,
                                                          Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_TRANSACTION_BY_CARD, cardId);
            String bodyJsonString = new Gson().toJson((transactionParams));
            String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction simulateTransactionByCard(String cardId,
                                                        Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.SIMULATE_TRANSACTION_BY_CARD, cardId);
            String bodyJsonString = new Gson().toJson(transactionParams);
            String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }
        public static Transaction simulateTransactionByCode(String code, Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.SIMULATE_TRANSACTION_BY_CODE, code);
            String bodyJsonString = new Gson().toJson(transactionParams);
            String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction actionOnTransaction(String cardId,
                                                      String transactionId,
                                                      String action,
                                                      Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.ACTION_ON_TRANSACTION, cardId, transactionId, action);
            String bodyJsonString = new Gson().toJson((transactionParams));
            String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return new Transaction(rawAPIResponse);
        }

        public static Transaction retrieveTransactionByCardIdAndUserSuppliedId(String cardId,
                                                                               String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CARD_AND_USERSUPPLIEDID,
                    cardId, userSuppliedId);
            return new TransactionSearchResult(Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null)).getOneTransaction();
        }

        public static Transaction retrieveTransactionByCodeAndUserSuppliedId(String code,
                                                                             String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_USERSUPPLIEDID,
                    code, userSuppliedId);
            return new TransactionSearchResult(Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null)).getOneTransaction();
        }

        public static Transaction retrieveTransactionByCodeAndTransactionId(String code,
                                                                            String transactionId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_TRANSACTIONID,
                    code, transactionId);
            return new Transaction(Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
        }

        public static Transaction retrieveTransactionByCardIdAndTransactionId(String cardId,
                                                                              String transactionId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CARD_AND_TRANSACTIONID,
                    cardId, transactionId);
            return new Transaction(Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
        }
    }

    public static final class Cards {

        public static CardDetails retrieveCardDetailsByCardId(String cardId) throws AuthorizationException, IOException, CouldNotFindObjectException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_CARD_DETAILS_BASED_ON_CARD,
                    cardId);
            try {
                return new CardDetails(Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static CardDetails retrieveCardDetailsByCode(String code) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(
                    LightrailConstants.API.Endpoints.RETRIEVE_CARD_DETAILS_BASED_ON_CODE,
                    code);
            try {
                return new CardDetails(Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card createCard(Map<String, Object> createCardParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CARD);
            String bodyJsonString = new Gson().toJson((createCardParams));
            try {
                return new Card(Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card retrieveCard(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CARD, cardId);
            try {
                return new Card(Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null));
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static CardSearchResult retrieveCardsOfContact(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT_CARDS, contactId);
            try {
                String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                return new CardSearchResult(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Transaction actionOnCard(String cardId, String action, Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.ACTION_ON_CARD, cardId, action);
            String bodyJsonString = new Gson().toJson((params));

            try {
                String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                return new Transaction(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static Card cancelCard(String cardId, Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CANCEL_CARD, cardId);
            String bodyJsonString = new Gson().toJson((params));

            try {
                String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                return new Card(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static FullCode retrieveCardsFullCode(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_FULL_CODE, contactId);
            try {
                String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                return new FullCode(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }
    }

    public static final class Contact {

        public static com.lightrail.model.api.objects.Contact createContact(Map<String, Object> createContactParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CONTACT);
            String bodyJsonString = new Gson().toJson((createContactParams));
            try {
                String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
                return new com.lightrail.model.api.objects.Contact(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }

        public static com.lightrail.model.api.objects.Contact retrieveContact(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
            String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT, contactId);
            try {
                String rawAPIResponse = Core.getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
                return new com.lightrail.model.api.objects.Contact(rawAPIResponse);
            } catch (InsufficientValueException e) { //never happens
                throw new RuntimeException(e);
            }
        }
    }


}
