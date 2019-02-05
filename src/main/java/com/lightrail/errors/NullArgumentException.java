package com.lightrail.errors;

public class NullArgumentException extends IllegalArgumentException {
    public NullArgumentException(String argumentName) {
        super(String.format("%s cannot be null.", argumentName == null ? "Argument" : argumentName));
    }

    public static void check(Object value, String arugmentName) {
        if (value == null) {
            throw new NullArgumentException(arugmentName);
        }
    }
}
