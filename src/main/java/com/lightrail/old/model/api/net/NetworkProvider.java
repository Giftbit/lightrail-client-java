package com.lightrail.old.model.api.net;

import com.lightrail.old.exceptions.AuthorizationException;
import com.lightrail.old.exceptions.CouldNotFindObjectException;
import com.lightrail.old.exceptions.InsufficientValueException;

import java.io.IOException;

public interface NetworkProvider {
    String getRawAPIResponse(String urlSuffix, String requestMethod, String body) throws AuthorizationException, IOException, InsufficientValueException, CouldNotFindObjectException;

    void handleErrors(int i, String s) throws AuthorizationException, InsufficientValueException, IOException, CouldNotFindObjectException;
}
