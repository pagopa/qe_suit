package it.pagopa.interop.common.infrastructure.contract.http;

import it.pagopa.interop.common.infrastructure.fuzzing.FuzzCase;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;

import java.util.List;

record ScopeState<T>(
        T source,
        Class<T> sourceType,
        ObjectGraph graph,
        List<FuzzCase> fuzzCases,
        ScopeOverrides overrides
) {
}
