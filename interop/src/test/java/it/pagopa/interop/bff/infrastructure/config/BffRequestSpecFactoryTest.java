package it.pagopa.interop.bff.infrastructure.config;

import io.restassured.filter.Filter;
import io.restassured.specification.FilterableRequestSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.http.restassured.AcceptLanguageFilter;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.HttpLoggingFilter;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

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
        assertEquals(3, filters.size());
        assertInstanceOf(AcceptLanguageFilter.class, filters.get(0));
        assertSame(contractFilter, filters.get(1));
        assertInstanceOf(HttpLoggingFilter.class, filters.get(2));
        assertEquals("Bearer test-token", builtSpec.requestSpecification.getHeaders().getValue("Authorization"));
        assertEquals("https://example.org/bff", builtSpec.requestSpecification.getBaseUri());
    }

    @Test
    void create_withFlowKind_addsFlowFilterAndHttpLoggingFilter() {
        Filter contractFilter = mock(Filter.class);
        Filter flowFilter = mock(Filter.class);
        BuiltSpec builtSpec = buildSpecFor(TestKind.FLOW, contractFilter, flowFilter);

        List<Filter> filters = builtSpec.requestSpecification.getDefinedFilters();
        assertEquals(3, filters.size());
        assertInstanceOf(AcceptLanguageFilter.class, filters.get(0));
        assertSame(flowFilter, filters.get(1));
        assertInstanceOf(HttpLoggingFilter.class, filters.get(2));
        assertEquals("Bearer test-token", builtSpec.requestSpecification.getHeaders().getValue("Authorization"));
        assertEquals("https://example.org/bff", builtSpec.requestSpecification.getBaseUri());
    }

    @Test
    void languageFilterReadsContextAndSetsAcceptLanguage() {
        Filter policy = mock(Filter.class);
        BuiltSpec spec = buildSpecFor(TestKind.FLOW, policy, policy);
        var filter = (AcceptLanguageFilter) spec.requestSpecification.getDefinedFilters().get(0);
        var responseSpec = mock(io.restassured.specification.FilterableResponseSpecification.class);
        var context = mock(io.restassured.filter.FilterContext.class);
        filter.filter(spec.requestSpecification, responseSpec, context);
        assertEquals("en", spec.requestSpecification.getHeaders().getValue("Accept-Language"));
    }

    @SuppressWarnings("unchecked")
    private static BuiltSpec buildSpecFor(TestKind testKind, Filter contractFilter, Filter flowFilter) {
        BearerAuthProvider bearerAuthProvider = mock(BearerAuthProvider.class);
        ObjectProvider<TestContext> testContextProvider = mock(ObjectProvider.class);
        ObjectProvider<AuthSession> currentUserSessionProvider = mock(ObjectProvider.class);
        TestContext testContext = mock(TestContext.class);
        AuthSession authSession = mock(AuthSession.class);
        TestPolicyFilterResolver resolver = new TestPolicyFilterResolver(contractFilter, flowFilter);
        ObjectProvider<CurrentChannel<Channel>> channelProvider = mock(ObjectProvider.class);
        CurrentChannel<Channel> channel = mock(CurrentChannel.class);
        when(channelProvider.getObject()).thenReturn(channel);
        when(channel.getCurrentLanguage()).thenReturn(Locale.ENGLISH);

        when(testContextProvider.getObject()).thenReturn(testContext);
        when(currentUserSessionProvider.getObject()).thenReturn(authSession);
        when(testContext.getCurrentTestKind()).thenReturn(testKind);
        when(authSession.getActor()).thenReturn(User.S_MATTIA);
        when(authSession.getTenant()).thenReturn(Tenant.COMUNE_DI_MILANO);
        when(bearerAuthProvider.getToken(User.S_MATTIA, Tenant.COMUNE_DI_MILANO)).thenReturn("test-token");

        BffRequestSpecFactory factory = new BffRequestSpecFactory(
                bearerAuthProvider,
                testContextProvider,
                currentUserSessionProvider,
                resolver,
                channelProvider
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
