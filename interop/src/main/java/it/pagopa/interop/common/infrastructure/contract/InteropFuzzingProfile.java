package it.pagopa.interop.common.infrastructure.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzCasePlanner;
import it.pagopa.infrastructure.fuzzing.FuzzingProfile;
import it.pagopa.infrastructure.fuzzing.NullAndMissingRule;
import it.pagopa.infrastructure.fuzzing.ScalarRule;

import java.util.List;

/**
 * Interop fuzzing configuration shared by the Spring runtime ({@code FuzzingConfig}, {@code JacksonConfig})
 * and the PST generator ({@code -Dpst.fuzzingProfile=...}). Adding or removing a rule here changes both.
 * Must stay instantiable without Spring and without external services.
 */
public final class InteropFuzzingProfile implements FuzzingProfile {

    private final ObjectMapper objectMapper;
    private final NullAndMissingRule nullAndMissingRule;
    private final ScalarRule scalarRule;
    private final FuzzCasePlanner payloadPlanner;
    private final FuzzCasePlanner pathParamsPlanner;

    public InteropFuzzingProfile() {
        this.objectMapper = new ObjectMapper();
        this.nullAndMissingRule = new NullAndMissingRule();
        this.scalarRule = new ScalarRule();
        this.payloadPlanner = new FuzzCasePlanner(List.of(nullAndMissingRule, scalarRule));
        this.pathParamsPlanner = new FuzzCasePlanner(List.of(scalarRule));
    }

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Override
    public FuzzCasePlanner payloadPlanner() {
        return payloadPlanner;
    }

    @Override
    public FuzzCasePlanner pathParamsPlanner() {
        return pathParamsPlanner;
    }

    public NullAndMissingRule nullAndMissingRule() {
        return nullAndMissingRule;
    }

    public ScalarRule scalarRule() {
        return scalarRule;
    }
}
