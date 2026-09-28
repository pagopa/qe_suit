package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class OpenApiOperationAdapterTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OpenApiOperationAdapter adapter = new OpenApiOperationAdapter(objectMapper);

    @Test
    void executeReturnsSameResponseFromCallbackAndBindsBodyAndPath() {
        FakeOperation operation = new FakeOperation();
        ObjectNode payload = objectMapper.createObjectNode().put("name", "mario");
        ObjectNode path = objectMapper.createObjectNode().put("agreementId", "not-a-valid-uuid");
        Response response = adapter.execute(operation, new HttpContractRequest(payload, true, path));
        assertSame(operation.response, response);
        assertEquals("not-a-valid-uuid", operation.boundPathParams.get("agreementId"));
        assertFalse(operation.typedBodyCalled);
    }

    @Test
    void nullNodePayloadIsBoundAsJsonNull() {
        FakeOperation operation = new FakeOperation();
        adapter.execute(operation, new HttpContractRequest(NullNode.instance, true, null));
        verify(operation.requestSpecBuilder).setBody("null");
    }

    @Test
    void rootRemovedPayloadLeavesBodyAbsent() {
        FakeOperation operation = new FakeOperation();
        adapter.execute(operation, new HttpContractRequest(null, false, null));
        verify(operation.requestSpecBuilder, never()).setBody(anyString());
    }

    @Test
    void missingPathBindingMethodFailsFast() {
        NoPathOperation operation = new NoPathOperation();
        ObjectNode path = objectMapper.createObjectNode().put("agreementId", "x");
        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> adapter.execute(operation, new HttpContractRequest(null, false, path))
        );
        assertEquals(true, exception.getMessage().contains("Cannot bind path parameter 'agreementId'"));
    }

    @Test
    void bindsFormFieldsWhenOperationHasNoBodyMethod() {
        FakeFormOperation operation = new FakeFormOperation();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("name", "mario");
        payload.put("revision", 7);

        adapter.execute(operation, new HttpContractRequest(payload, true, null));

        assertEquals("mario", operation.boundFormParams.get("name"));
        assertEquals(7, ((Number) operation.boundFormParams.get("revision")).intValue());
        verify(operation.requestSpecBuilder, never()).setBody(anyString());
    }

    @Test
    void bindsNullFormFieldValueAsNullLiteral() {
        FakeFormOperation operation = new FakeFormOperation();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.set("name", NullNode.instance);

        adapter.execute(operation, new HttpContractRequest(payload, true, null));

        assertEquals("null", operation.boundFormParams.get("name"));
    }

    @Test
    void removedFormFieldIsNotBound() {
        FakeFormOperation operation = new FakeFormOperation();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("revision", 1);

        adapter.execute(operation, new HttpContractRequest(payload, true, null));

        assertFalse(operation.boundFormParams.containsKey("name"));
    }

    @Test
    void missingFormFieldBindingMethodFailsFast() {
        FakeFormOperation operation = new FakeFormOperation();
        ObjectNode payload = objectMapper.createObjectNode().put("unknown", "x");

        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> adapter.execute(operation, new HttpContractRequest(payload, true, null))
        );
        assertEquals(true, exception.getMessage().contains("Cannot bind form field 'unknown'"));
    }

    static class FakeFormOperation {
        final RequestSpecBuilder requestSpecBuilder = Mockito.spy(new RequestSpecBuilder());
        final Response response = Mockito.mock(Response.class);
        final Map<String, Object> boundFormParams = new LinkedHashMap<>();

        public FakeFormOperation reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(requestSpecBuilder);
            return this;
        }

        public FakeFormOperation nameForm(Object... values) {
            boundFormParams.put("name", values[0]);
            return this;
        }

        public FakeFormOperation revisionForm(Object... values) {
            boundFormParams.put("revision", values[0]);
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            return handler.apply(response);
        }
    }

    static class FakeOperation  {
        final RequestSpecBuilder requestSpecBuilder = Mockito.spy(new RequestSpecBuilder());
        final Response response = Mockito.mock(Response.class);
        final Map<String, Object> boundPathParams = new LinkedHashMap<>();
        boolean typedBodyCalled;

        public FakeOperation reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(requestSpecBuilder);
            return this;
        }

        public FakeOperation agreementIdPath(Object value) {
            boundPathParams.put("agreementId", value);
            return this;
        }

        public FakeOperation body(Object value) {
            typedBodyCalled = true;
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            return handler.apply(response);
        }
    }

    static class NoPathOperation  {
        public NoPathOperation reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new RequestSpecBuilder());
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            return handler.apply(Mockito.mock(Response.class));
        }
    }
}
