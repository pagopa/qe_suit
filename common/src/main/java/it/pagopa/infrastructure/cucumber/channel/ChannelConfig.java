package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;

import java.util.Locale;
import java.util.Objects;

/** Channel and language selection for each semantic Gherkin phase. */
public record ChannelConfig<C extends Enum<C> & ChannelKind>(
        C given, C when, C then,
        Locale givenLanguage, Locale whenLanguage, Locale thenLanguage
) {
    public ChannelConfig {
        Objects.requireNonNull(given, "given must not be null");
        Objects.requireNonNull(when, "when must not be null");
        Objects.requireNonNull(then, "then must not be null");
        givenLanguage = normalize(givenLanguage);
        whenLanguage = normalize(whenLanguage);
        thenLanguage = normalize(thenLanguage);
    }

    /** Retains the original three-channel constructor (Italian by default). */
    public ChannelConfig(C given, C when, C then) {
        this(given, when, then, Locale.ITALIAN, Locale.ITALIAN, Locale.ITALIAN);
    }

    private static Locale normalize(Locale locale) {
        Objects.requireNonNull(locale, "language must not be null");
        return Locale.forLanguageTag(locale.toLanguageTag());
    }
}
