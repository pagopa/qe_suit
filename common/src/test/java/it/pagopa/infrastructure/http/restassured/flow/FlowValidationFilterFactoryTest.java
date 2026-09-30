package it.pagopa.infrastructure.http.restassured.flow;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlowValidationFilterFactoryTest {

    @Test
    void acceptedStatuses_doNotRegisterErrors() {
        List<String> errors = new ArrayList<>();
        Filter filter = FlowValidationFilterFactory.create(errors::add);

        invokeFilter(filter, 200);
        invokeFilter(filter, 204);
        invokeFilter(filter, 400);
        invokeFilter(filter, 404);

        assertTrue(errors.isEmpty());
    }

    @Test
    void nonAcceptedStatuses_registerErrorWithExpectedDetails() {
        List<String> errors = new ArrayList<>();
        Filter filter = FlowValidationFilterFactory.create(errors::add);

        invokeFilter(filter, 500);
        invokeFilter(filter, 502);

        assertEquals(2, errors.size());
        String firstError = errors.get(0);
        assertTrue(firstError.contains("HTTP 500"));
        assertTrue(firstError.contains("Method: POST"));
        assertTrue(firstError.contains("Endpoint: https://example.org/bff/resource"));
        assertTrue(firstError.contains("Payload: {\"id\":1}"));
        assertTrue(firstError.contains("Response: server error"));
    }

    @SuppressWarnings("unchecked")
    private static void invokeFilter(Filter filter, int statusCode) {
        FilterableRequestSpecification requestSpec = mock(FilterableRequestSpecification.class);
        FilterableResponseSpecification responseSpec = mock(FilterableResponseSpecification.class);
        FilterContext context = mock(FilterContext.class);
        Response response = mock(Response.class);
        ResponseBody<?> body = mock(ResponseBody.class);

        when(requestSpec.getMethod()).thenReturn("POST");
        when(requestSpec.getURI()).thenReturn("https://example.org/bff/resource");
        when(requestSpec.getBody()).thenReturn("{\"id\":1}");
        when(response.getStatusCode()).thenReturn(statusCode);
        when(response.getBody()).thenReturn(body);
        when(body.asString()).thenReturn("server error");
        when(context.next(requestSpec, responseSpec)).thenReturn(response);

        filter.filter(requestSpec, responseSpec, context);
    }
}
