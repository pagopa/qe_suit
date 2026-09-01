package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;
import it.pagopa.infrastructure.channel.CurrentChannel;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class ChannelScenarioInitializer<
        C extends Enum<C> & ChannelKind> {

    private final ChannelScenarioContext<C> scenarioChannelContext;
    private final CurrentChannel<C> currentChannel;
    private final ChannelTagParser<C> channelTagParser;
    private final ChannelConfig<C> defaultConfig;

    public ChannelScenarioInitializer(
            ChannelScenarioContext<C> scenarioChannelContext,
            CurrentChannel<C> currentChannel,
            ChannelTagParser<C> channelTagParser,
            ChannelConfig<C> defaultConfig
    ) {
        this.scenarioChannelContext = Objects.requireNonNull(scenarioChannelContext);
        this.currentChannel = Objects.requireNonNull(currentChannel);
        this.channelTagParser = Objects.requireNonNull(channelTagParser);
        this.defaultConfig = Objects.requireNonNull(defaultConfig);
    }

    public ChannelConfig<C> initialize(Collection<String> tags) {
        List<ChannelConfig<C>> configs = channelTagParser.parse(tags);

        ChannelConfig<C> config = ChannelConfigResolver
                .resolve(configs)
                .orElse(defaultConfig);

        scenarioChannelContext.setConfig(config);
        currentChannel.setCurrentChannel(config.given());

        return config;
    }
}