package it.pagopa.interop.common.infrastructure.contract.http;

import java.util.function.Supplier;

record ScopeState<T>(
        Supplier<T> sourceSupplier,
        ScopeOverrides overrides
) {
}
