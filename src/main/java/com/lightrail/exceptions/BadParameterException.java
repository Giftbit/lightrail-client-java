package com.lightrail.exceptions;

public class BadParameterException extends Exception{
    public BadParameterException(String message) {
        super(message);
    }
    public BadParameterException(Exception e)
    {
        super(e);
    }
}
