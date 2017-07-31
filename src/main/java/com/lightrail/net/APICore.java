package com.lightrail.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class APICore {

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
        APIError error = parseFromJson(responseString, APIError.class);
        String errorMessage = responseString;
        if (error != null && error.getMessage() != null)
            errorMessage = error.getMessage();
        switch (responseCode) {
            case 401:
            case 403:
                throw new AuthorizationException(String.format("Authorization error (%d): %s ", responseCode, errorMessage));
            case 404:
                throw new CouldNotFindObjectException(errorMessage);
            case 409:
                throw new BadParameterException(String.format("Idempotency error (%d): %s", responseCode, errorMessage));
            case 400:
                if (errorMessage.contains("Insufficient Value")) {
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

    private static <T> T parseFromJson(String jsonString, Class<T> classOfT) {
        JsonElement jsonElement = new Gson().fromJson(jsonString, JsonElement.class);

        JsonObjectRoot jsonRootAnnotation = classOfT.getAnnotation(JsonObjectRoot.class);
        if (jsonRootAnnotation != null) {
            String jsonRootName = jsonRootAnnotation.value();
            if (!"".equals(jsonRootName)) {
                jsonElement = jsonElement.getAsJsonObject().get(jsonRootName);
            }
        }

        return new Gson().fromJson(jsonElement, classOfT);
    }

    public static Balance balanceCheckByCardId(String cardId) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String rawAPIResponse = getRawAPIResponse(String.format(LightrailConstants.API.Endpoints.CARDS_BALANCE, cardId), LightrailConstants.API.REQUEST_METHOD_GET, null);
        return parseFromJson(rawAPIResponse, Balance.class);
    }

    public static Balance balanceCheckByCode(String code) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String rawAPIResponse = getRawAPIResponse(String.format(LightrailConstants.API.Endpoints.CODES_BALANCE_DETAILS, code), LightrailConstants.API.REQUEST_METHOD_GET, null);
        return parseFromJson(rawAPIResponse, Balance.class);
    }

    public static Transaction postTransactionOnCode(String code, Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.CODES_TRANSACTION, code);
        String bodyJsonString = new Gson().toJson(transactionParams);
        String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
        return parseFromJson(rawAPIResponse, Transaction.class);
    }

    public static Transaction actionOnTransaction(String cardId,
                                                  String transactionId,
                                                  String action,
                                                  Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.ACTION_ON_TRANSACTION, cardId, transactionId, action);
        String bodyJsonString = new Gson().toJson((transactionParams));
        String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
        return parseFromJson(rawAPIResponse, Transaction.class);
    }

    public static Transaction postTransactionOnCard(String cardId, Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.FUND_CARD, cardId);
        String bodyJsonString = new Gson().toJson((transactionParams));
        String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
        return parseFromJson(rawAPIResponse, Transaction.class);
    }

    public static Transaction retrieveTransactionByCodeAndUserSuppliedId(String code, String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        String urlSuffix = String.format(
                LightrailConstants.API.Endpoints.RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_USERSUPPLIEDID,
                code, userSuppliedId);
        String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
        return parseFromJson(rawAPIResponse, TransactionSearchResult.class).getOneTransaction();
    }

    public static Contact createContact(Map<String, Object> createContactParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CONTACT);
        String bodyJsonString = new Gson().toJson((createContactParams));
        try {
            String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return parseFromJson(rawAPIResponse, Contact.class);
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }
    }

    public static Contact retrieveContact(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT, contactId);
        try {
            String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
            return parseFromJson(rawAPIResponse, Contact.class);
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }
    }

    public static void deleteContact (String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT, contactId);
        try {
            String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_DELETE, null);
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }
    }

    public static Card createCard(Map<String, Object> createContactParams) throws AuthorizationException, CouldNotFindObjectException, IOException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.CREATE_CARD);
        String bodyJsonString = new Gson().toJson((createContactParams));
        try {
            String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return parseFromJson(rawAPIResponse, Card.class);
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }
    }

    public static Card retrieveCard(String cardId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CARD, cardId);
        try {
            String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
            return parseFromJson(rawAPIResponse, Card.class);
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }
    }

    public static CardSearchResult retrieveCardsOfContact(String contactId) throws AuthorizationException, CouldNotFindObjectException, IOException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.RETRIEVE_CONTACT_CARDS, contactId);
        try {
            String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_GET, null);
            return parseFromJson(rawAPIResponse, CardSearchResult.class);
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }
    }

    public static Card cancelCard(String cardId, Map<String, Object> params) throws AuthorizationException, CouldNotFindObjectException, IOException {
        String urlSuffix = String.format(LightrailConstants.API.Endpoints.CANCEL_CARD, cardId);
        String bodyJsonString = new Gson().toJson((params));

        try {
            String rawAPIResponse = getRawAPIResponse(urlSuffix, LightrailConstants.API.REQUEST_METHOD_POST, bodyJsonString);
            return parseFromJson(rawAPIResponse, Card.class);
        } catch (InsufficientValueException e) { //never happens
            throw new RuntimeException(e);
        }
    }
}
