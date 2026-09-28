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
}
