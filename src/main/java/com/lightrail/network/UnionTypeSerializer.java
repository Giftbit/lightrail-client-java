package com.lightrail.network;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Helps GSON deserialize objects following our union type pattern where there is
 * a field with a constant value that identifies the concrete type.
 */
public class UnionTypeSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private final String typeField;
    private final Map<String, Class<? extends T>> concreteClasses = new HashMap<>();

    public UnionTypeSerializer(String typeField) {
        this.typeField = typeField;
    }

    public UnionTypeSerializer<T> addConcreteClass(String value, Class<? extends T> concreteClass) {
        concreteClasses.put(value, concreteClass);
        return this;
    }

    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String typeValue = jsonObject.get(typeField).getAsString();
        if (!concreteClasses.containsKey(typeValue)) {
            throw new JsonParseException(String.format("Cannot deserialize abstract class: unexpected value '%s' for field '%s'.", typeValue, typeField));
        }
        Class<? extends T> concreteClass = concreteClasses.get(typeValue);
        return context.deserialize(json, concreteClass);
    }

    @Override
    public JsonElement serialize(T t, Type type, JsonSerializationContext context) {
        return context.serialize(t, t.getClass());
    }
}
