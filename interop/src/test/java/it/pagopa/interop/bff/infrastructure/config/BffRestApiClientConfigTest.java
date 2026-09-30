package it.pagopa.interop.bff.infrastructure.config;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BffRestApiClientConfigTest {

    @Test
    void testPolicyFilterResolver_wiresContractAndFlowPolicies() {
        TestContext testContext = mock(TestContext.class);
        BffRestApiClientConfig config = new BffRestApiClientConfig(testContext);

        URL openApiSpec = BffRestApiClientConfigTest.class.getResource("/openapi/minimal-api.yaml");
        if (openApiSpec == null) {
            throw new IllegalStateException("Missing openapi test resource");
        }

        setField(config, "basePath", "https://example.org/bff");
        setField(config, "openApiSpecUrl", openApiSpec.toExternalForm());

        TestPolicyFilterResolver resolver = config.testPolicyFilterResolver();
        Filter contractFilter = resolver.resolve(TestKind.CONTRACT);
        Filter flowFilter = resolver.resolve(TestKind.FLOW);

        assertInstanceOf(OpenApiValidationFilter.class, contractFilter);

        invokeFlowFilterWithServerError(flowFilter);
        verify(testContext).addEventualConsistencyError(org.mockito.ArgumentMatchers.contains("HTTP 500"));
    }

    @SuppressWarnings("unchecked")
    private static void invokeFlowFilterWithServerError(Filter flowFilter) {
        FilterableRequestSpecification requestSpec = mock(FilterableRequestSpecification.class);
        FilterableResponseSpecification responseSpec = mock(FilterableResponseSpecification.class);
        FilterContext context = mock(FilterContext.class);
        Response response = mock(Response.class);
        ResponseBody<?> responseBody = mock(ResponseBody.class);

        when(requestSpec.getMethod()).thenReturn("POST");
        when(requestSpec.getURI()).thenReturn("https://example.org/bff/resource");
        when(requestSpec.getBody()).thenReturn("{\"payload\":true}");
        when(response.getStatusCode()).thenReturn(500);
        when(response.getBody()).thenReturn(responseBody);
        when(responseBody.asString()).thenReturn("failure");
        when(context.next(requestSpec, responseSpec)).thenReturn(response);

        flowFilter.filter(requestSpec, responseSpec, context);
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
}
