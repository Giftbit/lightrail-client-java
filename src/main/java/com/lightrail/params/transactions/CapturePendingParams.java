package com.lightrail.params.transactions;

import java.util.Map;

public class CapturePendingParams {

    public String id;
    public Map<String, Object> metadata;

    public CapturePendingParams() {
    }

    public CapturePendingParams(String id) {
        this.id = id;
    }
}
