package com.lightrail.params;

import com.google.gson.JsonObject;

public class CreateContactParams {
    public String id = "";
    public String firstName = "";
    public String lastName = "";
    public String email = "";
    public JsonObject metadata;

    public CreateContactParams() {
    }

    public CreateContactParams(String id) {
        this.id = id;
    }
}
