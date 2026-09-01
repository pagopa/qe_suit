package it.pagopa.interop.bff.infrastructure.config;

import io.restassured.filter.Filter;
import io.restassured.specification.FilterableRequestSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BffRequestSpecFactoryTest {

    @Test
    void create_withContractKind_addsContractFilterAndHttpLoggingFilter() {
        Filter contractFilter = mock(Filter.class);
        Filter flowFilter = mock(Filter.class);
        BuiltSpec builtSpec = buildSpecFor(TestKind.CONTRACT, contractFilter, flowFilter);

        List<Filter> filters = builtSpec.requestSpecification.getDefinedFilters();
        assertEquals(2, filters.size());
        assertSame(contractFilter, filters.get(0));
        assertInstanceOf(HttpLoggingFilter.class, filters.get(1));
        assertEquals("Bearer test-token", builtSpec.requestSpecification.getHeaders().getValue("Authorization"));
        assertEquals("https://example.org/bff", builtSpec.requestSpecification.getBaseUri());
    }

    @Test
    void create_withFlowKind_addsFlowFilterAndHttpLoggingFilter() {
        Filter contractFilter = mock(Filter.class);
        Filter flowFilter = mock(Filter.class);
        BuiltSpec builtSpec = buildSpecFor(TestKind.FLOW, contractFilter, flowFilter);

        List<Filter> filters = builtSpec.requestSpecification.getDefinedFilters();
        assertEquals(2, filters.size());
        assertSame(flowFilter, filters.get(0));
        assertInstanceOf(HttpLoggingFilter.class, filters.get(1));
        assertEquals("Bearer test-token", builtSpec.requestSpecification.getHeaders().getValue("Authorization"));
        assertEquals("https://example.org/bff", builtSpec.requestSpecification.getBaseUri());
    }

    @SuppressWarnings("unchecked")
    private static BuiltSpec buildSpecFor(TestKind testKind, Filter contractFilter, Filter flowFilter) {
        BearerAuthProvider bearerAuthProvider = mock(BearerAuthProvider.class);
        ObjectProvider<TestContext> testContextProvider = mock(ObjectProvider.class);
        ObjectProvider<CurrentUserSession> currentUserSessionProvider = mock(ObjectProvider.class);
        TestContext testContext = mock(TestContext.class);
        CurrentUserSession currentUserSession = mock(CurrentUserSession.class);
        TestPolicyFilterResolver resolver = new TestPolicyFilterResolver(contractFilter, flowFilter);

        when(testContextProvider.getObject()).thenReturn(testContext);
        when(currentUserSessionProvider.getObject()).thenReturn(currentUserSession);
        when(testContext.getCurrentTestKind()).thenReturn(testKind);
        when(currentUserSession.getUser()).thenReturn(User.S_MATTIA);
        when(currentUserSession.getTenant()).thenReturn(Tenant.COMUNE_DI_MILANO);
        when(bearerAuthProvider.getToken(User.S_MATTIA, Tenant.COMUNE_DI_MILANO)).thenReturn("test-token");

        BffRequestSpecFactory factory = new BffRequestSpecFactory(
                bearerAuthProvider,
                testContextProvider,
                currentUserSessionProvider,
                resolver
        );
        setField(factory, "basePath", "https://example.org/bff");

        FilterableRequestSpecification requestSpecification =
                (FilterableRequestSpecification) factory.create().build();

        return new BuiltSpec(requestSpecification);
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot set field " + fieldName, e);
        }
    }

    private record BuiltSpec(FilterableRequestSpecification requestSpecification) {
    }
}
