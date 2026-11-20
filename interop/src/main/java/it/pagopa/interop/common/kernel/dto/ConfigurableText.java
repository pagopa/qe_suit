package it.pagopa.interop.common.kernel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** A Cucumber text value and its requested mapping policy. */
public record ConfigurableText(
        @NotBlank String value,
        @NotNull TextGenerationMode mode
) {
    public static ConfigurableText random(String value) {
        return new ConfigurableText(value, TextGenerationMode.RANDOM);
    }

    public static ConfigurableText literal(String value) {
        return new ConfigurableText(value, TextGenerationMode.EXACT);
    }
}
