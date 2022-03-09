package com.grpc.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropsUtil {

    public static Properties readProperties() {
        String resourceName = "file-info.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            Properties props = new Properties();
            props.load(resourceStream);
            return props;
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Unable to load properties");
    }
}
