package com.lightrail.utils;

import com.lightrail.model.LightrailException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class LightrailConstants {
    public static final class API {

        //        public enum EndpointsEnum {
//            PROGRAMS("programs"),
//            CARDS("cards");
//
//            String endpoint;
//
//            EndpointsEnum(String endpoint) {
//                this.endpoint = endpoint;
//            }
//        }
//
        public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
        public static final String AUTHORIZATION_TOKEN_TYPE = "Bearer";

        public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
        public static final String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";

        public static final String REQUEST_METHOD_GET = "GET";
        public static final String REQUEST_METHOD_POST = "POST";

    }

    private static String urlEncode(String string) throws LightrailException {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new LightrailException("Could not URL-encode the parameter " + string);
        }
    }


}
