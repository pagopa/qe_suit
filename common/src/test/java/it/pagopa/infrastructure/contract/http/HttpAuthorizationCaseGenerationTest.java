package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Method;
import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.logging.TestMdcKeys;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpAuthorizationCaseGenerationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ------------------------------------------------------------------ prodotto cartesiano

    @Test
    void threeIdentitiesAndFourApiCallsProduceTwelveIndependentTests() {
        HttpContractValidator contract = validator();
        List<HttpContractIdentity> identities = identities("USER_1", "IMPRESA_1", "COMUNE_1");

        List<DynamicTest> tests = contract
                .authorization(identities)
                .apiCalls(StatusOper::forbidden, StatusOper::forbidden, StatusOper::forbidden, StatusOper::forbidden)
                .tests()
                .toList();

        assertEquals(12, tests.size());

        Set<String> names = tests.stream().map(DynamicTest::getDisplayName).collect(Collectors.toSet());
        assertEquals(12, names.size(), "ogni combinazione deve avere un nome distinguibile");
        assertTrue(names.contains("[USER_1] api#1"));
        assertTrue(names.contains("[COMUNE_1] api#4"));
    }

    // ------------------------------------------------------------------ status atteso

    @Test
    void defaultExpectedStatusIsForbidden() throws Throwable {
        DynamicTest passing = singleTest(StatusOper::forbidden, null);
        passing.getExecutable().execute();

        DynamicTest failing = singleTest(() -> new StatusOper(200), null);
        AssertionError error = assertThrows(AssertionError.class, () -> failing.getExecutable().execute());
        assertTrue(error.getMessage().contains("expectedStatus: 403"));
        assertTrue(error.getMessage().contains("actualStatus: 200"));
    }

    @Test
    void expectedStatusOverrideReplacesTheDefault() throws Throwable {
        DynamicTest passing = singleTest(() -> new StatusOper(401), 401);
        passing.getExecutable().execute();

        DynamicTest failing = singleTest(StatusOper::forbidden, 401);
        AssertionError error = assertThrows(AssertionError.class, () -> failing.getExecutable().execute());
        assertTrue(error.getMessage().contains("expectedStatus: 401"));
        assertTrue(error.getMessage().contains("actualStatus: 403"));
    }

    /**
     * Il controllo dei permessi precede la validazione del payload: un 400 non e' un esito
     * alternativo accettabile quando ci si aspetta un 403.
     */
    @Test
    void badRequestIsAFailureWhenForbiddenIsExpected() {
        DynamicTest test = singleTest(() -> new StatusOper(400), null);

        AssertionError error = assertThrows(AssertionError.class, () -> test.getExecutable().execute());
        assertTrue(error.getMessage().contains("expectedStatus: 403"));
        assertTrue(error.getMessage().contains("actualStatus: 400"));
    }

    // ------------------------------------------------------------------ supplier e operazioni

    @Test
    void noRequestIsIssuedWhileGeneratingTests() {
        AtomicInteger authentications = new AtomicInteger();
        AtomicInteger supplierCalls = new AtomicInteger();
        AtomicInteger executions = new AtomicInteger();

        HttpContractIdentity identity = new HttpContractIdentity(
                "USER_1",
                authentications::incrementAndGet
        );
        Supplier<?> supplier = () -> {
            supplierCalls.incrementAndGet();
            return new StatusOper(403, executions);
        };

        List<DynamicTest> tests = validator()
                .authorization(List.of(identity))
                .apiCalls(supplier)
                .tests()
                .toList();

        assertEquals(0, authentications.get());
        assertEquals(0, supplierCalls.get());
        assertEquals(0, executions.get());

        assertEquals(1, tests.size());
    }

    @Test
    void everyCombinationGetsAFreshOperationFromTheSupplier() throws Throwable {
        List<Object> operations = Collections.synchronizedList(new ArrayList<>());
        Supplier<?> supplier = () -> {
            StatusOper operation = new StatusOper(403);
            operations.add(operation);
            return operation;
        };

        List<DynamicTest> tests = validator()
                .authorization(identities("USER_1", "IMPRESA_1", "COMUNE_1"))
                .apiCalls(supplier)
                .tests()
                .toList();

        for (DynamicTest test : tests) {
            test.getExecutable().execute();
        }

        assertEquals(3, operations.size(), "un'operazione nuova per ogni combinazione");
        assertNotSame(operations.get(0), operations.get(1));
        assertNotSame(operations.get(1), operations.get(2));
    }

    @Test
    void heterogeneousOperationsAreAllExecutedWithoutBinding() throws Throwable {
        List<DynamicTest> tests = validator()
                .authorization(identities("USER_1"))
                .apiCalls(
                        StatusOper::forbidden,
                        BodyOper::new,
                        PathParamOper::new
                )
                .tests()
                .toList();

        assertEquals(3, tests.size());
        for (DynamicTest test : tests) {
            test.getExecutable().execute();
        }
    }

    // ------------------------------------------------------------------ isolamento in parallelo

    @Test
    void twoIdentitiesRunningInParallelDoNotSeeEachOther() throws Exception {
        ThreadLocal<String> session = new ThreadLocal<>();
        CountDownLatch bothAuthenticated = new CountDownLatch(2);
        List<String> observed = Collections.synchronizedList(new ArrayList<>());

        List<HttpContractIdentity> identities = List.of(
                parallelIdentity("USER_1", session, bothAuthenticated),
                parallelIdentity("COMUNE_1", session, bothAuthenticated)
        );

        // Il supplier legge l'identita' dal ThreadLocal solo dopo che entrambi i thread hanno
        // autenticato: se il contesto non fosse isolato per thread, uno dei due leggerebbe
        // l'identita' dell'altro.
        Supplier<?> supplier = () -> {
            observed.add(session.get());
            return new StatusOper(403);
        };

        List<DynamicTest> tests = validator()
                .authorization(identities)
                .apiCalls(supplier)
                .tests()
                .toList();

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<? extends Future<?>> futures = tests.stream()
                    .map(test -> pool.submit(() -> {
                        try {
                            test.getExecutable().execute();
                        } catch (Throwable throwable) {
                            throw new AssertionError(throwable);
                        }
                    }))
                    .toList();
            for (Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }

        assertEquals(Set.of("USER_1", "COMUNE_1"), Set.copyOf(observed));
    }

    // ------------------------------------------------------------------ errori e contesto

    @Test
    void supplierFailureIsAConfigurationErrorNotAnHttpOutcome() {
        DynamicTest test = singleTest(
                () -> {
                    throw new IllegalStateException("boom");
                },
                null
        );

        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> test.getExecutable().execute()
        );
        assertTrue(exception.getMessage().contains("USER_1"));
        assertTrue(exception.getMessage().contains("api#1"));
    }

    @Test
    void supplierReturningNullIsAConfigurationError() {
        DynamicTest test = singleTest(() -> null, null);

        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> test.getExecutable().execute()
        );
        assertTrue(exception.getMessage().contains("returned null"));
    }

    @Test
    void authenticationFailureMentionsTheIdentity() {
        HttpContractIdentity identity = new HttpContractIdentity("USER_1", () -> {
            throw new IllegalStateException("no token configured");
        });

        DynamicTest test = validator()
                .authorization(List.of(identity))
                .apiCalls(StatusOper::forbidden)
                .tests()
                .toList()
                .get(0);

        ContractHttpException exception = assertThrows(
                ContractHttpException.class,
                () -> test.getExecutable().execute()
        );
        assertTrue(exception.getMessage().contains("authentication failed"));
        assertTrue(exception.getMessage().contains("USER_1"));
    }

    @Test
    void mdcIsRestoredAfterSuccessAndAfterFailure() throws Throwable {
        MDC.put(TestMdcKeys.SCENARIO_NAME, "parent-factory");
        try {
            singleTest(StatusOper::forbidden, null).getExecutable().execute();
            assertEquals("parent-factory", MDC.get(TestMdcKeys.SCENARIO_NAME));

            DynamicTest failing = singleTest(() -> new StatusOper(200), null);
            assertThrows(AssertionError.class, () -> failing.getExecutable().execute());
            assertEquals("parent-factory", MDC.get(TestMdcKeys.SCENARIO_NAME));
        } finally {
            MDC.remove(TestMdcKeys.SCENARIO_NAME);
        }
    }

    @Test
    void mdcIsClearedWhenThereWasNoPreviousScenario() throws Throwable {
        MDC.remove(TestMdcKeys.SCENARIO_NAME);

        singleTest(StatusOper::forbidden, null).getExecutable().execute();

        assertNull(MDC.get(TestMdcKeys.SCENARIO_NAME));
    }

    // ------------------------------------------------------------------ diagnostica

    @Test
    void failureReportsMethodAndUriAndLeaksNoCredentials() {
        DynamicTest test = singleTest(() -> new StatusOper(200), null);

        AssertionError error = assertThrows(AssertionError.class, () -> test.getExecutable().execute());
        String message = error.getMessage();

        assertTrue(message.contains("identity: USER_1"));
        assertTrue(message.contains("apiCall: api#1"));
        assertTrue(message.contains("GET /v1/status"), "deve riportare metodo e URI dell'operazione");
        // Nessuna credenziale: la diagnostica non stampa gli header di richiesta, dove vive il
        // bearer token costruito dal reqSpecSupplier.
        assertFalse(message.contains("Bearer"), "la diagnostica non deve contenere token");
        assertFalse(message.contains("Authorization:"), "non deve stampare header di richiesta");
    }

    // ------------------------------------------------------------------ convivenza col fuzzing

    @Test
    void authorizationModeDoesNotTouchTheFuzzingPipeline() throws Throwable {
        FuzzEngine explodingEngine = source -> {
            throw new AssertionError("la modalita' autorizzazione non deve usare il FuzzEngine");
        };
        ObjectGraphDecomposer explodingDecomposer = source -> {
            throw new AssertionError("la modalita' autorizzazione non deve decomporre oggetti");
        };
        HttpContractValidator contract = new HttpContractValidator(
                objectMapper,
                explodingEngine,
                explodingEngine,
                explodingDecomposer,
                completePolicy()
        );

        List<DynamicTest> tests = contract
                .authorization(identities("USER_1"))
                .apiCalls(StatusOper::forbidden)
                .tests()
                .toList();

        assertEquals(1, tests.size());
        tests.get(0).getExecutable().execute();
    }

    @Test
    void fuzzingModeStillWorksOnTheSameValidator() {
        HttpContractValidator contract = validator();

        assertEquals(
                0,
                contract.apiCall(StatusOper::forbidden).tests().count(),
                "senza scope dichiarati il percorso fuzzing non produce casi, come prima"
        );

        assertEquals(
                2,
                contract.authorization(identities("USER_1", "COMUNE_1"))
                        .apiCalls(StatusOper::forbidden)
                        .tests()
                        .count()
        );
    }

    // ------------------------------------------------------------------ helper

    private HttpContractValidator validator() {
        return new HttpContractValidator(
                objectMapper,
                source -> List.of(),
                source -> List.of(),
                createDecomposer(),
                completePolicy()
        );
    }

    private DynamicTest singleTest(Supplier<?> supplier, Integer expectedStatus) {
        HttpContractStages.AuthorizationTestsStage stage = validator()
                .authorization(identities("USER_1"))
                .apiCalls(supplier);
        if (expectedStatus != null) {
            stage = stage.expectedStatus(expectedStatus);
        }
        return stage.tests().toList().get(0);
    }

    private static List<HttpContractIdentity> identities(String... names) {
        return java.util.Arrays.stream(names)
                .map(name -> new HttpContractIdentity(name, () -> {
                }))
                .collect(Collectors.toList());
    }

    private static HttpContractIdentity parallelIdentity(
            String name,
            ThreadLocal<String> session,
            CountDownLatch bothAuthenticated
    ) {
        return new HttpContractIdentity(name, () -> {
            session.set(name);
            bothAuthenticated.countDown();
            try {
                if (!bothAuthenticated.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("timeout attendendo l'altra utenza");
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(exception);
            }
        });
    }

    private HttpContractPolicy completePolicy() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().success(response -> {
        });
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenario(scenario, response -> {
            });
        }
        return builder.build();
    }

    private ObjectGraphDecomposer createDecomposer() {
        try {
            Class<?> jackson = Class.forName("it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer");
            Constructor<?> jacksonCtor = jackson.getDeclaredConstructor(ObjectMapper.class);
            jacksonCtor.setAccessible(true);
            Object objectDecomposer = jacksonCtor.newInstance(objectMapper);
            Class<?> defaultCls = Class.forName("it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer");
            Constructor<?> defaultCtor = defaultCls.getDeclaredConstructor(
                    Class.forName("it.pagopa.infrastructure.objectgraph.ObjectDecomposer")
            );
            defaultCtor.setAccessible(true);
            return (ObjectGraphDecomposer) defaultCtor.newInstance(objectDecomposer);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    // ------------------------------------------------------------------ fixture di operazioni

    /**
     * Riproduce la forma delle classi {@code *Oper} generate: {@code reqSpec(Consumer)},
     * {@code execute(Function)} e le costanti {@code REQ_METHOD}/{@code REQ_URI}.
     */
    static class StatusOper {
        public static final Method REQ_METHOD = Method.GET;
        public static final String REQ_URI = "/v1/status";

        private final int status;
        private final AtomicInteger executions;

        StatusOper(int status) {
            this(status, new AtomicInteger());
        }

        StatusOper(int status, AtomicInteger executions) {
            this.status = status;
            this.executions = executions;
        }

        static StatusOper forbidden() {
            return new StatusOper(403);
        }

        public StatusOper reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new RequestSpecBuilder());
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            executions.incrementAndGet();
            Response response = Mockito.mock(Response.class);
            Mockito.when(response.getStatusCode()).thenReturn(status);
            Mockito.when(response.asString()).thenReturn("{\"status\":" + status + "}");
            return handler.apply(response);
        }
    }

    /** Operazione con body gia' preparato dal chiamante: non deve essere toccato. */
    static class BodyOper {
        public static final Method REQ_METHOD = Method.POST;
        public static final String REQ_URI = "/v1/resources";

        private Object body;

        public BodyOper body(Object body) {
            this.body = body;
            return this;
        }

        public BodyOper reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new RequestSpecBuilder());
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            Response response = Mockito.mock(Response.class);
            Mockito.when(response.getStatusCode()).thenReturn(403);
            return handler.apply(response);
        }
    }

    /** Operazione con path param gia' valorizzato dal chiamante. */
    static class PathParamOper {
        public static final Method REQ_METHOD = Method.GET;
        public static final String REQ_URI = "/v1/resources/{id}";

        private Object id;

        public PathParamOper idPath(Object id) {
            this.id = id;
            return this;
        }

        public PathParamOper reqSpec(Consumer<RequestSpecBuilder> customizer) {
            customizer.accept(new RequestSpecBuilder());
            return this;
        }

        public <T> T execute(Function<Response, T> handler) {
            Response response = Mockito.mock(Response.class);
            Mockito.when(response.getStatusCode()).thenReturn(403);
            return handler.apply(response);
        }
    }
}
