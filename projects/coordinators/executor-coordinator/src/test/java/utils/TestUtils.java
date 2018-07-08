package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {
    protected static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    public static String getUrl(int port) {
        return "http://localhost:"+port;
    }
}
