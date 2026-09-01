package it.pagopa.interop.common.infrastructure.cucumber.channel;

import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ChannelConfigResolverTest {

    private static final ChannelConfig CONFIG_A =
            new ChannelConfig(Channel.BFF, Channel.WEB_BROWSER, Channel.WEB_BROWSER);
    private static final ChannelConfig CONFIG_B =
            new ChannelConfig(Channel.BFF, Channel.BFF, Channel.BFF);

    @Test
    void zeroConfigs_returnsEmpty() {
        assertThat(ChannelConfigResolver.resolve(List.of())).isEmpty();
    }

    @Test
    void oneConfig_returnsThatConfig() {
        Optional<ChannelConfig> result = ChannelConfigResolver.resolve(List.of(CONFIG_A));
        assertThat(result).contains(CONFIG_A);
    }

    @Test
    void multipleConfigs_returnsFirst() {
        Optional<ChannelConfig> result = ChannelConfigResolver.resolve(List.of(CONFIG_A, CONFIG_B));
        assertThat(result).contains(CONFIG_A);
    }
}
