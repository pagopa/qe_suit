package it.pagopa.interop.common.infrastructure.cucumber;

import it.pagopa.interop.common.infrastructure.cucumber.dto.TextGenerationMode;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Centralized Gherkin header vocabulary, independent of DTO field names.
 */
public final class ConfigurableTextHeaderSyntax {
    private static final Pattern MODIFIER =
            Pattern.compile("^(.+?)\\s*\\(([^()]*)\\)$");

    // Add or rename terms here; the unmodified header always means RANDOM.
    private static final Map<String, TextGenerationMode> MODES = Map.of(
            "letterale", TextGenerationMode.EXACT,
            "casuale", TextGenerationMode.RANDOM
    );

    public Header parse(String header) {
        Objects.requireNonNull(header, "header");
        Matcher matcher = MODIFIER.matcher(header);
        if (!matcher.matches()) {
            return new Header(header, null, false);
        }
        String label = matcher.group(1).strip();
        String modifier = matcher.group(2).strip().toLowerCase(java.util.Locale.ROOT);
        return new Header(label, MODES.get(modifier), true);
    }

    public record Header(String label, TextGenerationMode mode, boolean modified) {
    }
}
