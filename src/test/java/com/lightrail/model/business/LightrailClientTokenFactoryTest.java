package com.lightrail.model.business;

import com.lightrail.helpers.TestParams;
import com.lightrail.model.Lightrail;
import io.jsonwebtoken.Jwts;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

public class LightrailClientTokenFactoryTest {

    @Test
    public void issueAndVerify() throws IOException {
        Properties properties = TestParams.getProperties();
        Lightrail.apiKey = properties.getProperty("lightrail.testApiKey");
        Lightrail.clientSecret = properties.getProperty("lightrail.clientSecret");

        LightrailClientTokenFactory factory = new LightrailClientTokenFactory();
        String jwt = factory.generate("alice", 5000L);
        Jwts.parser().setSigningKey("secret".getBytes("UTF-8")).parse(jwt);
    }
}
