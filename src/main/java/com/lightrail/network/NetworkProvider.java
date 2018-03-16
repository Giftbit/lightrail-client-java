package com.lightrail.network;

import com.lightrail.model.LightrailException;

public interface NetworkProvider {
    String getAPIResponse(String urlSuffix, String requestMethod, String body) throws LightrailException;

    void handleErrors(int i, String s) throws LightrailException;
}
