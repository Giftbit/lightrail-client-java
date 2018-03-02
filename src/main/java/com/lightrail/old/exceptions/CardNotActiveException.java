package com.lightrail.old.exceptions;


public class CardNotActiveException extends RuntimeException {
    public CardNotActiveException(String message) {
        super(message);
    }

    public CardNotActiveException(Exception e) {
        super(e);
    }
}
