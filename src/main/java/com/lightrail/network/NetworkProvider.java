package com.lightrail.network;

import com.lightrail.model.LightrailException;

import java.io.IOException;

public interface NetworkProvider {
    String getAPIResponse(String urlSuffix, String requestMethod, String body) throws LightrailException, IOException;

    void handleErrors(int i, String s) throws LightrailException;
}
