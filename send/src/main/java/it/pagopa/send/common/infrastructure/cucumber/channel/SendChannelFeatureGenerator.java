package it.pagopa.send.common.infrastructure.cucumber.channel;

import it.pagopa.infrastructure.cucumber.channel.ChannelFeatureGenerator;
import it.pagopa.infrastructure.cucumber.channel.ChannelScenarioExpander;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngineConfig;
import it.pagopa.send.common.kernel.domain.Channel;

import java.nio.file.Path;

public final class SendChannelFeatureGenerator {

    private SendChannelFeatureGenerator() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: SendChannelFeatureGenerator <sourceRoot> <targetRoot>"
            );
        }

        GherkinChannelEngineConfig<Channel> config = SendChannelEngineConfigs.sendChannelModule();

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
