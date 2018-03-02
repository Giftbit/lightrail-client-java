package com.lightrail.params;

public class CreateAccountCardParams extends CreateCardParams {
    public String shopperId;
    public String currency;
    public int initialValue = 0;
    public String cardType = "ACCOUNT_CARD";
}
