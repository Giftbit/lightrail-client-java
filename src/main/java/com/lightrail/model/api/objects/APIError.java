package com.lightrail.model.api.objects;

public class APIError extends LightrailObject{
    public String status;
    public String message;
    public String messageCode;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
    public String getMessageCode() {
        return messageCode;
    }

    public APIError (String jsonObject) {
        super(jsonObject);

    }
}
