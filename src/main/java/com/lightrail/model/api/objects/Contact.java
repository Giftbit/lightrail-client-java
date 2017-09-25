package com.lightrail.model.api.objects;

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
        super(jsonObject);
    }
}
