package it.pagopa.interop.common.infrastructure.cucumber.channel;

/**
 * Formats a {@link ChannelConfig} into its Gherkin textual representations: the
 * {@code @channel:...} tag, and the {@code [Given=...,When=...,Then=...]} scenario name suffix.
 * <p>
 * Centralised here so tag and suffix formatting stay in sync and reuse
 * {@link ChannelGherkinMapping} for the actual value → Gherkin-name conversion.
 */
final class ChannelConfigFormatter {

    private ChannelConfigFormatter() {}

    static String toTag(ChannelConfig config) {
        return "@channel:" + fields(config);
    }

    static String toSuffix(ChannelConfig config) {
        return "[" + fields(config) + "]";
    }

    private static String fields(ChannelConfig config) {
        return "Given=" + ChannelGherkinMapping.toGherkin(config.given())
                + ",When=" + ChannelGherkinMapping.toGherkin(config.when())
                + ",Then=" + ChannelGherkinMapping.toGherkin(config.then());
    }
}
