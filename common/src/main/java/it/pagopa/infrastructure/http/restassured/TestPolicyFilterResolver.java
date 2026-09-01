package it.pagopa.infrastructure.http.restassured;

import io.restassured.filter.Filter;
import it.pagopa.application.TestKind;

import java.util.Objects;

public final class TestPolicyFilterResolver {

    private final Filter contractFilter;
    private final Filter flowFilter;

    public TestPolicyFilterResolver(Filter contractFilter, Filter flowFilter) {
        this.contractFilter = Objects.requireNonNull(contractFilter);
        this.flowFilter = Objects.requireNonNull(flowFilter);
    }

    public Filter resolve(TestKind testKind) {
        return switch (testKind) {
            case CONTRACT -> contractFilter;
            case FLOW -> flowFilter;
        };
    }
}
