package com.lightrail.params.contacts;

import java.util.Map;

public class CreateContactParams {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public Map<String, Object> metadata;

    public CreateContactParams() {
    }

    public CreateContactParams(String id) {
        this.id = id;
    }
}
