package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.infrastructure.cucumber.channel.ChannelConfig;
import it.pagopa.infrastructure.cucumber.channel.ChannelRuntime;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChannelScenarioHook {

    private static final Logger LOG =
            LoggerFactory.getLogger(ChannelScenarioHook.class);

    private final ChannelRuntime<Channel> channelRuntime;

    public ChannelScenarioHook(
            ChannelRuntime<Channel> channelRuntime
    ) {
        this.channelRuntime = channelRuntime;
    }

    @Before(order = Integer.MIN_VALUE)
    public void beforeScenario(Scenario scenario) {
        ChannelConfig<Channel> config =
                channelRuntime.initializeScenario(
                        scenario.getSourceTagNames()
                );

        LOG.debug(
                "Scenario '{}': channel config {}",
                scenario.getName(),
                config
        );
    }
}