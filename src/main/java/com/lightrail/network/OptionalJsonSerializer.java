package com.lightrail.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalJsonSerializer implements JsonSerializer<Optional<?>> {

    @Override
    public JsonElement serialize(Optional<?> o, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
