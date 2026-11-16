package it.pagopa.send.common.infrastructure.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.*;

import java.util.List;

public final class SendFuzzingProfile implements FuzzingProfile {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NullAndMissingRule nullAndMissingRule = new NullAndMissingRule();
    private final ScalarRule scalarRule = new ScalarRule();

    private final FuzzCasePlanner payloadPlanner = new FuzzCasePlanner(List.of(nullAndMissingRule, scalarRule));
    private final FuzzCasePlanner pathParamsPlanner = new FuzzCasePlanner(List.of(scalarRule));
    private final FuzzCasePlanner queryParamsPlanner = new FuzzCasePlanner(List.of(nullAndMissingRule, scalarRule));

    @Override public ObjectMapper objectMapper() { return objectMapper; }
    @Override public FuzzCasePlanner payloadPlanner() { return payloadPlanner; }
    @Override public FuzzCasePlanner pathParamsPlanner() { return pathParamsPlanner; }
    @Override public FuzzCasePlanner queryParamsPlanner() { return queryParamsPlanner; }

    public NullAndMissingRule nullAndMissingRule() { return nullAndMissingRule; }
    public ScalarRule scalarRule() { return scalarRule; }
}