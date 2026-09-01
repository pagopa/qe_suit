package it.pagopa.interop.common.infrastructure.cucumber;

import it.pagopa.infrastructure.cucumber.hook.channel.ChannelConfig;
import it.pagopa.infrastructure.cucumber.hook.channel.ChannelFeatureGenerator;
import it.pagopa.infrastructure.cucumber.hook.channel.ChannelGherkinMapping;
import it.pagopa.infrastructure.cucumber.hook.channel.ChannelScenarioExpander;
import it.pagopa.interop.common.kernel.domain.Channel;

import java.nio.file.Path;
import java.util.Map;

public final class InteropChannelFeatureGenerator {

    private InteropChannelFeatureGenerator() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: InteropChannelFeatureGenerator <sourceRoot> <targetRoot>"
            );
        }

        ChannelGherkinMapping<Channel> mapping =
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
                );

        ChannelConfig<Channel> defaultConfig =
                new ChannelConfig<>(
                        Channel.BFF,
                        Channel.BFF,
                        Channel.BFF
                );

        ChannelScenarioExpander<Channel> expander =
                new ChannelScenarioExpander<>(
                        mapping,
                        defaultConfig
                );

        new ChannelFeatureGenerator(
                Path.of(args[0]),
                Path.of(args[1]),
                expander::expand
        ).generate();
    }
}