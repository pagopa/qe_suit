package it.pagopa.infrastructure.cucumber.channel;

import it.pagopa.application.ChannelKind;

import java.util.LinkedHashSet;
import java.util.List;

public final class EffectiveChannelConfigResolver {

    private EffectiveChannelConfigResolver() {}

    public static <C extends Enum<C> & ChannelKind> List<ChannelConfig<C>> resolve(
            List<ChannelConfig<C>> featureConfigs,
            List<ChannelConfig<C>> scenarioConfigs
    ) {
        // Scenario tags override feature tags. Preserve declared order, removing only
        // configurations identical across all three (channel, language) selections.
        return List.copyOf(new LinkedHashSet<>(
                scenarioConfigs.isEmpty() ? featureConfigs : scenarioConfigs));
    }
}
