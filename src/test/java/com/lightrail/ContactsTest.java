package com.lightrail;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lightrail.model.Contact;
import com.lightrail.model.PaginatedList;
import com.lightrail.params.contacts.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.lightrail.TestUtils.generateId;
import static com.lightrail.TestUtils.getLightrailClient;
import static org.junit.Assert.*;

public class ContactsTest {

    private LightrailClient lc;

    @Before
    public void setUp() {
        lc = getLightrailClient();
    }

    @After
    public void tearDown() {
        lc = null;
    }

    @Test
    public void createGetAndListOneContact() throws Exception {
        CreateContactParams params = new CreateContactParams(generateId());
        params.firstName = "TesterFace";
        params.lastName = "Mctesty Face";
        params.email = "tester@face.com";
        params.metadata = new JsonObject();
        params.metadata.add("deepestFear", new JsonPrimitive("spiders"));

        Contact contactCreated = lc.contacts.createContact(params);
        assertEquals(params.id, contactCreated.id);
        assertEquals(params.firstName, contactCreated.firstName);
        assertEquals(params.lastName, contactCreated.lastName);
        assertEquals(params.email, contactCreated.email);
        assertEquals(params.metadata.get("deepestFear"), contactCreated.metadata.get("deepestFear"));
        assertNotNull(contactCreated.createdDate);
        assertNotNull(contactCreated.updatedDate);

        Contact contactGetted = lc.contacts.getContact(params.id);
        assertEquals(params.id, contactGetted.id);
        assertEquals(params.firstName, contactGetted.firstName);
        assertEquals(params.lastName, contactGetted.lastName);
        assertEquals(params.email, contactGetted.email);
        assertEquals(params.metadata.get("deepestFear"), contactGetted.metadata.get("deepestFear"));
        assertNotNull(contactGetted.createdDate);
        assertNotNull(contactGetted.updatedDate);

        ListContactsParams listContactsParams = new ListContactsParams();
        listContactsParams.id = params.id;
        PaginatedList<Contact> contactList = lc.contacts.listContacts(listContactsParams);
        assertEquals(1, contactList.size());
        assertEquals(params.id, contactList.get(0).id);
    }

    @Test
    public void updateContact() throws Exception {
        CreateContactParams createParams = new CreateContactParams(generateId());
        createParams.firstName = "TesterFace";
        createParams.lastName = "Mctesty Face";
        createParams.email = "tester@face.com";
        createParams.metadata = new JsonObject();
        createParams.metadata.add("deepestFear", new JsonPrimitive("spiders"));

        Contact contactCreated = lc.contacts.createContact(createParams);
        assertEquals(createParams.id, contactCreated.id);

        UpdateContactParams updateParams = new UpdateContactParams();
        updateParams.firstName = Optional.of("Johnny");
        updateParams.email = Optional.empty();
        updateParams.metadata = Optional.of(new JsonObject());
        updateParams.metadata.get().add("deepestFear", new JsonPrimitive("sharks"));

        Contact contactUpdated = lc.contacts.updateContact(contactCreated, updateParams);
        assertEquals(contactCreated.id, contactUpdated.id);
        assertEquals(updateParams.firstName.get(), contactUpdated.firstName);
        assertEquals(contactCreated.lastName, contactUpdated.lastName);
        assertNull(contactUpdated.email);
        assertEquals(updateParams.metadata.get().get("deepestFear"), contactUpdated.metadata.get("deepestFear"));
        assertNotEquals(contactCreated, contactUpdated);
    }
}
