package com.lightrail.old.model.api.objects;

import java.util.HashMap;

public class RequestParameters extends HashMap<String, Object> {
    public RequestParameters() {
        super();
    }

    public RequestParameters(RequestParameters requestParameters) {
        super(requestParameters);
    }

    public RequestParameters(HashMap<String, Object> params) {
        super();
        this.putAll(params);
    }
}
