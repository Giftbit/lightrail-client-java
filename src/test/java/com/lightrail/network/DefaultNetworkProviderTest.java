package com.lightrail.network;

import com.lightrail.LightrailClient;
import com.lightrail.errors.LightrailRestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.lightrail.TestUtils.getLightrailClient;
import static org.junit.Assert.*;

public class DefaultNetworkProviderTest {

    private LightrailClient lc;

    @Before
    public void setUp() {
        lc = getLightrailClient();
    }

    @After
    public void tearDown() {
        lc = null;
    }

    @Test
    public void createsGoodRestExceptions() throws Exception {
        DefaultNetworkProvider net = (DefaultNetworkProvider) lc.getNetworkProvider();

        LightrailRestException ex = null;
        try {
            net.get("/thisisnotapaththatexists", Object.class);
        } catch (LightrailRestException e) {
            ex = e;
        }

        assertNotNull(ex);
        assertNotNull(ex.getMessage());
        assertEquals("/thisisnotapaththatexists", ex.getPath());
        assertEquals(404, ex.getHttpStatus());
        assertEquals(ex.getMessage(), ex.getBody().get("message"));
    }
}
