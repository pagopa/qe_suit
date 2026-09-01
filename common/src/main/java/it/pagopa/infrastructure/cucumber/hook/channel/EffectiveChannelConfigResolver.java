package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;

import java.util.List;

public final class EffectiveChannelConfigResolver {

    private EffectiveChannelConfigResolver() {}

    public static <C extends Enum<C> & ChannelKind> List<ChannelConfig<C>> resolve(
            List<ChannelConfig<C>> featureConfigs,
            List<ChannelConfig<C>> scenarioConfigs
    ) {
        return scenarioConfigs.isEmpty() ? featureConfigs : scenarioConfigs;
    }
}
