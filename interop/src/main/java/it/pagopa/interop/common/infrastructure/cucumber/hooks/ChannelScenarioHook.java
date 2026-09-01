package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.infrastructure.cucumber.hook.channel.ChannelConfig;
import it.pagopa.infrastructure.cucumber.hook.channel.ChannelScenarioInitializer;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChannelScenarioHook {

    private static final Logger LOG =
            LoggerFactory.getLogger(ChannelScenarioHook.class);

    private final ChannelScenarioInitializer<Channel> initializer;

    public ChannelScenarioHook(
            ChannelScenarioInitializer<Channel> initializer
    ) {
        this.initializer = initializer;
    }

    @Before(order = Integer.MIN_VALUE)
    public void beforeScenario(Scenario scenario) {
        ChannelConfig<Channel> config =
                initializer.initialize(
                        scenario.getSourceTagNames()
                );

        LOG.debug(
                "Scenario '{}': channel config {}",
                scenario.getName(),
                config
        );
    }
}