package com.lightrail.params.contacts;

import com.google.gson.JsonElement;

import java.util.Map;
import java.util.Optional;

/**
 * Update a Contact.  Leave fields as null to not change them,
 * set to Optional.empty() to set to null, set Optional.of(value)
 * to change.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class UpdateContactParams {
    public Optional<String> firstName;
    public Optional<String> lastName;
    public Optional<String> email;
    public Optional<Map<String, JsonElement>> metadata;
}
