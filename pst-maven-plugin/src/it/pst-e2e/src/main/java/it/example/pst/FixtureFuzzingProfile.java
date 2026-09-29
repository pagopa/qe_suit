package it.example.pst;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzCasePlanner;
import it.pagopa.infrastructure.fuzzing.FuzzingProfile;
import it.pagopa.infrastructure.fuzzing.NullAndMissingRule;
import it.pagopa.infrastructure.fuzzing.ScalarRule;

import java.util.List;

public final class FixtureFuzzingProfile implements FuzzingProfile {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FuzzCasePlanner payloadPlanner =
            new FuzzCasePlanner(List.of(new NullAndMissingRule(), new ScalarRule()));
    private final FuzzCasePlanner pathParamsPlanner = new FuzzCasePlanner(List.of(new ScalarRule()));

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
}
