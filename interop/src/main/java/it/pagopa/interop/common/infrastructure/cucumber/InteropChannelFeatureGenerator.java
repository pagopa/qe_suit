package it.pagopa.interop.common.infrastructure.cucumber;

import it.pagopa.infrastructure.cucumber.channel.ChannelFeatureGenerator;
import it.pagopa.infrastructure.cucumber.channel.ChannelScenarioExpander;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngineConfig;
import it.pagopa.interop.common.infrastructure.cucumber.channel.InteropChannelEngineConfigs;
import it.pagopa.interop.common.kernel.domain.Channel;

import java.nio.file.Path;

public final class InteropChannelFeatureGenerator {

    private InteropChannelFeatureGenerator() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: InteropChannelFeatureGenerator <sourceRoot> <targetRoot>"
            );
        }

        GherkinChannelEngineConfig<Channel> config = InteropChannelEngineConfigs.interopChannelModule();

        ChannelScenarioExpander<Channel> expander =
                new ChannelScenarioExpander<>(
                        config.mapping(),
                        config.defaultConfig()
                );

        new ChannelFeatureGenerator(
                Path.of(args[0]),
                Path.of(args[1]),
                expander::expand
        ).generate();
    }
}