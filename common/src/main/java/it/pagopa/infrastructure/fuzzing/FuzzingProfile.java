package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Single source of truth for the fuzzing configuration of one API family.
 * <p>
 * The same profile must feed both the runtime contract tests (e.g. through Spring beans)
 * and the PST generator, so that both pipelines share the same {@link ObjectMapper}
 * and the same rule sets per request scope.
 * <p>
 * Implementations used by the PST Maven goal must expose a public no-arg constructor
 * and must not require external services.
 */
public interface FuzzingProfile {

    ObjectMapper objectMapper();

    FuzzCasePlanner payloadPlanner();

    FuzzCasePlanner pathParamsPlanner();

    /**
     * Rule set for query parameters. Defaults to the path parameter planner so that existing
     * profiles keep compiling; override it when query parameters need a different rule set
     * (for example removal/null scenarios, which are meaningful only for optional parameters).
     */
    default FuzzCasePlanner queryParamsPlanner() {
        return pathParamsPlanner();
    }
}
