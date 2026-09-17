package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultFuzzEngine implements FuzzEngine {

    private final ObjectGraphDecomposer objectGraphDecomposer;
    private final ObjectMapper objectMapper;
    private final FuzzMutationApplier mutationApplier;
    private final List<FuzzRule> rules;

    public DefaultFuzzEngine(
            ObjectGraphDecomposer objectGraphDecomposer,
            ObjectMapper objectMapper,
            FuzzMutationApplier mutationApplier,
            List<FuzzRule> rules
    ) {
        this.objectGraphDecomposer = Objects.requireNonNull(objectGraphDecomposer, "objectGraphDecomposer must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.mutationApplier = Objects.requireNonNull(mutationApplier, "mutationApplier must not be null");
        this.rules = List.copyOf(Objects.requireNonNull(rules, "rules must not be null"));
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

            for (FuzzRule rule : rules) {
                for (Node node : graph.select(rule.selector())) {
                    for (FuzzMutation mutation : rule.mutationsFor(node, graph)) {
                        JsonNode mutated = mutationApplier.apply(baseline.deepCopy(), node.path(), mutation);
                        cases.add(new FuzzCase(node.path(), mutation, mutated));
                    }
                }
            }
            return cases;
        } catch (FuzzingException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new FuzzingException("Failed to generate fuzz cases", exception);
        }
    }
}
