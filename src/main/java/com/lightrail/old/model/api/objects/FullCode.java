package com.lightrail.old.model.api.objects;

@JsonObjectRoot("fullcode")
public class FullCode extends LightrailObject {
    public String code;

    public String getCode() {
        return code;
    }

    public FullCode(String jsonObject) {
        super(jsonObject);

    }
}
