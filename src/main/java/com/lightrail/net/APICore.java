package com.lightrail.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javafx.scene.effect.Light;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static com.lightrail.net.Lightrail.CODES_BALANCE_DETAILS_ENDPOINT;
import static com.lightrail.net.Lightrail.CODES_TRANSACTION_ENDPOINT;
import static com.lightrail.net.Lightrail.PING_ENDPOINT;


public class APICore {

    private static String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static String AUTHORIZATION_TOKEN_TYPE = "Bearer";

    private static String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";


    private static String REQUEST_METHOD_GET = "GET";
    private static String REQUEST_METHOD_POST = "POST";

    private static JsonObject getRawAPIResponse(String urlSuffix, String requestMethod, JsonObject body) throws IOException {
        URL requestURL = new URL(Lightrail.apiBaseURL + urlSuffix);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) requestURL.openConnection();
        httpsURLConnection.setRequestProperty(
                AUTHORIZATION_HEADER_NAME,
                AUTHORIZATION_TOKEN_TYPE + " " + Lightrail.apiKey);
        httpsURLConnection.setRequestMethod(requestMethod);

        if (body != null)
        {
            httpsURLConnection.setRequestProperty(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_JSON_UTF8);
            httpsURLConnection.setDoOutput(true);
            OutputStream wr = httpsURLConnection.getOutputStream();
            System.err.print(new Gson().toJson(body));
            wr.write(new Gson().toJson(body).getBytes(StandardCharsets.UTF_8));
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
        return new Gson().fromJson(responseStringBuffer.toString(), JsonObject.class);
    }

    public static JsonObject ping() throws IOException {
        return getRawAPIResponse(PING_ENDPOINT, REQUEST_METHOD_GET, null);
    }

    public static JsonObject balanceCheck(String code) throws IOException {
        return getRawAPIResponse(String.format(CODES_BALANCE_DETAILS_ENDPOINT, code), REQUEST_METHOD_GET, null);
    }

    public static JsonObject postTransactionOnCode (String code, int amount, String currency, String userSuppliedId) throws IOException {
        String urlSuffix = String.format(CODES_TRANSACTION_ENDPOINT, code);
        JsonObject bodyJsonObject = new JsonObject();

        bodyJsonObject.add("value", new JsonPrimitive(amount));
        bodyJsonObject.add("currency", new JsonPrimitive(currency));
        bodyJsonObject.add("userSuppliedId", new JsonPrimitive(userSuppliedId));

        return getRawAPIResponse(urlSuffix, REQUEST_METHOD_POST, bodyJsonObject);

    }
}
