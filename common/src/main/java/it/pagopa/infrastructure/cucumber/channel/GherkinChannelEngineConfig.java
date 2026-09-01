package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

import java.util.Objects;

public final class GherkinChannelEngineConfig<C extends Enum<C> & ChannelKind> {

    private final ChannelGherkinMapping<C> mapping;
    private final ChannelConfig<C> defaultConfig;
    private final ChannelTagParser<C> tagParser;

    private GherkinChannelEngineConfig(
            ChannelGherkinMapping<C> mapping,
            ChannelConfig<C> defaultConfig
    ) {
        this.mapping = Objects.requireNonNull(mapping, "mapping must not be null");
        this.defaultConfig = Objects.requireNonNull(defaultConfig, "defaultConfig must not be null");
        this.tagParser = new ChannelTagParser<>(mapping);
    }

    public static <C extends Enum<C> & ChannelKind> GherkinChannelEngineConfig<C> of(
            ChannelGherkinMapping<C> mapping,
            ChannelConfig<C> defaultConfig
    ) {
        return new GherkinChannelEngineConfig<>(mapping, defaultConfig);
    }

    public GherkinChannelEngine<C> newRuntime(CurrentChannel<C> currentChannel) {
        return new GherkinChannelEngine<>(tagParser, defaultConfig, currentChannel);
    }

    public ChannelGherkinMapping<C> mapping() {
        return mapping;
    }

    public ChannelConfig<C> defaultConfig() {
        return defaultConfig;
    }
}
