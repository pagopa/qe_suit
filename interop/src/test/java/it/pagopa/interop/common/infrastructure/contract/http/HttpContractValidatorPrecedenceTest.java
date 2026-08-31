package it.pagopa.interop.common.infrastructure.contract.http;

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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpContractValidatorPrecedenceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void targetOverrideTakesPrecedenceOverScenarioOverride() throws Throwable {
        FuzzEngine fuzzEngine = source -> List.of(
                fuzzCase("/legacyField", ""),
                fuzzCase("/otherField", "")
        );
        AtomicInteger targetOverrideCount = new AtomicInteger();
        AtomicInteger scenarioOverrideCount = new AtomicInteger();
        AtomicInteger policyCount = new AtomicInteger();

        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().success(response -> policyCount.incrementAndGet());
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenario(scenario, response -> policyCount.incrementAndGet());
        }

        HttpContractValidator contract = new HttpContractValidator(
                objectMapper,
                fuzzEngine,
                createDecomposer(),
                builder.build()
        );

        var tests = contract.apiCall(EmptyOper::new)
                .payload(new Payload("legacy", "other"))
                .scenario(FuzzScenario.REPLACED_WITH_EMPTY_STRING, response -> scenarioOverrideCount.incrementAndGet())
                .targets(FuzzScenario.REPLACED_WITH_EMPTY_STRING, response -> targetOverrideCount.incrementAndGet(), List.of(Payload::getLegacyField))
                .tests()
                .toList();

        tests.get(0).getExecutable().execute();
        tests.get(1).getExecutable().execute();

        assertEquals(1, targetOverrideCount.get());
        assertEquals(1, scenarioOverrideCount.get());
        assertEquals(0, policyCount.get());
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

    private FuzzCase fuzzCase(String pointer, String value) {
        return new FuzzCase(
                path(pointer),
                new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, value),
                objectMapper.createObjectNode()
                        .put("legacyField", value)
                        .put("otherField", value)
        );
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

    static class Payload {
        private final String legacyField;
        private final String otherField;

        Payload(String legacyField, String otherField) {
            this.legacyField = legacyField;
            this.otherField = otherField;
        }

        public String getLegacyField() {
            return legacyField;
        }

        public String getOtherField() {
            return otherField;
        }
    }

    static class EmptyOper implements Oper {
        public EmptyOper reqSpec(java.util.function.Consumer<io.restassured.builder.RequestSpecBuilder> customizer) {
            customizer.accept(new io.restassured.builder.RequestSpecBuilder());
            return this;
        }

        @Override
        public <T> T execute(Function<io.restassured.response.Response, T> handler) {
            return handler.apply(org.mockito.Mockito.mock(io.restassured.response.Response.class));
        }
    }
}
