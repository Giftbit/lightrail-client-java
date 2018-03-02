package com.lightrail.old.exceptions;

public class AuthorizationException extends Exception {
    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(Exception e) {
        super(e);
    }
}
