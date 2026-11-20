package it.pagopa.interop.common.kernel.dto;

import it.pagopa.utils.RandomUtils;

/** Pure mapping conversion: the DTO preserves the intent until command mapping. */
public final class ConfigurableTextValueResolver {
    private ConfigurableTextValueResolver() {
    }

    public static String resolve(ConfigurableText text) {
        if (text == null) {
            return null;
        }
        if (text.maxLength() != null) {
            int minimumLength = text.value().length()
                    + (text.mode() == TextGenerationMode.RANDOM ? 2 : 0);
            if (text.maxLength() < minimumLength) {
                throw new IllegalArgumentException("Text length limit " + text.maxLength()
                        + " is too small: requires at least " + minimumLength + " characters");
            }
        }
        return switch (text.mode()) {
            case EXACT -> text.value();
            case RANDOM -> text.maxLength() == null
                    ? RandomUtils.randomAlphanumericName(text.value())
                    : RandomUtils.randomAlphanumericName(text.value(),
                            text.maxLength() - text.value().length() - 1);
        };
    }
}
