package com.lightrail.exceptions;

public class CurrencyMismatchException extends Exception {

    public CurrencyMismatchException(String message) {
        super(message);
    }
    public CurrencyMismatchException(Exception e)
    {
        super(e);
    }
}
