package com.lightrail.exceptions;

/**
 * Created by mohammad on 2017-07-11.
 */
public class ThirdPartyPaymentException extends Exception {
    public ThirdPartyPaymentException (Exception e) {
        super(e);
    }
}
