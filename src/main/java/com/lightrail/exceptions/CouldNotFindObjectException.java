package com.lightrail.exceptions;

public class CouldNotFindObjectException extends Exception {
    public CouldNotFindObjectException(String message) {
        super(message);
    }
    public CouldNotFindObjectException(Exception e) {
        super(e);
    }

}
