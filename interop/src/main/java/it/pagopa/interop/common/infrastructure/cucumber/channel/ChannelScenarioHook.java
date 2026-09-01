package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.interop.common.infrastructure.context.cucumber.ScenarioChannelContext;
import it.pagopa.kernel.context.CurrentChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Cucumber {@code @Before} hook responsible solely for populating
 * {@link ScenarioChannelContext} from the scenario's {@code @channel:…} tags
 * and initializing {@link CurrentChannel} to the Given-phase channel.
 * <p>
 * Responsibility chain:
 * <pre>
 *   scenario tags
 *       → ChannelTagParser   (parse tags into configs)
 *       → ChannelConfigResolver (pick one config)
 *       → ScenarioChannelContext (store it)
 *       → CurrentChannel (initialize to config.given())
 * </pre>
 *
 * Per-step channel switching is handled by {@link ChannelStepHook}.
 */
@RequiredArgsConstructor
@Slf4j
public class ChannelScenarioHook {

    private final ScenarioChannelContext scenarioChannelContext;
    private final CurrentChannel currentChannel;

    @Before(order = Integer.MIN_VALUE)
    public void beforeScenario(Scenario scenario) {

        List<ChannelConfig> configs =
                ChannelTagParser.parse(scenario.getSourceTagNames());

        ChannelConfig config =
                ChannelConfigResolver.resolve(configs)
                        .orElse(ChannelConfig.DEFAULT);

        scenarioChannelContext.setConfig(config);
        currentChannel.setCurrentChannel(config.given());

        log.debug(
                "Scenario '{}': channel config {}",
                scenario.getName(),
                config
        );
    }
}