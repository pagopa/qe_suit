package it.pagopa.interop.common.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzCase;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzMutationKind;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.generated.openapi.clients.bff.api.Oper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpContractCaseGenerationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void payloadAndPathParamsCasesMutateExactlyOneRequestPartAndUseFreshOperation() throws Throwable {
        ObjectGraphDecomposer decomposer = createDecomposer();
        FuzzEngine fuzzEngine = source -> {
            if (source instanceof Payload) {
                JsonNode payload = objectMapper.createObjectNode().put("name", "");
                return List.of(new FuzzCase(path("/name"), new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""), payload));
            }
            JsonNode params = objectMapper.createObjectNode().put("agreementId", "not-a-valid-uuid");
            return List.of(new FuzzCase(path("/agreementId"), new FuzzMutation(FuzzScenario.REPLACED_WITH_MALFORMED_UUID, FuzzMutationKind.REPLACE, "not-a-valid-uuid"), params));
        };

        HttpContract contract = new HttpContract(objectMapper, fuzzEngine, decomposer, completePolicy());
        List<RecordingOper> operations = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger();
        var tests = contract.apiCall(() -> {
                    RecordingOper operation = new RecordingOper("op-" + counter.incrementAndGet());
                    operations.add(operation);
                    return operation;
                })
                .payload(new Payload("valid"))
                .pathParams(new PathParams("valid-uuid"))
                .tests()
                .toList();

        assertEquals(2, tests.size());
        tests.get(0).getExecutable().execute();
        tests.get(1).getExecutable().execute();
        assertEquals(2, operations.size());
        assertNotEquals(operations.get(0).id, operations.get(1).id);
        RecordingOper payloadMutated = operations.stream().filter(o -> "valid-uuid".equals(o.pathAgreementId)).findFirst().orElseThrow();
        RecordingOper pathMutated = operations.stream().filter(o -> "not-a-valid-uuid".equals(o.pathAgreementId)).findFirst().orElseThrow();
        assertEquals("{\"name\":\"\"}", payloadMutated.capturedBody);
        assertEquals("{\"name\":\"valid\"}", pathMutated.capturedBody);
    }

    @Test
    void failingExpectationIncludesDetailedDiagnostics() {
        ObjectGraphDecomposer decomposer = createDecomposer();
        FuzzEngine fuzzEngine = source -> List.of(new FuzzCase(
                path("/name"),
                new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""),
                objectMapper.createObjectNode().put("name", "")
        ));
        HttpContract contract = new HttpContract(objectMapper, fuzzEngine, decomposer, completePolicy());

        var tests = contract.apiCall(FailingOper::new)
                .payload(new Payload("valid"))
                .scenario(FuzzScenario.REPLACED_WITH_EMPTY_STRING, response -> response.then().statusCode(200))
                .tests()
                .toList();

        AssertionError error = assertThrows(AssertionError.class, () -> tests.get(0).getExecutable().execute());
        assertTrue(error.getMessage().contains("scenario: REPLACED_WITH_EMPTY_STRING"));
        assertTrue(error.getMessage().contains("targetPath: /name"));
        assertTrue(error.getMessage().contains("responseStatus: 400"));
        assertTrue(error.getMessage().contains("payload: {\"name\":\"\"}"));
    }

    private HttpContractPolicy completePolicy() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().success(response -> {});
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenario(scenario, response -> {});
        }
        return builder.build();
    }

    private NodePath path(String pointer) {
        try {
            Constructor<NodePath> constructor = NodePath.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(pointer);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    private ObjectGraphDecomposer createDecomposer() {
        try {
            Class<?> jackson = Class.forName("it.pagopa.interop.common.infrastructure.objectgraph.JacksonObjectDecomposer");
            Constructor<?> jacksonCtor = jackson.getDeclaredConstructor(ObjectMapper.class);
            jacksonCtor.setAccessible(true);
            Object objectDecomposer = jacksonCtor.newInstance(objectMapper);
            Class<?> defaultCls = Class.forName("it.pagopa.interop.common.infrastructure.objectgraph.DefaultObjectGraphDecomposer");
            Constructor<?> defaultCtor = defaultCls.getDeclaredConstructor(Class.forName("it.pagopa.interop.common.infrastructure.objectgraph.ObjectDecomposer"));
            defaultCtor.setAccessible(true);
            return (ObjectGraphDecomposer) defaultCtor.newInstance(objectDecomposer);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    record Payload(String name) {
    }

    record PathParams(String agreementId) {
    }

    static class RecordingOper implements Oper {
        final String id;
        String capturedBody;
        Object pathAgreementId;

        RecordingOper(String id) {
            this.id = id;
        }

        public RecordingOper reqSpec(java.util.function.Consumer<io.restassured.builder.RequestSpecBuilder> customizer) {
            customizer.accept(new CapturingBuilder(this));
            return this;
        }

        public RecordingOper agreementIdPath(Object agreementId) {
            this.pathAgreementId = agreementId;
            return this;
        }

        @Override
        public <T> T execute(Function<io.restassured.response.Response, T> handler) {
            return handler.apply(org.mockito.Mockito.mock(io.restassured.response.Response.class));
        }
    }

    static class FailingOper implements Oper {
        public FailingOper reqSpec(java.util.function.Consumer<io.restassured.builder.RequestSpecBuilder> customizer) {
            customizer.accept(new io.restassured.builder.RequestSpecBuilder());
            return this;
        }

        @Override
        public <T> T execute(Function<io.restassured.response.Response, T> handler) {
            io.restassured.response.Response response = org.mockito.Mockito.mock(io.restassured.response.Response.class);
            org.mockito.Mockito.when(response.getStatusCode()).thenReturn(400);
            org.mockito.Mockito.when(response.asString()).thenReturn("{\"detail\":\"bad request\"}");
            return handler.apply(response);
        }
    }

    static class CapturingBuilder extends io.restassured.builder.RequestSpecBuilder {
        private final RecordingOper owner;

        CapturingBuilder(RecordingOper owner) {
            this.owner = owner;
        }

        @Override
        public io.restassured.builder.RequestSpecBuilder setBody(Object body) {
            owner.capturedBody = String.valueOf(body);
            return this;
        }

        @Override
        public io.restassured.builder.RequestSpecBuilder setBody(String body) {
            owner.capturedBody = body;
            return this;
        }
    }
}
