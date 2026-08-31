package it.pagopa.interop.common.infrastructure.cucumber.channel;

import it.pagopa.interop.common.kernel.domain.Channel;

import java.util.Map;
import java.util.Optional;

/**
 * Maps Gherkin tag values (e.g. "WEB", "BFF") to {@link Channel} enum constants.
 * <p>
 * Gherkin uses short names; Java uses canonical names. This class is the single
 * place that owns that mapping so that no other class performs ad-hoc string conversions.
 */
public final class ChannelGherkinMapping {

    private static final Map<String, Channel> MAPPING = Map.of(
            "BFF", Channel.BFF,
            "WEB", Channel.WEB_BROWSER,
            "WEB_BROWSER", Channel.WEB_BROWSER
    );

    private ChannelGherkinMapping() {}

    /**
     * Returns the {@link Channel} for the given Gherkin tag value, or empty if unknown.
     */
    public static Optional<Channel> fromGherkin(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(MAPPING.get(value.trim().toUpperCase()));
    }

    /**
     * Returns the Gherkin tag value (short name) for the given channel.
     * Used when writing expanded feature files.
     */
    public static String toGherkin(Channel channel) {
        return switch (channel) {
            case BFF -> "BFF";
            case WEB_BROWSER -> "WEB";
        };
    }
}
