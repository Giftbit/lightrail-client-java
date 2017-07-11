package com.lightrail.exceptions;


public class GiftCodeNotActiveException extends Exception {
    public GiftCodeNotActiveException(String message) {
        super(message);
    }

    public GiftCodeNotActiveException(Exception e) {
        super(e);
    }
}
