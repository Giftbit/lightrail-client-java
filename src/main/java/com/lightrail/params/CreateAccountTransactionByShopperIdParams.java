package com.lightrail.params;

public class CreateAccountTransactionByShopperIdParams {
    public String shopperId;
    public String currency;
    public String userSuppliedId;
    public int value;
    public boolean pending = false;
    public boolean dryRun = false;
    public boolean nsf = true;
}
