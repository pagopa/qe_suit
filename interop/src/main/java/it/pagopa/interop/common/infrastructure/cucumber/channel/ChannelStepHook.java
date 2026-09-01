package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.cucumber.java.Step;
import it.pagopa.interop.common.infrastructure.context.cucumber.ScenarioChannelContext;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

/**
 * Cucumber {@code @BeforeStep} hook that updates {@link CurrentChannel} before
 * every real Gherkin step based on the semantic phase (Given / When / Then).
 * <p>
 * {@code And}, {@code But}, and equivalent keywords inherit the semantic phase
 * of the previous explicit {@code Given}, {@code When}, or {@code Then}.
 * <p>
 * This class is a normal Cucumber/Spring glue class with scenario-scoped
 * dependencies injected through the constructor. No static state, no plugin
 * event bus, no cross-lifecycle bridging.
 */
@RequiredArgsConstructor
@Slf4j
public class ChannelStepHook {

    private final ScenarioChannelContext scenarioChannelContext;
    private final CurrentChannel currentChannel;

    /** Scenario-local state – reset by {@link ChannelScenarioHook#beforeScenario}. */
    private SemanticStepType lastSemanticType = SemanticStepType.GIVEN;

    @BeforeStep(order = Integer.MIN_VALUE)
    public void beforeStep(Scenario scenario, Step step) {

        ChannelConfig config = scenarioChannelContext.getConfig();

        if (config == null) {
            return;
        }

        String keyword = step.getKeyword().trim();

        SemanticStepType semantic = resolveSemanticType(keyword);
        lastSemanticType = semantic;

        Channel channel = switch (semantic) {
            case GIVEN -> config.given();
            case WHEN  -> config.when();
            case THEN  -> config.then();
        };

        currentChannel.setCurrentChannel(channel);

        log.debug(
                "Step '{}' keyword='{}' semantic={} -> channel={}",
                step.getText(),
                keyword,
                semantic,
                channel
        );
    }

    private SemanticStepType resolveSemanticType(String keyword) {
        return switch (keyword.toLowerCase(Locale.ROOT)) {

            case "given",
                 "dato",
                 "data",
                 "dati",
                 "date",
                 "dado",
                 "dada",
                 "dados",
                 "dadas" ->
                    SemanticStepType.GIVEN;

            case "when",
                 "quando" ->
                    SemanticStepType.WHEN;

            case "then",
                 "allora",
                 "então",
                 "entao" ->
                    SemanticStepType.THEN;

            // And / But / E / Ma / * – inherit previous semantic phase
            default -> lastSemanticType;
        };
    }

    private enum SemanticStepType {
        GIVEN, WHEN, THEN
    }
}
