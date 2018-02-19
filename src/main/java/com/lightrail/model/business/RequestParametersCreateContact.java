package com.lightrail.model.business;

import com.lightrail.model.api.objects.LightrailObject;

public class RequestParametersCreateContact extends LightrailObject {
    public RequestParametersCreateContact(String jsonObject) {
        super(jsonObject);
    }

    public String userSuppliedId = "";
    public String email = "";
    public String firstName = "";
    public String lastName = "";
}
