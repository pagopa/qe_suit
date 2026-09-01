package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.interop.common.infrastructure.context.cucumber.ScenarioChannelContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Cucumber {@code @Before} hook responsible solely for populating
 * {@link ScenarioChannelContext} from the scenario's {@code @channel:…} tags.
 * <p>
 * Responsibility chain:
 * <pre>
 *   scenario tags
 *       → ChannelTagParser   (parse tags into configs)
 *       → ChannelConfigResolver (pick one config)
 *       → ScenarioChannelContext (store it)
 * </pre>
 *
 * This hook does NOT change the active channel.  That is the responsibility
 * of {@link ChannelStepListener}, which fires before each Gherkin step.
 */
@RequiredArgsConstructor
@Slf4j
public class ChannelScenarioHook {

    private final ScenarioChannelContext scenarioChannelContext;

    @Before
    public void beforeScenario(Scenario scenario) {
        List<ChannelConfig> configs = ChannelTagParser.parse(scenario.getSourceTagNames());

        ChannelConfigResolver.resolve(configs)
                .ifPresentOrElse(
                        config -> {
                            scenarioChannelContext.setConfig(config);
                            log.debug("Scenario '{}': resolved channel config {}",
                                    scenario.getName(), config);
                        },
                        () -> {
                            scenarioChannelContext.setConfig(ChannelConfig.DEFAULT);
                            log.debug("Scenario '{}': default channel config {}",
                                    scenario.getName(), ChannelConfig.DEFAULT);
                        }
                );
    }
}
