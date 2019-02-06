package com.lightrail.params.contacts;

import com.google.gson.JsonElement;

import java.util.Map;

public class CreateContactParams {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public Map<String, JsonElement> metadata;

    public CreateContactParams() {
    }

    public CreateContactParams(String id) {
        this.id = id;
    }
}
