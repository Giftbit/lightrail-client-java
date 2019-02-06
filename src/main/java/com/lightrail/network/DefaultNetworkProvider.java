package com.lightrail.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.lightrail.LightrailClient;
import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.model.LightrailErrorBody;
import com.lightrail.model.PaginatedList;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DefaultNetworkProvider implements NetworkProvider {

    private final LightrailClient lr;
    private final Map<String, String> additionalHeaders = new HashMap<>();
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    private final Map<Type, Type> listTypeMap = new HashMap<>();
    private String restRoot = "https://api.lightrail.com/v2";

    public DefaultNetworkProvider(LightrailClient lr) {
        this.lr = lr;
    }

    public String getRestRoot() {
        return restRoot;
    }

    public void setRestRoot(String restRoot) {
        this.restRoot = restRoot;
    }

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public <T> T get(String path, Class<T> responseType) throws LightrailRestException, IOException {
        return request("GET", path, null, responseType);
    }

    public <T> PaginatedList<T> getPaginatedList(String path, Class<T> elementType) throws LightrailRestException, IOException {
        return requestPaginatedList("GET", path, null, elementType);
    }

    public <T> T post(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException {
        return request("POST", path, body, responseType);
    }

    public <T> T patch(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException {
        return request("PATCH", path, body, responseType);
    }

    public <T> T put(String path, Object body, Class<T> responseType) throws LightrailRestException, IOException {
        return request("PUT", path, body, responseType);
    }

    public <T> T request(String method, String path, Object body, Class<T> responseType) throws LightrailRestException, IOException {
        HttpsURLConnection httpsURLConnection = getConnection(method, path, body);
        int responseCode = httpsURLConnection.getResponseCode();
        String responseString = readConnectionResponse(httpsURLConnection);
        handleError(method, path, responseCode, responseString);
        return gson.fromJson(responseString, responseType);
    }

    public <T> PaginatedList<T> requestPaginatedList(String method, String path, Object body, Class<T> elementType) throws LightrailRestException, IOException {
        HttpsURLConnection httpsURLConnection = getConnection(method, path, body);
        int responseCode = httpsURLConnection.getResponseCode();
        String responseString = readConnectionResponse(httpsURLConnection);
        handleError(method, path, responseCode, responseString);

        Type listType;
        if (listTypeMap.containsKey(elementType)) {
            listType = listTypeMap.get(elementType);
        } else {
            listType = TypeToken.getParameterized(PaginatedList.class, elementType).getType();
            listTypeMap.put(elementType, listType);
        }
        PaginatedList<T> list = gson.fromJson(responseString, listType);

        String links = httpsURLConnection.getHeaderField("Link");
        if (links != null) {
            list.setLinks(links, this, elementType);
        }

        String maxLimit = httpsURLConnection.getHeaderField("Max-Limit");
        if (maxLimit != null) {
            list.setMaxLimit(Integer.parseInt(maxLimit));
        }

        return list;
    }

    private HttpsURLConnection getConnection(String method, String path, Object body) throws IOException {
        NullArgumentException.check(method, "method");
        NullArgumentException.check(path, "path");

        URL requestURL = new URL(restRoot + (path.startsWith("/") ? "" : "/") + path);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) requestURL.openConnection();
        httpsURLConnection.setRequestProperty("Authorization", "Bearer " + lr.getApiKey());
        if (method.equals("PATCH")) {
            // Throws `java.net.ProtocolException: Invalid HTTP method: PATCH` if we use PATCH.
            // PATCH is a perfectly valid method.  Fortunately the web is familiar with
            // gross workarounds and has a solution ready made.
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        } else {
            httpsURLConnection.setRequestMethod(method);
        }
        for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
            httpsURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        if (body != null) {
            String jsonBody = gson.toJson(body);
            httpsURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpsURLConnection.setDoOutput(true);
            try (OutputStream wr = httpsURLConnection.getOutputStream()) {
                wr.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                wr.flush();
            }
        }

        return httpsURLConnection;
    }

    private String readConnectionResponse(HttpsURLConnection httpsURLConnection) throws IOException {
        InputStream responseInputStream;
        if (httpsURLConnection.getResponseCode() < HttpsURLConnection.HTTP_BAD_REQUEST) {
            responseInputStream = httpsURLConnection.getInputStream();
        } else {
            responseInputStream = httpsURLConnection.getErrorStream();
        }

        BufferedReader responseReader = new BufferedReader(new InputStreamReader(responseInputStream, StandardCharsets.UTF_8));
        StringBuilder responseStringBuffer = new StringBuilder();
        String inputLine;
        while ((inputLine = responseReader.readLine()) != null) {
            responseStringBuffer.append(inputLine).append('\n');
        }
        responseReader.close();

        return responseStringBuffer.toString();
    }

    private void handleError(String method, String path, int responseCode, String responseString) throws LightrailRestException {
        if (responseCode < 300) {
            return;
        }

        String message = "";
        String messageCode = "";
        JsonObject body = null;

        try {
            LightrailErrorBody errorBody = gson.fromJson(responseString, LightrailErrorBody.class);
            message = errorBody.message;
            messageCode = errorBody.messageCode;
            body = gson.fromJson(responseString, JsonObject.class);
        } catch (Error e) {
            message = responseString;
        }

        throw new LightrailRestException(method, path, responseCode, message, messageCode, body);
    }
}
