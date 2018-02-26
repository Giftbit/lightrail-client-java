package com.lightrail.model.api.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lightrail.exceptions.BadParameterException;

@JsonObjectRoot("contact")
public class Contact extends LightrailObject {
    public String contactId;
    public String userSuppliedId;
    public String email;
    public String firstName;
    public String lastName;
    public String dateCreated;

    public String getContactId() {
        return contactId;
    }

    public String getUserSuppliedId() {
        return userSuppliedId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public Contact(String jsonObject) {
        JsonObject jsonContact = new Gson().fromJson(jsonObject, JsonObject.class);
        Contact contact = null;
        if ((jsonContact.get("contact") != null)) {
            String nestedContact = jsonContact.get("contact").getAsString();
            new Gson().fromJson(nestedContact, Contact.class);
        } else if (jsonContact.get("contactId") != null) {
            new Gson().fromJson(jsonObject, Contact.class);
        } else {
            throw new BadParameterException("Could not create contact from JSON: " + jsonObject);
        }
    }

    public Contact() {}
}
