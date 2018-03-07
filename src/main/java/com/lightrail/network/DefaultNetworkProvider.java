package com.lightrail.network;

import com.lightrail.model.LightrailException;
import com.lightrail.utils.LightrailConstants;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DefaultNetworkProvider implements NetworkProvider {
    public String getAPIResponse(String apiKey, String urlSuffix, String requestMethod, String body) throws LightrailException, IOException {
        URL requestURL = new URL(LightrailConstants.API.apiBaseURL + urlSuffix);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) requestURL.openConnection();
        httpsURLConnection.setRequestProperty(
                LightrailConstants.API.AUTHORIZATION_HEADER_NAME,
                LightrailConstants.API.AUTHORIZATION_TOKEN_TYPE + " " + apiKey);
        httpsURLConnection.setRequestMethod(requestMethod);

        if (body != null) {
            httpsURLConnection.setRequestProperty(LightrailConstants.API.CONTENT_TYPE_HEADER_NAME, LightrailConstants.API.CONTENT_TYPE_JSON_UTF8);
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
        while ((inputLine = responseReader.readLine()) != null)
            responseStringBuffer.append(inputLine).append('\n');
        responseReader.close();

        String responseString = responseStringBuffer.toString();

        if (responseCode > 204) {
            handleErrors(responseCode, responseString);
        }

        return responseString;
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
