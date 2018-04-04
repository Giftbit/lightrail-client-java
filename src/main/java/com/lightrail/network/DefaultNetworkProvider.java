package com.lightrail.network;

import com.lightrail.LightrailClient;
import com.lightrail.model.LightrailException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DefaultNetworkProvider implements NetworkProvider {
    private final LightrailClient lr;

    public DefaultNetworkProvider(LightrailClient lr) {
        this.lr = lr;
    }

    public enum HttpMethods {
        GET, POST
    }

    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_TOKEN_TYPE = "Bearer";

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";

    public String get(String urlSuffix) throws LightrailException {
        return makeAPIRequest(urlSuffix, HttpMethods.GET.name(), null);
    }

    public String post(String urlSuffix, String body) throws LightrailException {
        return makeAPIRequest(urlSuffix, HttpMethods.POST.name(), body);
    }

    public String makeAPIRequest(String urlSuffix, String requestMethod, String body) throws LightrailException {
        try {
            URL requestURL = new URL(lr.endpointBuilder.apiBaseURL + urlSuffix);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) requestURL.openConnection();
            httpsURLConnection.setRequestProperty(
                    AUTHORIZATION_HEADER_NAME,
                    AUTHORIZATION_TOKEN_TYPE + " " + lr.apiKey);
            httpsURLConnection.setRequestMethod(requestMethod);

            if (body != null) {
                httpsURLConnection.setRequestProperty(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_JSON_UTF8);
                httpsURLConnection.setDoOutput(true);
                try (OutputStream wr = httpsURLConnection.getOutputStream()) {
                    wr.write(body.getBytes(StandardCharsets.UTF_8));
                    wr.flush();
                    wr.close();
                }
            }
            int responseCode = httpsURLConnection.getResponseCode();

            InputStream responseInputStream;
            if (httpsURLConnection.getResponseCode() < HttpsURLConnection.HTTP_BAD_REQUEST) {
                responseInputStream = httpsURLConnection.getInputStream();
            } else {
                responseInputStream = httpsURLConnection.getErrorStream();
            }

            // todo try() {}
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(responseInputStream, StandardCharsets.UTF_8));
            StringBuilder responseStringBuffer = new StringBuilder();
            String inputLine;
            while ((inputLine = responseReader.readLine()) != null) {
                responseStringBuffer.append(inputLine).append('\n');
            }
            responseReader.close();

            String responseString = responseStringBuffer.toString();

            if (responseCode > 204) {
                handleErrors(responseCode, responseString);
            }

            return responseString;
        } catch (IOException e) {
            throw new LightrailException("There was a problem making that API request: " + e.getMessage());
        }
    }

    @Override
    public void handleErrors(int responseCode, String responseString) throws LightrailException {
        LightrailException error = new LightrailException(responseString);
        String errorMessage = responseString;
        if (error.getMessage() != null)
            errorMessage = error.getMessage();

        throw new LightrailException(String.format("Server responded with %d : %s", responseCode, errorMessage));
    }
}
