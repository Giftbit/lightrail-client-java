package com.lightrail.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PaginatedListTest {

    @Test
    public void parsesLinksAtStart() {
        String links = "</v2/values?limit=2&after=eyJpZCI6ImF2YWx1ZXRvY2FuY2VsIiwic29ydCI6IjIwMTktMDItMDFUMTY6MDg6MzMuMDAwWiJ9>; rel=\"next\",</v2/values?limit=2&last=true>; rel=\"last\"";

        PaginatedList<Contact> list = new PaginatedList<>();
        list.setLinks(links, null, Contact.class);

        assertFalse(list.hasFirst());
        assertFalse(list.hasPrevious());
        assertTrue(list.hasNext());
        assertTrue(list.hasLast());
    }

    @Test
    public void parsesLinksAtMiddle() {
        String links = "</v2/values?limit=2>; rel=\"first\",</v2/values?limit=2&before=eyJpZCI6ImNoXzFEYnltTkJDdkJpR2M3U2RMMGIzS041TyIsInNvcnQiOiIyMDE4LTExLTI5VDIzOjE4OjQ1LjAwMFoifQ__>; rel=\"prev\",</v2/values?limit=2&after=eyJpZCI6ImVtb2ppQ29kZSIsInNvcnQiOiIyMDE4LTExLTE0VDIxOjU4OjM4LjAwMFoifQ__>; rel=\"next\",</v2/values?limit=2&last=true>; rel=\"last\"";

        PaginatedList<Contact> list = new PaginatedList<>();
        list.setLinks(links, null, Contact.class);

        assertTrue(list.hasFirst());
        assertTrue(list.hasPrevious());
        assertTrue(list.hasNext());
        assertTrue(list.hasLast());
    }
}
