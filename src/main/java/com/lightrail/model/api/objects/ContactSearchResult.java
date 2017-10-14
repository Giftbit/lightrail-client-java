package com.lightrail.model.api.objects;

import com.google.gson.Gson;
import com.lightrail.exceptions.BadParameterException;
import com.lightrail.exceptions.CouldNotFindObjectException;

public class ContactSearchResult extends LightrailObject{
    public Contact[] contacts;
    public Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public Contact[] getContacts() {
        return contacts;
    }

    public ContactSearchResult (String jsonObject) {
        super(jsonObject);
        for (Contact contact: contacts) {
            String contactJsonString = new Gson().toJson(contact);
            Class<? extends LightrailObject> myClass = Contact.class;
            JsonObjectRoot jsonRootAnnotation = myClass.getAnnotation(JsonObjectRoot.class);
            if (jsonRootAnnotation != null) {
                String jsonRootName = jsonRootAnnotation.value();
                contactJsonString = String.format("{\""+jsonRootName + "\":%s}", contactJsonString);
            }
            contact.setRawJson(contactJsonString);
        }
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
