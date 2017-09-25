package com.lightrail.model.api.objects;

public class ValueStore {
    public String valueStoreType;
    public Integer value;
    public String state;
    public String expires;
    public String startDate;
    public String programId;
    public String valueStoreId;
    public String[] restrictions;

    public String getValueStoreType() {
        return valueStoreType;
    }

    public Integer getValue() {
        return value;
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

    public String getProgramId() {
        return programId;
    }

    public String getValueStoreId() {
        return valueStoreId;
    }

    public String[] getRestrictions() {
        return restrictions;
    }
}
