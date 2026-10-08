package it.pagopa.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomUtils {
    private RandomUtils() {
    }

    public static String randomAlphanumeric(int length) {
        return RandomStringUtils.insecure().nextAlphanumeric(length);
    }

    public static String randomAlphanumeric() {
        return randomAlphanumeric(15);
    }

    public static String randomAlphanumericName(String prefix, int length) {
        return prefix + "-" + randomAlphanumeric(length);
    }

    public static String randomAlphanumericName(String prefix) {
        return randomAlphanumericName(prefix, 15);
    }
}
