package com.lightrail.exceptions;

public class InsufficientValueException extends Exception {
    public InsufficientValueException() {}
    public InsufficientValueException(String message) {
        super(message);
    }
    public InsufficientValueException(Exception e) {
        super(e);
    }
}
