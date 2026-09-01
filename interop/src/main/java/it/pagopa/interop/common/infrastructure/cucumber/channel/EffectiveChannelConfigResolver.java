package it.pagopa.interop.common.infrastructure.cucumber.channel;

import java.util.List;

public final class EffectiveChannelConfigResolver {

    private EffectiveChannelConfigResolver() {}

    public static List<ChannelConfig> resolve(
            List<ChannelConfig> featureConfigs,
            List<ChannelConfig> scenarioConfigs
    ) {
        return scenarioConfigs.isEmpty() ? featureConfigs : scenarioConfigs;
    }
}
