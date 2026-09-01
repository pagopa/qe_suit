package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.infrastructure.cucumber.channel.ChannelConfig;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngine;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChannelScenarioHook {

    private static final Logger LOG =
            LoggerFactory.getLogger(ChannelScenarioHook.class);

    private final GherkinChannelEngine<Channel> gherkinChannelEngine;

    public ChannelScenarioHook(
            GherkinChannelEngine<Channel> gherkinChannelEngine
    ) {
        this.gherkinChannelEngine = gherkinChannelEngine;
    }

    @Before(order = Integer.MIN_VALUE)
    public void beforeScenario(Scenario scenario) {
        ChannelConfig<Channel> config =
                gherkinChannelEngine.initializeScenario(
                        scenario.getSourceTagNames()
                );

        LOG.debug(
                "Scenario '{}': channel config {}",
                scenario.getName(),
                config
        );
    }
}