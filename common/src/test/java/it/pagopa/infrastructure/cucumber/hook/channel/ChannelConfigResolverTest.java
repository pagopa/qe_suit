package it.pagopa.infrastructure.cucumber.hook.channel;

import it.pagopa.application.ChannelKind;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelConfigResolverTest {

    private static final ChannelConfig<TestChannel> CONFIG_A =
            new ChannelConfig<>(
                    TestChannel.BFF,
                    TestChannel.WEB_BROWSER,
                    TestChannel.WEB_BROWSER
            );

    private static final ChannelConfig<TestChannel> CONFIG_B =
            new ChannelConfig<>(
                    TestChannel.BFF,
                    TestChannel.BFF,
                    TestChannel.BFF
            );

    @Test
    void zeroConfigs_returnsEmpty() {
        Optional<ChannelConfig<TestChannel>> result =
                ChannelConfigResolver.resolve(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void oneConfig_returnsThatConfig() {
        Optional<ChannelConfig<TestChannel>> result =
                ChannelConfigResolver.resolve(List.of(CONFIG_A));

        assertThat(result).contains(CONFIG_A);
    }

    @Test
    void multipleConfigs_returnsFirst() {
        Optional<ChannelConfig<TestChannel>> result =
                ChannelConfigResolver.resolve(List.of(CONFIG_A, CONFIG_B));

        assertThat(result).contains(CONFIG_A);
    }

    private enum TestChannel implements ChannelKind {
        BFF,
        WEB_BROWSER
    }
}
