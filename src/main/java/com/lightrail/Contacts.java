package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.model.Contact;
import com.lightrail.model.PaginatedList;
import com.lightrail.params.contacts.*;

import java.io.IOException;
import java.util.Map;

import static com.lightrail.network.NetworkUtils.*;

public class Contacts {

    private final LightrailClient lr;

    public Contacts(LightrailClient lr) {
        this.lr = lr;
    }

    public Contact createContact(String contactId) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");

        return createContact(new CreateContactParams(contactId));
    }

    public Contact createContact(CreateContactParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post("/contacts", params, Contact.class);
    }

    public Contact getContactById(String contactId) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");

        return lr.networkProvider.get(String.format("/contacts/%s", urlEncode(contactId)), Contact.class);
    }

    public PaginatedList<Contact> listContacts() throws IOException, LightrailRestException {
        return lr.networkProvider.getPaginatedList("/contacts", Contact.class);
    }

    public PaginatedList<Contact> listContacts(ListContactsParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/contacts%s", toQueryString(params)), Contact.class);
    }

    public PaginatedList<Contact> listContacts(Map<String, String> params) throws IOException, LightrailRestException {
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/contacts%s", toQueryString(params)), Contact.class);
    }

    public Contact updateContact(Contact contact, UpdateContactParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(contact, "contact");
        NullArgumentException.check(params, "params");

        return updateContact(contact.id, params);
    }

    public Contact updateContact(String contactId, UpdateContactParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.patch(String.format("/contacts/%s", urlEncode(contactId)), params, Contact.class);
    }
}
