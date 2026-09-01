package it.pagopa.interop.common.infrastructure.cucumber.channel;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import it.pagopa.interop.common.infrastructure.context.cucumber.ScenarioChannelContext;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.interop.common.kernel.domain.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
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
 * Cucumber instantiates plugins via a no-arg constructor before Spring is ready.
 * Spring then registers itself as the live instance via {@link #setLiveInstance}.
 * The Cucumber-created instance delegates all event handling to the live Spring-managed instance.
 */
@Slf4j
public class ChannelStepListener implements ConcurrentEventListener {

    /**
     * Holds the Spring-managed instance. Set by {@link #setLiveInstance} from {@code ContextConfig}.
     * The Cucumber-created plugin instance delegates to this.
     */
    private static final AtomicReference<ChannelStepListener> LIVE_INSTANCE = new AtomicReference<>();

    /** Called by Spring's {@code ContextConfig} after creating the bean. */
    public static void setLiveInstance(ChannelStepListener instance) {
        LIVE_INSTANCE.set(instance);
    }

    private final Supplier<ScenarioChannelContext> scenarioChannelContextProvider;
    private final Supplier<CurrentChannel> currentChannelProvider;

    /** Tracks the last explicit Given/When/Then keyword per test-case UUID. */
    private final ConcurrentHashMap<UUID, SemanticStepType> lastSemanticType =
            new ConcurrentHashMap<>();

    /**
     * No-arg constructor used by Cucumber when loading this class as a plugin.
     * All event calls on this instance delegate to the live Spring-managed instance.
     */
    public ChannelStepListener() {
        this.scenarioChannelContextProvider = null;
        this.currentChannelProvider = null;
    }

    public ChannelStepListener(
            Supplier<ScenarioChannelContext> scenarioChannelContextProvider,
            Supplier<CurrentChannel> currentChannelProvider) {
        this.scenarioChannelContextProvider = scenarioChannelContextProvider;
        this.currentChannelProvider = currentChannelProvider;
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        if (scenarioChannelContextProvider != null) {
            // Spring-managed instance (o test diretto): registra i propri handler
            publisher.registerHandlerFor(TestStepStarted.class, this::onTestStepStarted);
            publisher.registerHandlerFor(TestCaseFinished.class, this::onTestCaseFinished);
        } else {
            // Istanza no-arg creata da Cucumber come plugin: delega alla live instance
            // I lambda leggono LIVE_INSTANCE al momento dell'evento (non ora),
            // così Spring ha il tempo di inizializzarsi prima che gli step girino.
            publisher.registerHandlerFor(TestStepStarted.class,
                    event -> {
                        ChannelStepListener live = LIVE_INSTANCE.get();
                        if (live != null) live.onTestStepStarted(event);
                    });
            publisher.registerHandlerFor(TestCaseFinished.class,
                    event -> {
                        ChannelStepListener live = LIVE_INSTANCE.get();
                        if (live != null) live.onTestCaseFinished(event);
                    });
        }
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