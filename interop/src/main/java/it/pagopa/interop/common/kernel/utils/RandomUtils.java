package it.pagopa.interop.common.kernel.utils;

import org.instancio.Instancio;

import static org.instancio.Select.allStrings;

public class RandomUtils {
    private RandomUtils() {
    }

    public static String randomAlphanumericName(String prefix,  int length) {
        String randomPart = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.string().prefix(prefix+"-").length(length)).create();
        return prefix + randomPart;
    }

    public static String randomAlphanumericName(String prefix) {
       return randomAlphanumericName(prefix, 15);
    }
}
