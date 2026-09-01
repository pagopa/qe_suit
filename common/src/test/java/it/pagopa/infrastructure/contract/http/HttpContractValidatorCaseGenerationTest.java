package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import it.pagopa.infrastructure.fuzzing.FuzzCase;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.infrastructure.fuzzing.FuzzMutationKind;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.NodePath;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class HttpContractValidatorCaseGenerationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void payloadAndPathParamsCasesMutateExactlyOneRequestPartAndUseFreshOperation() throws Throwable {
        FuzzEngine fuzzEngine = source -> {
            if (source instanceof Payload) {
                JsonNode payload = objectMapper.createObjectNode().put("name", "");
                return List.of(new FuzzCase(
                        path("/name"),
                        new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""),
                        payload
                ));
            }

            JsonNode params = objectMapper.createObjectNode().put("agreementId", "not-a-valid-uuid");
            return List.of(new FuzzCase(
                    path("/agreementId"),
                    new FuzzMutation(FuzzScenario.REPLACED_WITH_MALFORMED_UUID, FuzzMutationKind.REPLACE, "not-a-valid-uuid"),
                    params
            ));
        };

        HttpContractValidator contract = new HttpContractValidator(
                objectMapper,
                fuzzEngine,
                createDecomposer(),
                completePolicy()
        );

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

        RecordingOper payloadMutated = operations.stream()
                .filter(o -> "valid-uuid".equals(o.pathAgreementId))
                .findFirst()
                .orElseThrow();

        RecordingOper pathMutated = operations.stream()
                .filter(o -> "not-a-valid-uuid".equals(o.pathAgreementId))
                .findFirst()
                .orElseThrow();

        assertEquals("{\"name\":\"\"}", payloadMutated.capturedBody);
        assertEquals("{\"name\":\"valid\"}", pathMutated.capturedBody);
    }

    @Test
    void failingExpectationIncludesDetailedDiagnostics() {
        FuzzEngine fuzzEngine = source -> List.of(new FuzzCase(
                path("/name"),
                new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""),
                objectMapper.createObjectNode().put("name", "")
        ));

        HttpContractValidator contract = new HttpContractValidator(
                objectMapper,
                fuzzEngine,
                createDecomposer(),
                completePolicy()
        );

        var tests = contract.apiCall(FailingOper::new)
                .payload(new Payload("valid"))
                .scenario(
                        FuzzScenario.REPLACED_WITH_EMPTY_STRING,
                        response -> response.then().statusCode(200)
                )
                .tests()
                .toList();

        AssertionError error = assertThrows(
                AssertionError.class,
                () -> tests.get(0).getExecutable().execute()
        );

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
            Class<?> jackson = Class.forName(
                    "it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer"
            );
            Constructor<?> jacksonCtor = jackson.getDeclaredConstructor(ObjectMapper.class);
            jacksonCtor.setAccessible(true);
            Object objectDecomposer = jacksonCtor.newInstance(objectMapper);

            Class<?> objectDecomposerType = Class.forName(
                    "it.pagopa.infrastructure.objectgraph.ObjectDecomposer"
            );
            Class<?> defaultType = Class.forName(
                    "it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer"
            );
            Constructor<?> defaultCtor = defaultType.getDeclaredConstructor(objectDecomposerType);
            defaultCtor.setAccessible(true);

            return (ObjectGraphDecomposer) defaultCtor.newInstance(objectDecomposer);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    record Payload(String name) {}

    record PathParams(String agreementId) {}

    public static class RecordingOper {
        final String id;
        String capturedBody;
        Object pathAgreementId;

        RecordingOper(String id) {
            this.id = id;
        }

        public RecordingOper reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new CapturingBuilder(this));
            return this;
        }

        public RecordingOper agreementIdPath(Object agreementId) {
            this.pathAgreementId = agreementId;
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            return handler.apply(Mockito.mock(Response.class));
        }
    }

    public static class FailingOper {
        public FailingOper reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new RequestSpecBuilder());
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            Response response = Mockito.mock(Response.class);
            ValidatableResponse validatable = Mockito.mock(ValidatableResponse.class);

            Mockito.when(response.then()).thenReturn(validatable);
            Mockito.when(validatable.statusCode(ArgumentMatchers.anyInt()))
                    .thenThrow(new AssertionError("unexpected status"));
            Mockito.when(response.getStatusCode()).thenReturn(400);
            Mockito.when(response.asString()).thenReturn("{\"detail\":\"bad request\"}");

            return handler.apply(response);
        }
    }

    static class CapturingBuilder extends RequestSpecBuilder {
        private final RecordingOper owner;

        CapturingBuilder(RecordingOper owner) {
            this.owner = owner;
        }

        @Override
        public RequestSpecBuilder setBody(Object body) {
            owner.capturedBody = String.valueOf(body);
            return this;
        }

        @Override
        public RequestSpecBuilder setBody(String body) {
            owner.capturedBody = body;
            return this;
        }
    }
}