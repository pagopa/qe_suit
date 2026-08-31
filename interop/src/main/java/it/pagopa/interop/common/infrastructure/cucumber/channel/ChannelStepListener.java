package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import it.pagopa.interop.common.infrastructure.context.cucumber.ScenarioChannelContext;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Cucumber {@link ConcurrentEventListener} that updates {@link CurrentChannel} before
 * every real Gherkin step based on the semantic phase (Given / When / Then).
 * <p>
 * Rules:
 * <ul>
 *   <li>Only {@link PickleStepTestStep} events are considered; hooks are ignored.</li>
 *   <li>The active keyword type is tracked per scenario (by test-case ID) to resolve
 *       {@code And} / {@code But} to the last explicit {@code Given}/{@code When}/{@code Then}.</li>
 *   <li>If the scenario has no {@link ChannelConfig} (legacy), the channel is not changed.</li>
 * </ul>
 * <p>
 * This class is injected with providers ({@link Supplier}) for the two scenario-scoped
 * beans so that Cucumber can build the listener once while the beans are re-created per scenario.
 */
@Slf4j
public class ChannelStepListener implements ConcurrentEventListener {

    private final Supplier<ScenarioChannelContext> scenarioChannelContextProvider;
    private final Supplier<CurrentChannel> currentChannelProvider;

    /** Tracks the last explicit Given/When/Then keyword per test-case UUID. */
    private final ConcurrentHashMap<UUID, SemanticStepType> lastSemanticType =
            new ConcurrentHashMap<>();

    public ChannelStepListener(
            Supplier<ScenarioChannelContext> scenarioChannelContextProvider,
            Supplier<CurrentChannel> currentChannelProvider) {
        this.scenarioChannelContextProvider = scenarioChannelContextProvider;
        this.currentChannelProvider = currentChannelProvider;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepStarted.class, this::onTestStepStarted);
        publisher.registerHandlerFor(TestCaseFinished.class, this::onTestCaseFinished);
    }

    // -------------------------------------------------------------------------
    // Handlers
    // -------------------------------------------------------------------------

    private void onTestStepStarted(TestStepStarted event) {
        if (!(event.getTestStep() instanceof PickleStepTestStep stepTestStep)) {
            return; // hooks – ignore
        }

        UUID testCaseId = event.getTestCase().getId();
        String keyword = stepTestStep.getStep().getKeyword().trim();

        SemanticStepType semantic = resolveSemanticType(keyword, testCaseId);
        lastSemanticType.put(testCaseId, semantic);

        ChannelConfig config = scenarioChannelContextProvider.get().getConfig();
        if (config == null) {
            return; // legacy scenario without @channel: – do not touch channel
        }

        Channel channel = switch (semantic) {
            case GIVEN -> config.given();
            case WHEN -> config.when();
            case THEN -> config.then();
        };

        currentChannelProvider.get().setCurrentChannel(channel);
        log.debug("Step '{}' (keyword='{}', semantic={}) → channel={}",
                stepTestStep.getStep().getText(), keyword, semantic, channel);
    }

    private void onTestCaseFinished(TestCaseFinished event) {
        lastSemanticType.remove(event.getTestCase().getId());
    }

    // -------------------------------------------------------------------------
    // Keyword → semantic type resolution
    // -------------------------------------------------------------------------

    /**
     * Maps the Gherkin keyword to a semantic step type.
     * {@code And} and {@code But} inherit the last explicit type for the scenario.
     */
    private SemanticStepType resolveSemanticType(String keyword, UUID testCaseId) {
        return switch (keyword.toLowerCase()) {
            case "given", "dado", "dada", "dados", "dadas" -> SemanticStepType.GIVEN;
            case "when", "quando" -> SemanticStepType.WHEN;
            case "then", "então", "entao", "allora" -> SemanticStepType.THEN;
            // And, But, * – inherit last semantic type (default GIVEN if none yet)
            default -> lastSemanticType.getOrDefault(testCaseId, SemanticStepType.GIVEN);
        };
    }

    // -------------------------------------------------------------------------
    // Inner types
    // -------------------------------------------------------------------------

    enum SemanticStepType {
        GIVEN, WHEN, THEN
    }
}
