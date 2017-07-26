package com.lightrail.exceptions;


public class CardNotActiveException extends RuntimeException {
    public CardNotActiveException(String message) {
        super(message);
    }
    public CardNotActiveException(Exception e) {
        super(e);
    }
}
