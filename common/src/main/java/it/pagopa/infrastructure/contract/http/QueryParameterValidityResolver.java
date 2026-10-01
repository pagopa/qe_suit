package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.Node;

import java.util.Map;
import java.util.Objects;

/**
 * Validity of query parameter mutations.
 * <p>
 * Query parameters have no generated DTO, so no validation annotation is available:
 * the only contract metadata is {@code required}, taken from the OpenAPI specification.
 * Removing or nulling an optional parameter is valid by contract; doing the same on a
 * required one is not. Every other scenario stays {@link ContractValidity#UNKNOWN} and is
 * therefore resolved by the contract policy, exactly like for the other scopes.
 * <p>
 * Parameters that are not described (empty map) are treated as optional, which mirrors the
 * runtime convention that a call without query parameters must succeed.
 */
public final class QueryParameterValidityResolver implements MutationValidityResolver {

    private final Map<String, Boolean> requiredByName;

    public QueryParameterValidityResolver(Map<String, Boolean> requiredByName) {
        this.requiredByName = Map.copyOf(Objects.requireNonNull(requiredByName, "requiredByName must not be null"));
    }

    @Override
    public ContractValidity resolve(Node node, FuzzMutation mutation) {
        FuzzScenario scenario = mutation.scenario();
        if (scenario != FuzzScenario.REMOVED && scenario != FuzzScenario.REPLACED_WITH_NULL) {
            return ContractValidity.UNKNOWN;
        }
        if (node.path().isRoot()) return ContractValidity.UNKNOWN;

        // Only the parameter itself carries the required/optional contract: nested nodes
        // (array elements) are left to the policy.
        String pointer = node.path().toString();
        String parameterName = pointer.substring(1);
        if (parameterName.indexOf('/') >= 0) return ContractValidity.UNKNOWN;
        parameterName = parameterName.replace("~1", "/").replace("~0", "~");

        Boolean required = requiredByName.get(parameterName);
        if (required == null) return ContractValidity.UNKNOWN;
        return required ? ContractValidity.INVALID : ContractValidity.VALID;
    }
}
