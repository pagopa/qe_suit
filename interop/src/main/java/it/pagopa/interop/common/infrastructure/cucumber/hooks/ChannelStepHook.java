package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.Step;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngine;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChannelStepHook {

    private static final Logger LOG =
            LoggerFactory.getLogger(ChannelStepHook.class);

    private final GherkinChannelEngine<Channel> gherkinChannelEngine;

    public ChannelStepHook(
            GherkinChannelEngine<Channel> gherkinChannelEngine
    ) {
        this.gherkinChannelEngine = gherkinChannelEngine;
    }

    @BeforeStep(order = Integer.MIN_VALUE)
    public void beforeStep(
            Scenario scenario,
            Step step
    ) {
        String keyword = step.getKeyword().trim();
        Channel channel = gherkinChannelEngine.initializeStep(keyword);

        if (channel == null) {
            return;
        }

        LOG.debug(
                "Step '{}' keyword='{}' -> channel={}",
                step.getText(),
                keyword,
                channel
        );
    }
}