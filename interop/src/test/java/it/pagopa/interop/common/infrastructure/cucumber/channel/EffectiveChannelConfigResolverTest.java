package it.pagopa.interop.common.infrastructure.cucumber.channel;

import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EffectiveChannelConfigResolverTest {

    private static final ChannelConfig A =
            new ChannelConfig(Channel.BFF, Channel.WEB_BROWSER, Channel.WEB_BROWSER);
    private static final ChannelConfig B =
            new ChannelConfig(Channel.BFF, Channel.BFF, Channel.BFF);
    private static final ChannelConfig C =
            new ChannelConfig(Channel.BFF, Channel.WEB_BROWSER, Channel.BFF);

    @Test
    void usesFeatureConfigsWhenScenarioHasNone() {
        assertThat(EffectiveChannelConfigResolver.resolve(List.of(A, B), List.of())).containsExactly(A, B);
    }

    @Test
    void scenarioConfigsOverrideFeatureConfigs() {
        assertThat(EffectiveChannelConfigResolver.resolve(List.of(A, B), List.of(C))).containsExactly(C);
    }
}
