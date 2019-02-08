package com.lightrail.network;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        if (!Optional.class.isAssignableFrom(typeToken.getRawType()) || !(type instanceof ParameterizedType)) {
            return null;
        }

        ParameterizedType parameterizedType = (ParameterizedType)type;
        Type actualType = parameterizedType.getActualTypeArguments()[0];
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));

        return (TypeAdapter<T>)new OptionalTypeAdapter(adapter);
    }
}
