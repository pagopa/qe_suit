package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.Step;
import it.pagopa.infrastructure.cucumber.hook.channel.ChannelStepInitializer;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChannelStepHook {

    private static final Logger LOG =
            LoggerFactory.getLogger(ChannelStepHook.class);

    private final ChannelStepInitializer<Channel> initializer;

    public ChannelStepHook(
            ChannelStepInitializer<Channel> initializer
    ) {
        this.initializer = initializer;
    }

    @BeforeStep(order = Integer.MIN_VALUE)
    public void beforeStep(
            Scenario scenario,
            Step step
    ) {
        String keyword = step.getKeyword().trim();
        Channel channel = initializer.initialize(keyword);

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