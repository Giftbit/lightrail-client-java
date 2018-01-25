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

        String jwt = LightrailClientTokenFactory.generate("alice", 50);
        Jwts.parser().setSigningKey(Lightrail.clientSecret.getBytes("UTF-8")).parse(jwt);
    }
}
