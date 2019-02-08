package com.lightrail.network;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Optional;

public class OptionalTypeAdapter<T> extends TypeAdapter<Optional<T>> {

    private final TypeAdapter<T> adapter;

    public OptionalTypeAdapter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void write(JsonWriter out, Optional<T> value) throws IOException {
        //noinspection OptionalAssignedToNull Yup we're abusing it.
        if (value == null) {
            out.setSerializeNulls(false);
            out.nullValue();
        } else if (value.isPresent()) {
            adapter.write(out, value.get());
        } else {
            out.setSerializeNulls(true);
            out.nullValue();
        }
    }

    @Override
    public Optional<T> read(JsonReader in) throws IOException {
        if (in.peek() != JsonToken.NULL) {
            return Optional.ofNullable(adapter.read(in));
        } else {
            in.nextNull();
            return Optional.empty();
        }
    }
}
