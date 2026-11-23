package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;

import java.util.Locale;
import java.util.Objects;

final class ChannelConfigFormatter<C extends Enum<C> & ChannelKind> {

    private final ChannelGherkinMapping<C> channelMapping;

    ChannelConfigFormatter(ChannelGherkinMapping<C> channelMapping) {
        this.channelMapping = Objects.requireNonNull(channelMapping, "channelMapping must not be null");
    }

    String toTag(ChannelConfig<C> config) { return "@channel:" + fields(config); }

    String toSuffix(ChannelConfig<C> config) { return "[" + fields(config) + "]"; }

    private String fields(ChannelConfig<C> config) {
        boolean showLanguages = !Locale.ITALIAN.equals(config.givenLanguage())
                || !Locale.ITALIAN.equals(config.whenLanguage())
                || !Locale.ITALIAN.equals(config.thenLanguage());
        return "Given=" + field(config.given(), config.givenLanguage(), showLanguages)
                + ",When=" + field(config.when(), config.whenLanguage(), showLanguages)
                + ",Then=" + field(config.then(), config.thenLanguage(), showLanguages);
    }

    private String field(C channel, Locale language, boolean showLanguage) {
        return channelMapping.toGherkin(channel)
                + (showLanguage ? ":" + language.toLanguageTag() : "");
    }
}
