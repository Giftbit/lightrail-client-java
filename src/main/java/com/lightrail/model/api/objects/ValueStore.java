package com.lightrail.model.api.objects;


public class ValueStore {
    Integer currentValue;
    String state;
    String expires;
    String startDate;

    public Integer getCurrentValue() {
        return currentValue;
    }
    public String getState() {
        return state;
    }

    public String getExpires() {
        return expires;
    }

    public String getStartDate() {
        return startDate;
    }
}
