package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.errors.NullArgumentException;
import com.lightrail.model.Contact;
import com.lightrail.model.PaginatedList;
import com.lightrail.model.Value;
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

    public Contact getContact(String contactId) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");

        return lr.networkProvider.get(String.format("/contacts/%s", encodeUriComponent(contactId)), Contact.class);
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

    public PaginatedList<Value> listContactsValues(String contactId) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");

        return lr.networkProvider.getPaginatedList(String.format("/contacts/%s/values", encodeUriComponent(contactId)), Value.class);
    }

    public PaginatedList<Value> listContactsValues(String contactId, ListContactsValuesParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/contacts/%s/values%s", encodeUriComponent(contactId), toQueryString(params)), Value.class);
    }

    public PaginatedList<Value> listContactsValues(String contactId, Map<String, String> params) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.getPaginatedList(String.format("/contacts/%s/values%s", encodeUriComponent(contactId), toQueryString(params)), Value.class);
    }

    public PaginatedList<Value> listContactsValues(Contact contact) throws IOException, LightrailRestException {
        NullArgumentException.check(contact, "contact");

        return listContactsValues(contact.id);
    }

    public PaginatedList<Value> listContactsValues(Contact contact, ListContactsValuesParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(contact, "contact");
        NullArgumentException.check(params, "params");

        return listContactsValues(contact.id, params);
    }

    public PaginatedList<Value> listContactsValues(Contact contact, Map<String, String> params) throws IOException, LightrailRestException {
        NullArgumentException.check(contact, "contact");
        NullArgumentException.check(params, "params");

        return listContactsValues(contact.id, params);
    }

    public Contact updateContact(String contactId, UpdateContactParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.patch(String.format("/contacts/%s", encodeUriComponent(contactId)), params, Contact.class);
    }

    public Contact updateContact(Contact contact, UpdateContactParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(contact, "contact");
        NullArgumentException.check(params, "params");

        return updateContact(contact.id, params);
    }

    public void deleteContact(String contactId) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");

        lr.networkProvider.delete(String.format("/contacts/%s", encodeUriComponent(contactId)), Object.class);
    }

    public void deleteContact(Contact contact) throws IOException, LightrailRestException {
        NullArgumentException.check(contact, "contact");

        deleteContact(contact.id);
    }

    public Value attachContactToValue(String contactId, AttachContactToValueParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(contactId, "contactId");
        NullArgumentException.check(params, "params");

        return lr.networkProvider.post(String.format("/contacts/%s/values/attach", encodeUriComponent(contactId)), params, Value.class);
    }

    public Value attachContactToValue(Contact contact, AttachContactToValueParams params) throws IOException, LightrailRestException {
        NullArgumentException.check(contact, "contact");
        NullArgumentException.check(params, "params");

        return attachContactToValue(contact.id, params);
    }
}
