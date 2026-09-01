package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

import java.util.Objects;

public final class ChannelModule<C extends Enum<C> & ChannelKind> {

    private final ChannelGherkinMapping<C> mapping;
    private final ChannelConfig<C> defaultConfig;
    private final ChannelTagParser<C> tagParser;

    private ChannelModule(
            ChannelGherkinMapping<C> mapping,
            ChannelConfig<C> defaultConfig
    ) {
        this.mapping = Objects.requireNonNull(mapping, "mapping must not be null");
        this.defaultConfig = Objects.requireNonNull(defaultConfig, "defaultConfig must not be null");
        this.tagParser = new ChannelTagParser<>(mapping);
    }

    public static <C extends Enum<C> & ChannelKind> ChannelModule<C> of(
            ChannelGherkinMapping<C> mapping,
            ChannelConfig<C> defaultConfig
    ) {
        return new ChannelModule<>(mapping, defaultConfig);
    }

    public ChannelRuntime<C> newRuntime(CurrentChannel<C> currentChannel) {
        return new ChannelRuntime<>(tagParser, defaultConfig, currentChannel);
    }

    public ChannelGherkinMapping<C> mapping() {
        return mapping;
    }

    public ChannelConfig<C> defaultConfig() {
        return defaultConfig;
    }
}
