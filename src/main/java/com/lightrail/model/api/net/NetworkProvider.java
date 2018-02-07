package com.lightrail.model.api.net;

import com.lightrail.exceptions.AuthorizationException;
import com.lightrail.exceptions.CouldNotFindObjectException;
import com.lightrail.exceptions.InsufficientValueException;

import java.io.IOException;

public interface NetworkProvider {
    String getRawAPIResponse(String urlSuffix, String requestMethod, String body) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException;

    void handleErrors(int i, String s) throws AuthorizationException, InsufficientValueException, IOException, CouldNotFindObjectException;
}
