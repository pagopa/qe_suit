package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.Step;
import it.pagopa.infrastructure.cucumber.hook.channel.ChannelRuntime;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChannelStepHook {

    private static final Logger LOG =
            LoggerFactory.getLogger(ChannelStepHook.class);

    private final ChannelRuntime<Channel> channelRuntime;

    public ChannelStepHook(
            ChannelRuntime<Channel> channelRuntime
    ) {
        this.channelRuntime = channelRuntime;
    }

    @BeforeStep(order = Integer.MIN_VALUE)
    public void beforeStep(
            Scenario scenario,
            Step step
    ) {
        String keyword = step.getKeyword().trim();
        Channel channel = channelRuntime.initializeStep(keyword);

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