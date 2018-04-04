package com.lightrail.network;

import com.lightrail.model.LightrailException;

public interface NetworkProvider {
    String makeAPIRequest(String urlSuffix, String requestMethod, String body) throws LightrailException;

    void handleErrors(int i, String s) throws LightrailException;

    String get(String urlQuery) throws LightrailException;

    String post(String urlQuery, String body) throws LightrailException;
}
