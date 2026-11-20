package it.pagopa.interop.common.infrastructure.cucumber.dto;

import it.pagopa.utils.RandomUtils;

/** Pure mapping conversion: the DTO preserves the intent until command mapping. */
public final class ConfigurableTextValueResolver {
    private ConfigurableTextValueResolver() {
    }

    public static String resolve(ConfigurableText text) {
        if (text == null) {
            return null;
        }
        return switch (text.mode()) {
            case EXACT -> text.value();
            case RANDOM -> RandomUtils.randomAlphanumericName(text.value());
        };
    }
}
