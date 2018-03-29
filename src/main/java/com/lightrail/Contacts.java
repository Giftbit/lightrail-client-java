package com.lightrail;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightrail.model.Contact;
import com.lightrail.model.LightrailException;
import com.lightrail.params.CreateContactParams;
import com.lightrail.utils.LightrailConstants;

public class Contacts {
    private LightrailClient lr;

    public Contacts(LightrailClient lr) {
        this.lr = lr;
    }

    public Contact create(String shopperId) throws LightrailException {
        CreateContactParams params = new CreateContactParams();
        params.userSuppliedId = shopperId;
        String jsonParams = lr.gson.toJson(params);
        String jsonResponse = lr.networkProvider.getAPIResponse(
                LightrailConstants.API.Endpoints.CREATE_CONTACT,
                LightrailConstants.API.REQUEST_METHOD_POST,
                jsonParams);
        return getSingleContactFromJson(jsonResponse);
    }

    public Contact create(CreateContactParams params) throws LightrailException {
        String jsonParams = lr.gson.toJson(params);
        String jsonResponse = lr.networkProvider.getAPIResponse(
                LightrailConstants.API.Endpoints.CREATE_CONTACT,
                LightrailConstants.API.REQUEST_METHOD_POST,
                jsonParams);
        return getSingleContactFromJson(jsonResponse);
    }

    public Contact retrieve(String contactId) throws LightrailException {
        String jsonResponse = lr.networkProvider.getAPIResponse(
                lr.endpointBuilder.retrieveContactById(contactId),
                LightrailConstants.API.REQUEST_METHOD_GET,
                null);
        return getSingleContactFromJson(jsonResponse);
    }

    public Contact retrieveByShopperId(String shopperId) throws LightrailException {
        return retrieveByUserSuppliedId(shopperId);
    }

    public Contact retrieveByUserSuppliedId(String userSuppliedId) throws LightrailException {
        String jsonResponse = lr.networkProvider.getAPIResponse(
                lr.endpointBuilder.searchContactByUserSuppliedId(userSuppliedId),
                LightrailConstants.API.REQUEST_METHOD_GET,
                null);
        return getFirstContactResultFromJson(jsonResponse);
    }

    private Contact getSingleContactFromJson(String jsonResponse) {
        try {
            JsonElement jsonContact = lr.gson.fromJson(jsonResponse, JsonObject.class).get("contact");
            return lr.gson.fromJson(lr.gson.toJson(jsonContact), Contact.class);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private Contact getFirstContactResultFromJson(String jsonResponse) {
        JsonArray jsonContactResults = lr.gson.fromJson(jsonResponse, JsonObject.class).getAsJsonArray("contacts");
        if (jsonContactResults.size() > 0) {
            JsonElement jsonContact = jsonContactResults.get(0);
            return lr.gson.fromJson(jsonContact, Contact.class);
        } else {
            return null;
        }
    }
}
