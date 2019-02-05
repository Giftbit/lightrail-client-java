package com.lightrail.network;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NetworkUtils {

    public static String urlEncode(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, "UTF-8");
    }
}
