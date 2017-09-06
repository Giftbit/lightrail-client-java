package com.lightrail.model.api.objects;

public class APIError {
    String status;
    String message;
    String messageCode;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
    public String getMessageCode() {
        return messageCode;
    }
}
