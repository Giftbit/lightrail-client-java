package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import com.lightrail.model.Contact;
import com.lightrail.params.CreateContactParams;

import java.io.IOException;

public class Contacts {
    private final LightrailClient lr;

    public Contacts(LightrailClient lr) {
        this.lr = lr;
    }

    public Contact create(String id) throws LightrailRestException, IOException {
        return create(new CreateContactParams(id));
    }

    public Contact create(CreateContactParams params) throws LightrailRestException, IOException {
        return lr.networkProvider.post("/v2/contacts", params, Contact.class);
    }

    public Contact getById(String id) throws LightrailRestException, IOException {
        return lr.networkProvider.get("/v2/contacts", Contact.class);
    }
}
