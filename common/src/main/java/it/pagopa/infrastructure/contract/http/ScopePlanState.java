package it.pagopa.infrastructure.contract.http;

import it.pagopa.infrastructure.fuzzing.FuzzCase;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;

import java.util.List;

record ScopePlanState<T>(
        T source,
        Class<T> sourceType,
        ObjectGraph graph,
        List<FuzzCase> fuzzCases,
        ScopeOverrides overrides
) {
}
