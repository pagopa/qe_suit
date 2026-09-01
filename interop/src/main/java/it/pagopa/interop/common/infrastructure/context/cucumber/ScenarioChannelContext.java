package it.pagopa.interop.common.infrastructure.context.cucumber;

import it.pagopa.infrastructure.cucumber.channel.ChannelConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * Scenario-scoped context that holds the immutable {@link ChannelConfig} assigned
 * to the current execution.
 * <p>
 * This is set once by the {@code @Before} hook before any step runs, and never
 * mutated during step execution. It answers the question:
 * <em>"What is the complete channel configuration for this scenario?"</em>
 * <p>
 * Compare with {@link CucumberCurrentChannel}, which answers:
 * <em>"Which channel is active right now for the current step?"</em>
 */
@Getter
@Setter
public class ScenarioChannelContext {

    /**
     * The resolved channel configuration for this scenario execution.
     * May be {@code null} when the scenario has no {@code @channel:} tags (legacy/default behaviour).
     */
    private ChannelConfig config;
}
