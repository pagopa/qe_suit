package it.pagopa.interop.common.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzCase;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzMutation;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzMutationKind;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.interop.generated.openapi.clients.bff.api.Oper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpContractValidatorSupplierSemanticsTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void supplierOverloadsAreAvailableFromAllStagesAndNullSuppliersFailFast() {
        HttpContractValidator contract = new HttpContractValidator(objectMapper, source -> List.of(), createDecomposer(), completePolicy());

        Supplier<Payload> payloadSupplier = () -> new Payload("id", "name", List.of(new Contact("x@y")));
        Supplier<PathParams> pathSupplier = () -> new PathParams("agreement", "descriptor");

        assertNotNull(contract.apiCall(SimpleOper::new).payload(payloadSupplier));
        assertNotNull(contract.apiCall(SimpleOper::new).pathParams(pathSupplier));
        assertNotNull(contract.apiCall(SimpleOper::new).payload(payloadSupplier).pathParams(pathSupplier));
        assertNotNull(contract.apiCall(SimpleOper::new).pathParams(pathSupplier).payload(payloadSupplier));
        assertNotNull(contract.apiCall(SimpleOper::new).payload(new Payload("id", "name", List.of(new Contact("x@y")))));
        assertNotNull(contract.apiCall(SimpleOper::new).pathParams(new PathParams("agreement", "descriptor")));

        assertThrows(ContractHttpException.class, () -> contract.apiCall(SimpleOper::new).payload((Supplier<Payload>) null));
        assertThrows(ContractHttpException.class, () -> contract.apiCall(SimpleOper::new).pathParams((Supplier<PathParams>) null));
        assertThrows(
                ContractHttpException.class,
                () -> contract.apiCall(SimpleOper::new).payload(payloadSupplier).pathParams((Supplier<PathParams>) null)
        );
        assertThrows(
                ContractHttpException.class,
                () -> contract.apiCall(SimpleOper::new).pathParams(pathSupplier).payload((Supplier<Payload>) null)
        );
    }

    @Test
    void supplierRegistrationDoesNotEagerlyEvaluate() {
        AtomicInteger payloadCounter = new AtomicInteger();
        AtomicInteger pathCounter = new AtomicInteger();
        FuzzEngine fuzzEngine = source -> source instanceof Payload
                ? List.of(payloadNameCase(""))
                : List.of(pathAgreementCase("bad-uuid"));
        HttpContractValidator contract = new HttpContractValidator(objectMapper, fuzzEngine, createDecomposer(), completePolicy());

        var stage = contract.apiCall(SimpleOper::new)
                .payload(() -> {
                    payloadCounter.incrementAndGet();
                    return new Payload("id", "name", List.of(new Contact("x@y")));
                })
                .pathParams(() -> {
                    pathCounter.incrementAndGet();
                    return new PathParams("agreement", "descriptor");
                });

        assertEquals(0, payloadCounter.get());
        assertEquals(0, pathCounter.get());

        stage.tests().toList();

        assertEquals(1, payloadCounter.get());
        assertEquals(1, pathCounter.get());
    }

    @Test
    void eachDynamicTestUsesFreshPayloadAndPathParamsWithSingleMutationInvariant() throws Throwable {
        AtomicInteger payloadCounter = new AtomicInteger();
        AtomicInteger pathCounter = new AtomicInteger();
        AtomicInteger operationCounter = new AtomicInteger();
        ConcurrentLinkedQueue<CapturingOper> operations = new ConcurrentLinkedQueue<>();
        FuzzEngine fuzzEngine = source -> {
            if (source instanceof Payload payload) {
                return List.of(payloadNameCase("", payload.id()));
            }
            if (source instanceof PathParams pathParams) {
                return List.of(pathAgreementCase("malformed-agreement", pathParams.descriptorId()));
            }
            return List.of();
        };
        HttpContractValidator contract = new HttpContractValidator(objectMapper, fuzzEngine, createDecomposer(), completePolicy());

        var tests = contract.apiCall(() -> {
                    CapturingOper operation = new CapturingOper("op-" + operationCounter.incrementAndGet());
                    operations.add(operation);
                    return operation;
                })
                .payload(() -> new Payload("payload-" + payloadCounter.incrementAndGet(), "valid-name", List.of(new Contact("x@y"))))
                .pathParams(() -> new PathParams("agreement-" + pathCounter.incrementAndGet(), "descriptor-" + pathCounter.get()))
                .tests()
                .toList();

        tests.get(0).getExecutable().execute();
        tests.get(1).getExecutable().execute();

        assertEquals(3, payloadCounter.get());
        assertEquals(3, pathCounter.get());
        assertEquals(2, operations.size());

        CapturingOper payloadMutated = operations.stream()
                .filter(operation -> !"malformed-agreement".equals(operation.pathAgreementId))
                .findFirst()
                .orElseThrow();
        assertTrue(payloadMutated.bodyJson.contains("\"name\":\"\""), payloadMutated.bodyJson);
        assertTrue(payloadMutated.pathAgreementId.startsWith("agreement-"));
        assertTrue(payloadMutated.pathDescriptorId.startsWith("descriptor-"));
        assertTrue(payloadMutated.bodyJson.contains("\"id\":\"payload-"));

        CapturingOper pathMutated = operations.stream()
                .filter(operation -> "malformed-agreement".equals(operation.pathAgreementId))
                .findFirst()
                .orElseThrow();
        assertTrue(pathMutated.bodyJson.contains("\"name\":\"valid-name\""));
        assertTrue(pathMutated.bodyJson.contains("\"id\":\"payload-"));
        assertTrue(pathMutated.pathDescriptorId.startsWith("descriptor-"));
    }

    @Test
    void dynamicValuesAreTakenFromRuntimeSupplierInvocationNotPlanningBaseline() throws Throwable {
        AtomicInteger payloadCounter = new AtomicInteger();
        FuzzEngine fuzzEngine = source -> {
            Payload payload = (Payload) source;
            return List.of(payloadNameCase("", payload.id()));
        };
        HttpContractValidator contract = new HttpContractValidator(objectMapper, fuzzEngine, createDecomposer(), completePolicy());
        CapturingOper operation = new CapturingOper("op");

        var tests = contract.apiCall(() -> operation)
                .payload(() -> new Payload("uuid-" + payloadCounter.incrementAndGet(), "valid", List.of(new Contact("x@y"))))
                .tests()
                .toList();

        tests.get(0).getExecutable().execute();

        assertEquals(2, payloadCounter.get());
        assertTrue(operation.bodyJson.contains("\"id\":\"uuid-2\""), operation.bodyJson);
        assertTrue(!operation.bodyJson.contains("\"id\":\"uuid-1\""), operation.bodyJson);
    }

    @Test
    void shapeChangeBetweenDiscoveryAndExecutionFailsFast() throws Throwable {
        AtomicInteger payloadCounter = new AtomicInteger();
        FuzzEngine fuzzEngine = source -> {
            Payload payload = (Payload) source;
            if (payload.contacts().isEmpty()) return List.of();
            return List.of(new FuzzCase(
                    path("/contacts/0/email"),
                    new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""),
                    objectMapper.createObjectNode()
                            .put("id", payload.id())
                            .put("name", payload.name())
                            .putArray("contacts")
                            .add(objectMapper.createObjectNode().put("email", ""))
            ));
        };
        HttpContractValidator contract = new HttpContractValidator(objectMapper, fuzzEngine, createDecomposer(), completePolicy());
        var tests = contract.apiCall(SimpleOper::new)
                .payload(() -> payloadCounter.incrementAndGet() == 1
                        ? new Payload("id", "name", List.of(new Contact("a@b")))
                        : new Payload("id", "name", List.of()))
                .tests()
                .toList();

        ContractHttpException exception = assertThrows(ContractHttpException.class, () -> tests.get(0).getExecutable().execute());
        assertTrue(exception.getMessage().contains("Cannot rebuild planned case on fresh baseline"));
        assertTrue(exception.getMessage().contains("PAYLOAD"));
        assertTrue(exception.getMessage().contains("/contacts/0/email"));
        assertTrue(exception.getMessage().contains("REPLACED_WITH_EMPTY_STRING"));
    }

    @Test
    void duplicateRuntimeMatchesFailFast() throws Throwable {
        AtomicInteger engineCounter = new AtomicInteger();
        FuzzEngine fuzzEngine = source -> {
            int invocation = engineCounter.incrementAndGet();
            FuzzCase caseA = payloadNameCase("");
            if (invocation == 1) return List.of(caseA);
            return List.of(caseA, payloadNameCase(""));
        };
        HttpContractValidator contract = new HttpContractValidator(objectMapper, fuzzEngine, createDecomposer(), completePolicy());
        var tests = contract.apiCall(SimpleOper::new)
                .payload(() -> new Payload("id", "name", List.of(new Contact("x@y"))))
                .tests()
                .toList();

        ContractHttpException exception = assertThrows(ContractHttpException.class, () -> tests.get(0).getExecutable().execute());
        assertTrue(exception.getMessage().contains("Non-deterministic runtime case rebuild"));
    }

    @Test
    void supplierExceptionsKeepOriginalCauseAndContext() {
        AtomicInteger counter = new AtomicInteger();
        FuzzEngine fuzzEngine = source -> List.of(payloadNameCase(""));
        HttpContractValidator contract = new HttpContractValidator(objectMapper, fuzzEngine, createDecomposer(), completePolicy());
        var tests = contract.apiCall(SimpleOper::new)
                .payload(() -> {
                    if (counter.incrementAndGet() == 1) return new Payload("id", "name", List.of(new Contact("x@y")));
                    throw new IllegalStateException("payload boom");
                })
                .tests()
                .toList();

        ContractHttpException exception = assertThrows(ContractHttpException.class, () -> tests.get(0).getExecutable().execute());
        assertTrue(exception.getMessage().contains("payload supplier failed during execution"));
        assertNotNull(exception.getCause());
        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertEquals("payload boom", exception.getCause().getMessage());
    }

    @Test
    void concurrentExecutionsDoNotShareRuntimeValues() throws Exception {
        AtomicInteger payloadCounter = new AtomicInteger();
        FuzzEngine fuzzEngine = source -> {
            Payload payload = (Payload) source;
            return List.of(payloadNameCase("", payload.id()));
        };
        HttpContractValidator contract = new HttpContractValidator(objectMapper, fuzzEngine, createDecomposer(), completePolicy());
        ConcurrentLinkedQueue<CapturingOper> operations = new ConcurrentLinkedQueue<>();

        var tests = contract.apiCall(() -> {
                    CapturingOper operation = new CapturingOper(UUID.randomUUID().toString());
                    operations.add(operation);
                    return operation;
                })
                .payload(() -> new Payload("id-" + payloadCounter.incrementAndGet(), "name", List.of(new Contact("x@y"))))
                .tests()
                .toList();

        var executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> first = executor.submit(() -> executeUnchecked(tests.get(0)));
            Future<?> second = executor.submit(() -> executeUnchecked(tests.get(0)));
            first.get();
            second.get();
        } finally {
            executor.shutdownNow();
        }

        assertEquals(3, payloadCounter.get());
        assertEquals(2, operations.size());
        List<String> bodies = operations.stream().map(operation -> operation.bodyJson).toList();
        assertEquals(2, bodies.size());
        assertEquals(2, bodies.stream().distinct().count());
    }

    private void executeUnchecked(org.junit.jupiter.api.DynamicTest test) {
        try {
            test.getExecutable().execute();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private FuzzCase payloadNameCase(String nameValue) {
        return payloadNameCase(nameValue, "id");
    }

    private FuzzCase payloadNameCase(String nameValue, String idValue) {
        var mutated = objectMapper.createObjectNode()
                .put("id", idValue)
                .put("name", nameValue);
        mutated.putArray("contacts")
                .add(objectMapper.createObjectNode().put("email", "x@y"));
        return new FuzzCase(
                path("/name"),
                new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, nameValue),
                mutated
        );
    }

    private FuzzCase pathAgreementCase(String agreementId) {
        return pathAgreementCase(agreementId, "descriptor");
    }

    private FuzzCase pathAgreementCase(String agreementId, String descriptorId) {
        JsonNode mutated = objectMapper.createObjectNode()
                .put("agreementId", agreementId)
                .put("descriptorId", descriptorId);
        return new FuzzCase(
                path("/agreementId"),
                new FuzzMutation(FuzzScenario.REPLACED_WITH_MALFORMED_UUID, FuzzMutationKind.REPLACE, agreementId),
                mutated
        );
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

    record Payload(String id, String name, List<Contact> contacts) {
    }

    record Contact(String email) {
    }

    record PathParams(String agreementId, String descriptorId) {
    }

    static class SimpleOper implements Oper {
        public SimpleOper reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new RequestSpecBuilder());
            return this;
        }

        @Override
        public <T> T execute(Function<Response, T> handler) {
            return handler.apply(Mockito.mock(Response.class));
        }
    }

    static class CapturingOper implements Oper {
        final String id;
        String bodyJson;
        String pathAgreementId;
        String pathDescriptorId;

        CapturingOper(String id) {
            this.id = id;
        }

        public CapturingOper reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new CapturingBuilder(this));
            return this;
        }

        public CapturingOper agreementIdPath(Object value) {
            this.pathAgreementId = String.valueOf(value);
            return this;
        }

        public CapturingOper descriptorIdPath(Object value) {
            this.pathDescriptorId = String.valueOf(value);
            return this;
        }

        @Override
        public <T> T execute(Function<Response, T> handler) {
            return handler.apply(Mockito.mock(Response.class));
        }
    }

    static class CapturingBuilder extends RequestSpecBuilder {
        private final CapturingOper owner;

        CapturingBuilder(CapturingOper owner) {
            this.owner = owner;
        }

        @Override
        public RequestSpecBuilder setBody(String body) {
            owner.bodyJson = body;
            return this;
        }

        @Override
        public RequestSpecBuilder setBody(Object body) {
            owner.bodyJson = String.valueOf(body);
            return this;
        }
    }
}
