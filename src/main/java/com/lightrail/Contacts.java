package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.Contact;
import com.lightrail.model.PaginatedList;
import com.lightrail.params.CreateContactParams;
import com.lightrail.params.ListContactsParams;

import java.io.IOException;

import static com.lightrail.network.NetworkUtils.*;

public class Contacts {

    private final LightrailClient lr;

    public Contacts(LightrailClient lr) {
        this.lr = lr;
    }

    public Contact create(String id) throws IOException, LightrailRestException {
        return create(new CreateContactParams(id));
    }

    public Contact create(CreateContactParams params) throws IOException, LightrailRestException {
        return lr.networkProvider.post("/contacts", params, Contact.class);
    }

    public Contact getById(String id) throws IOException, LightrailRestException {
        return lr.networkProvider.get(String.format("/contacts/%s", urlEncode(id)), Contact.class);
    }

    public PaginatedList<Contact> listContacts() throws IOException, LightrailRestException {
        return listContacts(null);
    }

    public PaginatedList<Contact> listContacts(ListContactsParams params) throws IOException, LightrailRestException {
        return lr.networkProvider.getPaginatedList(String.format("/contacts%s", toQueryString(params)), Contact.class);
    }
}
