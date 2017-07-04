package com.lightrail.net;

import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 */
public class APICoreTest {

    @Test
    public void pingTest() throws IOException {
        Lightrail.apiKey = "";
        System.out.println(APICore.ping());
    }

}
