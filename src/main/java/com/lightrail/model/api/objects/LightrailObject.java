package com.lightrail.model.api.objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lightrail.exceptions.BadParameterException;

import java.lang.reflect.Field;

public class LightrailObject {

    String rawJson;

    public String getRawJson() {
        return rawJson;
    }
    public void setRawJson(String rawJson) {this.rawJson = rawJson;}

    LightrailObject(){
    }

    public LightrailObject (String jsonString) {
        try {
            this.rawJson = jsonString;
            JsonObject jsonObject = (JsonObject) new Gson().fromJson(jsonString, JsonElement.class);

            Class<? extends LightrailObject> myClass = this.getClass();
            JsonObjectRoot jsonRootAnnotation = myClass.getAnnotation(JsonObjectRoot.class);
            Class<?> superclass = myClass;
            while (jsonRootAnnotation == null && superclass != Object.class) {
                jsonRootAnnotation = superclass.getAnnotation(JsonObjectRoot.class);
                superclass = superclass.getSuperclass();
            }
            String jsonRootName="";
            if (jsonRootAnnotation != null) {
                jsonRootName = jsonRootAnnotation.value();
                if (!"".equals(jsonRootName)) {
                    jsonObject = (JsonObject) jsonObject.get(jsonRootName);
                }
            }
            if (jsonObject == null)
                throw new BadParameterException(String.format("Invalid JSON object (must have '%s' key).", jsonRootName));
            Field[] fields = myClass.getFields();
            Gson gson = new Gson();
            for (Field field : fields) {
                Field t = myClass.getField(field.getName());
                t.set(this, gson.fromJson(jsonObject.get(field.getName()), field.getType()));
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
