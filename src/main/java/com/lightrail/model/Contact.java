package com.lightrail.model;

import java.util.Objects;

public class Contact {
    public String contactId;
    public String userSuppliedId;
    public String email;
    public String firstName;
    public String lastName;
    public String dateCreated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(contactId, contact.contactId) &&
                Objects.equals(userSuppliedId, contact.userSuppliedId) &&
                Objects.equals(email, contact.email) &&
                Objects.equals(firstName, contact.firstName) &&
                Objects.equals(lastName, contact.lastName) &&
                Objects.equals(dateCreated, contact.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactId, userSuppliedId, email, firstName, lastName, dateCreated);
    }
}
