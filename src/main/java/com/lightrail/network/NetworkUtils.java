package com.lightrail.network;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetworkUtils {

    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toQueryString(Map<String, String> params) {
        if (params == null) {
            return "";
        }

        StringBuilder queryString = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() == 0) {
                queryString.append("?");
            } else {
                queryString.append("&");
            }
            queryString.append(entry.getKey());
            queryString.append("=");
            queryString.append(urlEncode(entry.getValue()));
        }

        return queryString.toString();
    }

    public static String toQueryString(Object params) {
        if (params == null) {
            return "";
        }

        StringBuilder queryString = new StringBuilder();

        for (Field field : params.getClass().getDeclaredFields()) {
            try {
                String key = field.getName();
                Object value = field.get(params);
                if (value == null) {
                    continue;
                }

                if (queryString.length() == 0) {
                    queryString.append("?");
                } else {
                    queryString.append("&");
                }
                queryString.append(key);
                queryString.append("=");

                if (value instanceof List) {
                    //noinspection unchecked
                    queryString.append(((List<Object>) value).stream()
                            .map(String::valueOf)
                            .map(NetworkUtils::urlEncode)
                            .collect(Collectors.joining(", ")));
                } else {
                    queryString.append(urlEncode(String.valueOf(value)));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unexpected private field in params object:", e);
            }
        }

        return queryString.toString();
    }
}
