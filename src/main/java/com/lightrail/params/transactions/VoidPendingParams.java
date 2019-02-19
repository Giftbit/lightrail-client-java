package com.lightrail.params.transactions;

import java.util.Map;

public class VoidPendingParams {

    public String id;
    public Map<String, Object> metadata;

    public VoidPendingParams() {
    }

    public VoidPendingParams(String id) {
        this.id = id;
    }
}
