package it.pagopa.interop.common.infrastructure.cucumber.channel;

import it.pagopa.infrastructure.cucumber.channel.ChannelConfig;
import it.pagopa.infrastructure.cucumber.channel.ChannelGherkinMapping;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngineConfig;
import it.pagopa.interop.common.kernel.domain.Channel;

import java.util.Map;

public final class InteropChannelEngineConfigs {
    private InteropChannelEngineConfigs() {}

    public static GherkinChannelEngineConfig<Channel> interopChannelModule() {
        return GherkinChannelEngineConfig.of(
                new ChannelGherkinMapping<>(
                        Map.of(
                                "BFF", Channel.BFF,
                                "WEB", Channel.WEB_BROWSER,
                                "WEB_BROWSER", Channel.WEB_BROWSER
                        ),
                        Map.of(
                                Channel.BFF, "BFF",
                                Channel.WEB_BROWSER, "WEB"
                        )
                ),
                new ChannelConfig<>(
                        Channel.BFF,
                        Channel.BFF,
                        Channel.BFF
                )
        );
    }
}
