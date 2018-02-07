package com.lightrail.model.api.net;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;
import com.lightrail.helpers.LightrailConstants;
import com.lightrail.model.Lightrail;
import com.lightrail.model.api.objects.APIError;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DefaultNetworkProvider implements NetworkProvider {
    @Override
    public String getRawAPIResponse(String urlSuffix, String requestMethod, String body) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException {
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

    @Override
    public void handleErrors(int responseCode, String responseString) throws AuthorizationException, InsufficientValueException, IOException, CouldNotFindObjectException {
        APIError error = new APIError(responseString);
        String errorMessage = responseString;
        String errorMessageCode = "";
        if (error.getMessage() != null)
            errorMessage = error.getMessage();
        if (error.getMessageCode() != null)
            errorMessageCode = error.getMessageCode();

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
}
