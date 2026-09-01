package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EffectiveChannelConfigResolverTest {

    private static final ChannelConfig<TestChannel> A =
            new ChannelConfig<>(
                    TestChannel.BFF,
                    TestChannel.WEB_BROWSER,
                    TestChannel.WEB_BROWSER
            );

    private static final ChannelConfig<TestChannel> B =
            new ChannelConfig<>(
                    TestChannel.BFF,
                    TestChannel.BFF,
                    TestChannel.BFF
            );

    private static final ChannelConfig<TestChannel> C =
            new ChannelConfig<>(
                    TestChannel.BFF,
                    TestChannel.WEB_BROWSER,
                    TestChannel.BFF
            );

    @Test
    void usesFeatureConfigsWhenScenarioHasNone() {
        assertThat(
                EffectiveChannelConfigResolver.resolve(
                        List.of(A, B),
                        List.of()
                )
        ).containsExactly(A, B);
    }

    @Test
    void scenarioConfigsOverrideFeatureConfigs() {
        assertThat(
                EffectiveChannelConfigResolver.resolve(
                        List.of(A, B),
                        List.of(C)
                )
        ).containsExactly(C);
    }

    private enum TestChannel implements ChannelKind {
        BFF,
        WEB_BROWSER
    }
}
