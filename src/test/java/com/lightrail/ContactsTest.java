package com.lightrail;

import com.lightrail.model.Contact;
import com.lightrail.model.PaginatedList;
import com.lightrail.model.Value;
import com.lightrail.params.contacts.*;
import com.lightrail.params.values.CreateValueParams;
import com.lightrail.params.values.CreateValueQueryParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
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
        params.metadata = new HashMap<>();
        params.metadata.put("deepestFear", "spiders");

        Contact contactCreated = lc.contacts.createContact(params);
        assertEquals(params.id, contactCreated.id);
        assertEquals(params.firstName, contactCreated.firstName);
        assertEquals(params.lastName, contactCreated.lastName);
        assertEquals(params.email, contactCreated.email);
        assertEquals(params.metadata, contactCreated.metadata);
        assertNotNull(contactCreated.createdDate);
        assertNotNull(contactCreated.updatedDate);

        Contact contactGetted = lc.contacts.getContact(params.id);
        assertEquals(contactCreated, contactGetted);

        ListContactsParams listContactsParams = new ListContactsParams();
        listContactsParams.id = params.id;
        PaginatedList<Contact> contactList = lc.contacts.listContacts(listContactsParams);
        assertEquals(1, contactList.size());
        assertEquals(contactGetted, contactList.get(0));
    }

    @Test
    public void updateContact() throws Exception {
        CreateContactParams createParams = new CreateContactParams(generateId());
        createParams.firstName = "TesterFace";
        createParams.lastName = "Mctesty Face";
        createParams.email = "tester@face.com";
        createParams.metadata = new HashMap<>();
        createParams.metadata.put("deepestFear", "spiders");

        Contact contactCreated = lc.contacts.createContact(createParams);
        assertEquals(createParams.id, contactCreated.id);

        UpdateContactParams updateParams = new UpdateContactParams();
        updateParams.firstName = Optional.of("Johnny");
        updateParams.email = Optional.empty();
        updateParams.metadata = Optional.of(new HashMap<>());
        updateParams.metadata.get().put("deepestFear", "sharks");
        updateParams.metadata.get().put("kittensOwned", 5.0);
        updateParams.metadata.get().put("moreStuff", new HashMap<String, Object>());

        Contact contactUpdated = lc.contacts.updateContact(contactCreated, updateParams);
        assertEquals(contactCreated.id, contactUpdated.id);
        assertEquals(updateParams.firstName.get(), contactUpdated.firstName);
        assertEquals(contactCreated.lastName, contactUpdated.lastName);
        assertNull(contactUpdated.email);
        assertEquals(updateParams.metadata.get(), contactUpdated.metadata);
        assertNotEquals(contactCreated, contactUpdated);
    }

    @Test
    public void paginateContacts() throws Exception {
        ListContactsParams params = new ListContactsParams();
        params.limit = 1;

        PaginatedList<Contact> contactsStart = lc.contacts.listContacts(params);
        assertEquals(1, contactsStart.size());
        assertFalse(contactsStart.hasFirst());
        assertFalse(contactsStart.hasPrevious());
        assertTrue(contactsStart.hasNext());
        assertTrue(contactsStart.hasLast());

        PaginatedList<Contact> contactsNext = contactsStart.getNext();
        assertEquals(1, contactsNext.size());
        assertTrue(contactsNext.hasFirst());
        assertTrue(contactsNext.hasPrevious());
        assertTrue(contactsNext.hasNext());
        assertTrue(contactsNext.hasLast());

        PaginatedList<Contact> contactsPrev = contactsNext.getPrevious();
        assertEquals(1, contactsPrev.size());
        assertEquals(contactsStart.get(0), contactsPrev.get(0));
        assertTrue(contactsPrev.hasNext());
        assertTrue(contactsPrev.hasLast());

        PaginatedList<Contact> contactsFirst = contactsNext.getFirst();
        assertEquals(1, contactsFirst.size());
        assertEquals(contactsStart.get(0), contactsFirst.get(0));
        assertFalse(contactsFirst.hasFirst());
        assertFalse(contactsFirst.hasPrevious());
        assertTrue(contactsFirst.hasNext());
        assertTrue(contactsFirst.hasLast());

        PaginatedList<Contact> contactsLast = contactsNext.getLast();
        assertEquals(1, contactsLast.size());
        assertTrue(contactsLast.hasFirst());
        assertTrue(contactsLast.hasPrevious());
        assertFalse(contactsLast.hasNext());
        assertFalse(contactsLast.hasLast());
    }

    @Test
    public void attachValueAndGetContactValues() throws Exception {
        CreateContactParams createContactParams = new CreateContactParams(generateId());
        createContactParams.firstName = "Testado";
        createContactParams.lastName = "Testington";
        Contact contactCreated = lc.contacts.createContact(createContactParams);
        assertNull(contactCreated.metadata);

        assertEquals(createContactParams.id, contactCreated.id);

        CreateValueParams createValueParams = new CreateValueParams(generateId());
        createValueParams.code = generateId();
        createValueParams.currency = "USD";
        createValueParams.balance = 500;
        CreateValueQueryParams createValueQueryParams = new CreateValueQueryParams();
        createValueQueryParams.showCode = true;
        Value valueCreated = lc.values.createValue(createValueParams, createValueQueryParams);

        assertEquals(createValueParams.id, valueCreated.id);
        assertEquals(createValueParams.code, valueCreated.code);
        assertEquals(createValueParams.balance, valueCreated.balance);
        assertNull(valueCreated.contactId);

        AttachContactToValueParams attachContactToValueParams = new AttachContactToValueParams();
        attachContactToValueParams.valueId = valueCreated.id;
        Value valueAttached = lc.contacts.attachContactToValue(contactCreated, attachContactToValueParams);

        assertEquals(valueCreated.id, valueAttached.id);
        assertEquals(valueCreated.balance, valueAttached.balance);
        assertEquals(contactCreated.id, valueAttached.contactId);
        assertEquals(valueCreated.metadata, valueAttached.metadata);

        PaginatedList<Value> contactValues = lc.contacts.listContactsValues(contactCreated);
        assertEquals(1, contactValues.size());
        assertEquals(valueCreated.id, contactValues.get(0).id);

        PaginatedList<Contact> valueContacts = lc.values.listValuesAttachedContacts(valueCreated);
        assertEquals(1, valueContacts.size());
        assertEquals(contactCreated, valueContacts.get(0));
    }
}
