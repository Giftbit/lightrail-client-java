package com.lightrail.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.Constants;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class APICore {

    private static String getRawAPIResponse(String urlSuffix, String requestMethod, String body) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        URL requestURL = new URL(Constants.LightrailAPI.apiBaseURL + urlSuffix);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) requestURL.openConnection();
        httpsURLConnection.setRequestProperty(
                Constants.LightrailAPI.AUTHORIZATION_HEADER_NAME,
                Constants.LightrailAPI.AUTHORIZATION_TOKEN_TYPE + " " + Lightrail.apiKey);
        httpsURLConnection.setRequestMethod(requestMethod);

        if (body != null) {
            httpsURLConnection.setRequestProperty(Constants.LightrailAPI.CONTENT_TYPE_HEADER_NAME, Constants.LightrailAPI.CONTENT_TYPE_JSON_UTF8);
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

        if (responseCode > 200) {
            handleErrors(responseCode, responseString);
        }

        return responseString;
    }

    private static void handleErrors(int responseCode, String responseString) throws AuthorizationException, InsufficientValueException, IOException, CouldNotFindObjectException {
        APIError error = parseFromJson(responseString, APIError.class);
        switch (responseCode) {
            case 401:
            case 403:
                throw new AuthorizationException("Authorization error: " + responseCode + "\n" + responseString);
            case 400:
                if (error.getMessage() != null && responseString.contains("Insufficient Value")) {
                    throw new InsufficientValueException();
                }
            case 404:
                throw new CouldNotFindObjectException (responseString);
            default:
                throw new IOException(String.format("Server responded with %d : %s", responseCode, error.getMessage()));
        }

    }

    public static String ping() throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        return getRawAPIResponse(Constants.LightrailAPI.PING_ENDPOINT, Constants.LightrailAPI.REQUEST_METHOD_GET, null);
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

    public static CodeBalance balanceCheck(String code) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String rawAPIResponse = getRawAPIResponse(String.format(Constants.LightrailAPI.CODES_BALANCE_DETAILS_ENDPOINT, code), Constants.LightrailAPI.REQUEST_METHOD_GET, null);
        return parseFromJson(rawAPIResponse, CodeBalance.class);
    }

    public static Transaction postTransactionOnCode(String code, Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String urlSuffix = String.format(Constants.LightrailAPI.CODES_TRANSACTION_ENDPOINT, code);
        String bodyJsonString = new Gson().toJson(transactionParams);
        String rawAPIResponse = getRawAPIResponse(urlSuffix, Constants.LightrailAPI.REQUEST_METHOD_POST, bodyJsonString);
        return parseFromJson(rawAPIResponse, Transaction.class);
    }

    public static Transaction finalizeTransaction(String cardId,
                                                  String transactionId,
                                                  String finalizationType,
                                                  Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String urlSuffix = String.format(Constants.LightrailAPI.FINALIZE_TRANSACTION_ENDPOINT, cardId, transactionId, finalizationType);
        String bodyJsonString = new Gson().toJson((transactionParams));
        String rawAPIResponse = getRawAPIResponse(urlSuffix, Constants.LightrailAPI.REQUEST_METHOD_POST, bodyJsonString);
        return parseFromJson(rawAPIResponse, Transaction.class);
    }

    public static Transaction postTransactionOnCard(String cardId, Map<String, Object> transactionParams) throws IOException, InsufficientValueException, AuthorizationException, CouldNotFindObjectException {
        String urlSuffix = String.format(Constants.LightrailAPI.FUND_CARD_ENDPOINT, cardId);
        String bodyJsonString = new Gson().toJson((transactionParams));
        String rawAPIResponse = getRawAPIResponse(urlSuffix, Constants.LightrailAPI.REQUEST_METHOD_POST, bodyJsonString);
        return parseFromJson(rawAPIResponse, Transaction.class);
    }

    public static Transaction retrieveTransactionByCodeAndUserSuppliedId (String code, String userSuppliedId) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
        String urlSuffix = String.format(
                Constants.LightrailAPI.RETRIEVE_TRANSACTION_BASED_ON_CODE_AND_USERSUPPLIEDID_ENDPOINT,
                code, userSuppliedId);
        String rawAPIResponse = getRawAPIResponse(urlSuffix, Constants.LightrailAPI.REQUEST_METHOD_GET, null);
        return parseFromJson(rawAPIResponse, TransactionSearchResult.class).getOneTransaction();
    }
}
