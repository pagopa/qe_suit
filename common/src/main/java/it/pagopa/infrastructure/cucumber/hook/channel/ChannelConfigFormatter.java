package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

import java.util.Objects;

final class ChannelConfigFormatter<C extends Enum<C> & ChannelKind> {

    private final ChannelGherkinMapping<C> channelMapping;

    ChannelConfigFormatter(ChannelGherkinMapping<C> channelMapping) {
        this.channelMapping = Objects.requireNonNull(channelMapping, "channelMapping must not be null");
    }

    String toTag(ChannelConfig<C> config) { return "@channel:" + fields(config); }

    String toSuffix(ChannelConfig<C> config) { return "[" + fields(config) + "]"; }

    private String fields(ChannelConfig<C> config) {
        return "Given=" + channelMapping.toGherkin(config.given())
                + ",When=" + channelMapping.toGherkin(config.when())
                + ",Then=" + channelMapping.toGherkin(config.then());
    }
}
