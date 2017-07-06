package com.lightrail.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.CodeBalance;
import com.lightrail.model.api.Transaction;
import com.lightrail.model.api.JsonObjectRoot;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.lightrail.model.Lightrail.*;


public class APICore {


    private static String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static String AUTHORIZATION_TOKEN_TYPE = "Bearer";

    private static String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";


    private static String REQUEST_METHOD_GET = "GET";
    private static String REQUEST_METHOD_POST = "POST";

    private static String getRawAPIResponse(String urlSuffix, String requestMethod, String body) throws IOException {
        URL requestURL = new URL(Lightrail.apiBaseURL + urlSuffix);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) requestURL.openConnection();
        httpsURLConnection.setRequestProperty(
                AUTHORIZATION_HEADER_NAME,
                AUTHORIZATION_TOKEN_TYPE + " " + Lightrail.apiKey);
        httpsURLConnection.setRequestMethod(requestMethod);

        if (body != null) {
            httpsURLConnection.setRequestProperty(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_JSON_UTF8);
            httpsURLConnection.setDoOutput(true);
            OutputStream wr = httpsURLConnection.getOutputStream();
            //System.err.print(body);
            wr.write(body.getBytes(StandardCharsets.UTF_8));
            wr.flush();
            wr.close();
        }
        int responseCode = httpsURLConnection.getResponseCode();

        if (responseCode != 200)
            throw new IOException("Server responded with " + responseCode);

        BufferedReader responseReader =
                new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder responseStringBuffer = new StringBuilder();
        String inputLine;
        while ((inputLine = responseReader.readLine()) != null)
            responseStringBuffer.append(inputLine).append('\n');
        responseReader.close();
        return responseStringBuffer.toString();
    }

    public static String ping() throws IOException {
        return getRawAPIResponse(PING_ENDPOINT, REQUEST_METHOD_GET, null);
    }

    private static <T> T parseFromJson(String jsonString, Class<T> classOfT) {
        String jsonRootName = classOfT.getAnnotation(JsonObjectRoot.class).value();
        JsonElement jsonElement = new Gson().fromJson(jsonString, JsonElement.class);
        if (jsonRootName != null && !"".equals(jsonRootName)) {
            jsonElement = jsonElement.getAsJsonObject().get(jsonRootName);
        }

        return new Gson().fromJson(jsonElement, classOfT);
    }

    public static CodeBalance balanceCheck(String code) throws IOException {
        String rawAPIResponse = getRawAPIResponse(String.format(CODES_BALANCE_DETAILS_ENDPOINT, code), REQUEST_METHOD_GET, null);
        return parseFromJson(rawAPIResponse, CodeBalance.class);
    }

    public static Transaction postTransactionOnCode(String code, Map<String, Object> transactionParams) throws IOException {
        String urlSuffix = String.format(CODES_TRANSACTION_ENDPOINT, code);
        String bodyJsonString = new Gson().toJson(transactionParams);
        String rawAPIResponse = getRawAPIResponse(urlSuffix, REQUEST_METHOD_POST, bodyJsonString);
        return parseFromJson(rawAPIResponse, Transaction.class);
    }

    public static Transaction finalizeTransaction (String cardId, String transactionId, String finalizationType, Map<String, Object> transactionParams) throws IOException {
        String urlSuffix = String.format(FINALIZE_TRANSACTION_ENDPOINT, cardId, transactionId, finalizationType);
        String bodyJsonString = new Gson().toJson((transactionParams));
        String rawAPIResponse = getRawAPIResponse(urlSuffix, REQUEST_METHOD_POST, bodyJsonString);
        return parseFromJson(rawAPIResponse, Transaction.class);
    }
}
