package com.lightrail.net;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class APICore {
    public static String ping () throws IOException {
        String httpsURL = Lightrail.apiBaseURL + "ping";
        URL myurl = new URL(httpsURL);
        HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
        con.setRequestProperty ("Authorization", "Bearer " + Lightrail.apiKey);
        BufferedReader responseReader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder responseStringBuffer = new StringBuilder();
        String inputLine;
        while ((inputLine = responseReader.readLine()) != null)
            responseStringBuffer.append(inputLine).append('\n');
        responseReader.close();
        return responseStringBuffer.toString();
    }
}
