package com.lightrail.exceptions;

public class BadParameterException extends RuntimeException{
    public BadParameterException(String message) {
        super(message);
    }
    public BadParameterException(Exception e)
    {
        super(e);
    }
}
