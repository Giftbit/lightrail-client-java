package com.lightrail.old.model.api.objects;

import com.lightrail.old.exceptions.BadParameterException;
import com.lightrail.old.exceptions.CouldNotFindObjectException;

public class ContactSearchResult extends LightrailObject {
    public Contact[] contacts;
    public Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public Contact[] getContacts() {
        return contacts;
    }

    public ContactSearchResult() {
    }

    public Contact getOneContact() throws CouldNotFindObjectException {
        if (contacts.length == 1)
            return contacts[0];
        else if (contacts.length > 1)
            throw new BadParameterException("Search results include more than one Contact.");
        else
            throw new CouldNotFindObjectException("Contact does not exists.");
    }
}
