package com.lightrail.model;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class Contact {

    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public Map<String, Object> metadata;
    public Date createdDate;
    public Date updatedDate;
    public String createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id) &&
                Objects.equals(firstName, contact.firstName) &&
                Objects.equals(lastName, contact.lastName) &&
                Objects.equals(email, contact.email) &&
                Objects.equals(metadata, contact.metadata) &&
                Objects.equals(createdDate, contact.createdDate) &&
                Objects.equals(updatedDate, contact.updatedDate) &&
                Objects.equals(createdBy, contact.createdBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, metadata, createdDate, updatedDate, createdBy);
    }
}
