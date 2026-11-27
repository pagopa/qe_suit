package it.pagopa.interop.bff.infrastructure.config;

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.sun.net.httpserver.HttpServer;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import it.pagopa.application.TestKind;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.http.restassured.TestPolicyFilterResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BffRestApiClientConfigTest {

    private HttpServer server;
    private final List<String> requests = new CopyOnWriteArrayList<>();
    private final AtomicInteger attempts = new AtomicInteger();

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void retryOnConflict_retriesConflictsWithTheSameRequest() throws IOException {
        startServer(2, 200);

        sendRequest().then().statusCode(200);

        assertEquals(3, attempts.get());
        assertEquals(List.of(expectedRequest(), expectedRequest(), expectedRequest()), requests);
    }

    @Test
    void retryOnConflict_returnsConflictAfterThreeRetries() throws IOException {
        startServer(Integer.MAX_VALUE, 200);

        sendRequest().then().statusCode(409);

        assertEquals(4, attempts.get());
    }

    @Test
    void retryOnConflict_doesNotRetryOtherErrors() throws IOException {
        startServer(0, 400);

        sendRequest().then().statusCode(400);

        assertEquals(1, attempts.get());
    }

    private void startServer(int conflicts, int finalStatus) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI()
                    + " " + exchange.getRequestHeaders().getFirst("Authorization") + " " + body);
            int status = attempts.incrementAndGet() <= conflicts ? 409 : finalStatus;
            exchange.sendResponseHeaders(status, -1);
            exchange.close();
        });
        server.start();
    }

    private Response sendRequest() {
        return RestAssured.given()
                .baseUri("http://127.0.0.1")
                .port(server.getAddress().getPort())
                .header("Authorization", "Bearer token")
                .contentType("application/json")
                .pathParam("eServiceId", "service")
                .pathParam("descriptorId", "draft")
                .queryParam("test", "value")
                .body("{\"attributes\":{\"certified\":[]}}")
                .filter(BffRestApiClientConfig.retryOnConflict(3, 0, 0))
                .put("/eservices/{eServiceId}/descriptors/{descriptorId}");
    }

    private String expectedRequest() {
        return "PUT /eservices/service/descriptors/draft?test=value"
                + " Bearer token {\"attributes\":{\"certified\":[]}}";
    }

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
