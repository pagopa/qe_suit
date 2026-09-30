package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzCase;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;

import java.util.List;

record ScopePlanState<T>(
        T source,
        Class<T> sourceType,
        ObjectGraph graph,
        List<FuzzCase> fuzzCases,
        ScopeOverrides overrides,
        /** Optional contract metadata for the scope; when null the planner applies its default. */
        MutationValidityResolver validityResolver
) {
    ScopePlanState(
            T source,
            Class<T> sourceType,
            ObjectGraph graph,
            List<FuzzCase> fuzzCases,
            ScopeOverrides overrides
    ) {
        this(source, sourceType, graph, fuzzCases, overrides, null);
    }
}
