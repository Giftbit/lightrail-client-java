package com.lightrail;

import com.lightrail.network.DefaultNetworkProvider;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.UUID;

public class TestUtils {

    private static Dotenv dotenv;

    public static LightrailClient getLightrailClient() {
        Dotenv dotenv = getDotenv();
        LightrailClient c = new LightrailClient(dotenv.get("LIGHTRAIL_API_KEY"));
        ((DefaultNetworkProvider) c.getNetworkProvider()).setRestRoot(dotenv.get("LIGHTRAIL_API_PATH"));
        return c;
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    private static Dotenv getDotenv() {
        if (dotenv == null) {
            dotenv = Dotenv.configure()
                    .directory("src/test/resources")
                    .load();
        }
        return dotenv;
    }
}
