package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultFuzzEngine implements FuzzEngine {

    private final ObjectGraphDecomposer objectGraphDecomposer;
    private final ObjectMapper objectMapper;
    private final FuzzMutationApplier mutationApplier;
    private final FuzzCasePlanner fuzzCasePlanner;

    public DefaultFuzzEngine(
            ObjectGraphDecomposer objectGraphDecomposer,
            ObjectMapper objectMapper,
            FuzzMutationApplier mutationApplier,
            List<FuzzRule> rules
    ) {
        this.objectGraphDecomposer = Objects.requireNonNull(objectGraphDecomposer, "objectGraphDecomposer must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.mutationApplier = Objects.requireNonNull(mutationApplier, "mutationApplier must not be null");
        this.fuzzCasePlanner = new FuzzCasePlanner(rules);
    }

    public DefaultFuzzEngine(
            ObjectGraphDecomposer objectGraphDecomposer,
            ObjectMapper objectMapper,
            FuzzMutationApplier mutationApplier,
            FuzzCasePlanner fuzzCasePlanner
    ) {
        this.objectGraphDecomposer = Objects.requireNonNull(objectGraphDecomposer, "objectGraphDecomposer must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.mutationApplier = Objects.requireNonNull(mutationApplier, "mutationApplier must not be null");
        this.fuzzCasePlanner = Objects.requireNonNull(fuzzCasePlanner, "fuzzCasePlanner must not be null");
    }

    @Override
    public List<FuzzCase> generate(Object source) {
        if (source == null) {
            throw new FuzzingException("source must not be null");
        }

        try {
            ObjectGraph graph = objectGraphDecomposer.decompose(source);
            JsonNode baseline = objectMapper.valueToTree(source);
            List<FuzzCase> cases = new ArrayList<>();

            for (PlannedFuzzCase plannedCase : fuzzCasePlanner.plan(graph)) {
                JsonNode mutated = mutationApplier.apply(
                        baseline.deepCopy(),
                        plannedCase.target(),
                        plannedCase.mutation()
                );
                cases.add(new FuzzCase(plannedCase.target(), plannedCase.mutation(), mutated));
            }
            return cases;
        } catch (FuzzingException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new FuzzingException("Failed to generate fuzz cases", exception);
        }
    }
}
