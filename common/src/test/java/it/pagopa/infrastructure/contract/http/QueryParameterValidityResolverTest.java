package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.NodePath;
import it.pagopa.infrastructure.objectgraph.NodeKind;
import it.pagopa.infrastructure.fuzzing.FuzzMutationKind;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryParameterValidityResolverTest {

    private final QueryParameterValidityResolver resolver =
            new QueryParameterValidityResolver(Map.of("q", true, "limit", false));

    @Test
    void removingAnOptionalParameterIsValid() {
        assertEquals(ContractValidity.VALID, resolve("limit", FuzzScenario.REMOVED));
        assertEquals(ContractValidity.VALID, resolve("limit", FuzzScenario.REPLACED_WITH_NULL));
    }

    @Test
    void removingARequiredParameterIsInvalid() {
        assertEquals(ContractValidity.INVALID, resolve("q", FuzzScenario.REMOVED));
        assertEquals(ContractValidity.INVALID, resolve("q", FuzzScenario.REPLACED_WITH_NULL));
    }

    @Test
    void otherScenariosAreLeftToThePolicy() {
        assertEquals(ContractValidity.UNKNOWN, resolve("limit", FuzzScenario.REPLACED_WITH_SQL_INJECTION));
        assertEquals(ContractValidity.UNKNOWN, resolve("q", FuzzScenario.REPLACED_WITH_EMPTY_STRING));
    }

    @Test
    void undescribedAndNestedParametersAreLeftToThePolicy() {
        assertEquals(ContractValidity.UNKNOWN, resolve("unknown", FuzzScenario.REMOVED));
        assertEquals(ContractValidity.UNKNOWN, resolve(NodePath.root(), FuzzScenario.REMOVED));
        assertEquals(
                ContractValidity.UNKNOWN,
                resolve(NodePath.fromPointer("/limit/0"), FuzzScenario.REMOVED)
        );
    }

    private ContractValidity resolve(String parameter, FuzzScenario scenario) {
        return resolve(NodePath.fromPointer("/" + parameter), scenario);
    }

    private ContractValidity resolve(NodePath path, FuzzScenario scenario) {
        return resolver.resolve(
                new Node(NodeKind.SCALAR, path, "seed", String.class),
                new FuzzMutation(scenario, FuzzMutationKind.REMOVE, null)
        );
    }
}
