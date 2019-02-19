package com.lightrail.params.transactions;

import java.util.Map;

public class ReverseParams {

    public String id;
    public Map<String, Object> metadata;

    public ReverseParams() {
    }

    public ReverseParams(String id) {
        this.id = id;
    }
}
