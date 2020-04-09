package com.lightrail;

import com.lightrail.errors.LightrailRestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.lightrail.TestUtils.getLightrailClient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WebhooksTest {

    private LightrailClient lc;

    private String payload = "{\"id\":\"1\",\"nested\":{\"num\":1,\"bool\":false}}";
    private String secret = "ABCDE";
    private String goodSignature = "828c719f4e058351f153d76221ebff6ab240cc20ed56ca54783dcf766e6eb3c9";

    @Before
    public void setUp() {
        lc = getLightrailClient();
    }

    @After
    public void tearDown() {
        lc = null;
    }

    @Test
    public void verifySignature() throws IOException, LightrailRestException {
        // can validate a good signature
        assertEquals( true, lc.webhooks.verifySignature(goodSignature, payload, secret));

        // can validate a good and bad signature
        assertEquals(true, lc.webhooks.verifySignature(goodSignature.concat(",bad"), payload, secret));

        // can validate a bad and good signature
        assertEquals(true, lc.webhooks.verifySignature("bad,".concat(goodSignature), payload, secret));

        // can invalidate a bad signature
        assertEquals(false, lc.webhooks.verifySignature("bad", payload, secret));

        // can invalidate two bad signatures
        assertEquals(false, lc.webhooks.verifySignature("bad,alsoBad", payload, secret));

        // can't validate without providing signatureHeader
        try {
            lc.webhooks.verifySignature(null, payload, secret);
            fail();
        } catch (Exception e) {
            // do nothing
        }

        // can't validate without providing secret
        try {
            lc.webhooks.verifySignature(goodSignature, payload, null);
            fail();
        } catch (Exception e) {
            // do nothing
        }

        // can't validate without providing payload
        try {
            lc.webhooks.verifySignature(goodSignature, null, secret);
            fail();
        } catch (Exception e) {
            // do nothing
        }

        // can validate by setting configuration webhookSecret
        lc.setWebhookSecret(secret);
        assertEquals(true, lc.webhooks.verifySignature(goodSignature, payload));

        // can override configuration webhookSecret by passing secret into method
        lc.setWebhookSecret("bad");
        assertEquals(false, lc.webhooks.verifySignature(goodSignature, payload)); // this should fail because configuration webhookSecret is bad
        assertEquals(true, lc.webhooks.verifySignature(goodSignature, payload, secret)); // configuration secret is ignored if a secret is passed in.
    }
}
