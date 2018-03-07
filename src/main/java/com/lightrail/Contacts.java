package com.lightrail;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightrail.model.Contact;
import com.lightrail.model.LightrailException;
import com.lightrail.utils.LightrailConstants;

import java.io.IOException;

import static java.lang.String.format;

public class Contacts {
    public LightrailClient lr;

    public Contacts(LightrailClient lr) {
        this.lr = lr;
    }

    public Contact create() {
        return new Contact();
    }

    public Contact create(String shopperId) {
        return new Contact();
    }

    public Contact retrieve(String contactId) throws IOException, LightrailException {
        String jsonResponse = lr.networkProvider.getAPIResponse(lr.apiKey, format("contacts/%s", contactId), LightrailConstants.API.REQUEST_METHOD_GET, null);

        JsonElement jsonContact = lr.gson.fromJson(jsonResponse, JsonObject.class).get("contact");

        return lr.gson.fromJson(lr.gson.toJson(jsonContact), Contact.class);
    }

    public Contact retrieveByShopperId(String shopperId) {
        return new Contact();
    }

    public Contact retrieveByUserSuppliedId(String shopperId) throws LightrailException {
        return new Contact();
    }
}
