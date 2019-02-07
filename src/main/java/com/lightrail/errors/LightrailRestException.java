package com.lightrail.errors;

import java.util.Map;

public class LightrailRestException extends Exception {

    private String method;
    private String path;
    private int httpStatus;
    private String message;
    private String messageCode;
    private Map<String, Object> body;

    public LightrailRestException(String method, String path, int httpStatus, String message, String messageCode, Map<String, Object> body) {
        this.method = method;
        this.path = path;
        this.httpStatus = httpStatus;
        this.message = message;
        this.messageCode = messageCode;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "LightrailRestException{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", httpStatus=" + httpStatus +
                ", message='" + message + '\'' +
                ", messageCode='" + messageCode + '\'' +
                '}';
    }
}
