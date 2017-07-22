package com.lightrail.model.api;

@JsonObjectRoot("contact")
public class Contact {
   String contactId;
   String userSuppliedId;
   String email;
   String firstName;
   String lastName;
   String dateCreated;

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
}
