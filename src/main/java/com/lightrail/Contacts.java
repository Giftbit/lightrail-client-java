package com.lightrail;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightrail.model.Contact;
import com.lightrail.model.LightrailException;
import com.lightrail.utils.LightrailConstants;

import java.io.IOException;

import static java.lang.String.format;

public class Contacts {
    public LightrailClient lr;
    public Gson gson;

    public Contacts(LightrailClient lr) {
        this.lr = lr;
        this.gson = new Gson();
    }

    public Contact create() {
        return new Contact();
    }

    public Contact retrieve(String contactId) throws IOException, LightrailException {
        String jsonResponse = lr.networkProvider.getAPIResponse(lr.apiKey, format("contacts/%s", contactId), LightrailConstants.API.REQUEST_METHOD_GET, null);

        JsonElement jsonContact = gson.fromJson(jsonResponse, JsonObject.class).get("contact");

        return gson.fromJson(gson.toJson(jsonContact), Contact.class);
    }

    public Contact retrieveByShopperId(String shopperId) {
        return new Contact();
    }

}
