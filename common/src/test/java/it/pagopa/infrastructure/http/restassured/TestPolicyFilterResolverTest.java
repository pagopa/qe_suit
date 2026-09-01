package it.pagopa.infrastructure.http.restassured;

import io.restassured.filter.Filter;
import it.pagopa.application.TestKind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class TestPolicyFilterResolverTest {

    @Test
    void resolve_returnsContractFilterForContractKind() {
        Filter contractFilter = mock(Filter.class);
        Filter flowFilter = mock(Filter.class);
        TestPolicyFilterResolver resolver = new TestPolicyFilterResolver(contractFilter, flowFilter);

        assertSame(contractFilter, resolver.resolve(TestKind.CONTRACT));
    }

    @Test
    void resolve_returnsFlowFilterForFlowKind() {
        Filter contractFilter = mock(Filter.class);
        Filter flowFilter = mock(Filter.class);
        TestPolicyFilterResolver resolver = new TestPolicyFilterResolver(contractFilter, flowFilter);

        assertSame(flowFilter, resolver.resolve(TestKind.FLOW));
    }
}
