package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import it.pagopa.infrastructure.contract.http.HttpAuthorizationValidatorTest.StubApi.PreparedOper;
import it.pagopa.infrastructure.contract.http.HttpAuthorizationValidatorTest.StubApi.ReadOper;
import it.pagopa.infrastructure.contract.http.HttpAuthorizationValidatorTest.StubApi.WriteOper;
import it.pagopa.infrastructure.fuzzing.FuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.logging.TestMdcKeys;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.infrastructure.reporting.contract.target.ContractCaseTargetRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HttpAuthorizationValidatorTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpContractValidator validator = new HttpContractValidator(
            objectMapper,
            source -> List.of(),
            source -> List.of(),
            Mockito.mock(ObjectGraphDecomposer.class),
            completePolicy()
    );
    private final List<String> events = new CopyOnWriteArrayList<>();
    private final ThreadLocal<String> currentIdentity = new ThreadLocal<>();

    @AfterEach
    void cleanUp() {
        MDC.remove(TestMdcKeys.SCENARIO_NAME);
        currentIdentity.remove();
    }

    @Test
    void generatesFullCartesianProductWithoutAuthenticatingOrInvokingSuppliers() {
        AtomicInteger supplierCalls = new AtomicInteger();
        List<Supplier<?>> operations = IntStream.range(0, 4)
                .<Supplier<?>>mapToObj(index -> () -> {
                    supplierCalls.incrementAndGet();
                    return new ReadOper(403);
                })
                .toList();

        List<DynamicTest> tests = validator.as(identities("alice", "bob", "carol")).apiCalls(operations).tests().toList();

        assertEquals(12, tests.size());
        assertEquals(12, tests.stream().map(DynamicTest::getDisplayName).collect(Collectors.toSet()).size());
        assertEquals(12, tests.stream().map(DynamicTest::getTestSourceUri).collect(Collectors.toSet()).size());
        assertEquals("[authorization] alice / API | api #1 | expected 403", tests.get(0).getDisplayName());
        assertEquals("[authorization] bob / API | api #2 | expected 403", tests.get(5).getDisplayName());
        assertEquals(0, supplierCalls.get());
        assertTrue(events.isEmpty());
    }

    @Test
    void defaultExpectedStatusIsForbiddenAndCanBeOverridden() throws Throwable {
        execute(validator.as(identities("alice")).apiCalls(() -> new ReadOper(403)).tests());
        execute(validator.as(identities("alice")).apiCalls(() -> new ReadOper(404)).expectedStatus(404).tests());

        DynamicTest wrongDefault = validator.as(identities("alice")).apiCalls(() -> new ReadOper(404)).tests().findFirst().orElseThrow();
        assertThrows(AssertionError.class, () -> wrongDefault.getExecutable().execute());
        DynamicTest wrongOverride = validator.as(identities("alice")).apiCalls(() -> new ReadOper(403)).expectedStatus(401).tests().findFirst().orElseThrow();
        assertTrue(wrongOverride.getDisplayName().endsWith("expected 401"));
        assertThrows(AssertionError.class, () -> wrongOverride.getExecutable().execute());
    }

    @Test
    void badRequestInsteadOfForbiddenIsAFailureWithDiagnostics() {
        DynamicTest test = validator.as(identities("alice"))
                .apiCalls(() -> new ReadOper(400))
                .tests().findFirst().orElseThrow();

        AssertionError error = assertThrows(AssertionError.class, () -> test.getExecutable().execute());

        assertTrue(error.getMessage().contains("- identity: alice"), error.getMessage());
        assertTrue(error.getMessage().contains("- api: StubApi.read"), error.getMessage());
        assertTrue(error.getMessage().contains("- request: GET /things/{id}"), error.getMessage());
        assertTrue(error.getMessage().contains("- expectedStatus: 403"), error.getMessage());
        assertTrue(error.getMessage().contains("- responseStatus: 400"), error.getMessage());
        assertTrue(error.getMessage().contains("- responseBody: body-400"), error.getMessage());
    }

    @Test
    void authenticatesBeforeCreatingEachOperationAndInvokesSupplierPerCombination() throws Throwable {
        List<Object> created = new ArrayList<>();
        Supplier<?> supplier = () -> {
            events.add("supplier:" + currentIdentity.get());
            ReadOper operation = new ReadOper(403);
            created.add(operation);
            return operation;
        };

        execute(validator.as(identities("alice", "bob")).apiCalls(supplier, supplier).tests());

        assertEquals(List.of(
                "auth:alice", "supplier:alice", "auth:alice", "supplier:alice",
                "auth:bob", "supplier:bob", "auth:bob", "supplier:bob"
        ), events);
        assertEquals(4, created.size());
        assertEquals(4, Set.copyOf(created.stream().map(System::identityHashCode).toList()).size());
    }

    @Test
    void heterogeneousOperationsAreExecutedAsPreparedWithoutMutationBinding() throws Throwable {
        PreparedOper prepared = new PreparedOper().body("{\"name\":\"\"}").path("42").query("x");
        ReadOper read = new ReadOper(403);

        execute(validator.as(identities("alice")).apiCalls(() -> prepared, () -> read).tests());

        assertEquals("{\"name\":\"\"}", prepared.body);
        assertEquals("42", prepared.path);
        assertEquals("x", prepared.query);
        assertEquals(0, prepared.reqSpecCalls);
        assertEquals(1, prepared.executions);
        assertEquals(1, read.executions);
    }

    @Test
    void eachCaseUsesItsOwnIdentityUnderConcurrentExecution() throws Exception {
        List<String> names = IntStream.range(0, 6).mapToObj(index -> "user-" + index).toList();
        Map<String, String> identityByCase = new ConcurrentHashMap<>();
        List<Supplier<?>> operations = IntStream.range(0, 5)
                .<Supplier<?>>mapToObj(index -> () -> {
                    String identity = currentIdentity.get();
                    sleepBriefly();
                    return new ReadOper(403) {
                        @Override
                        Response respond() {
                            identityByCase.put(MDC.get(TestMdcKeys.SCENARIO_NAME), identity + "|" + currentIdentity.get());
                            return super.respond();
                        }
                    };
                })
                .toList();
        List<DynamicTest> tests = validator.as(identities(names.toArray(String[]::new))).apiCalls(operations).tests().toList();

        ExecutorService pool = Executors.newFixedThreadPool(8);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (DynamicTest test : tests) {
                futures.add(pool.submit(() -> {
                    try {
                        test.getExecutable().execute();
                    } catch (Throwable throwable) {
                        throw new IllegalStateException(throwable);
                    }
                }));
            }
            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            pool.shutdownNow();
        }

        assertEquals(30, identityByCase.size());
        identityByCase.forEach((caseName, identities) -> {
            String expected = caseName.substring("[authorization] ".length(), caseName.indexOf(" / API"));
            assertEquals(expected + "|" + expected, identities, caseName);
        });
    }

    @Test
    void scenarioMdcIsRestoredAfterSuccessAndFailure() {
        MDC.put(TestMdcKeys.SCENARIO_NAME, "parent");
        DynamicTest failing = validator.as(identities("alice")).apiCalls(() -> new ReadOper(400)).tests().findFirst().orElseThrow();
        assertThrows(AssertionError.class, () -> failing.getExecutable().execute());
        assertEquals("parent", MDC.get(TestMdcKeys.SCENARIO_NAME));

        MDC.remove(TestMdcKeys.SCENARIO_NAME);
        DynamicTest erroring = validator.as(identities("alice")).apiCalls(() -> {
            assertEquals(1, 1);
            throw new IllegalStateException("boom");
        }).tests().findFirst().orElseThrow();
        assertThrows(ContractHttpException.class, () -> erroring.getExecutable().execute());
        assertNull(MDC.get(TestMdcKeys.SCENARIO_NAME));
    }

    @Test
    void configurationErrorsHaveExplicitMessages() {
        HttpContractIdentity noAuth = new HttpContractIdentity("x", null);
        assertMessage("identities must not be empty", () -> validator.as(List.<HttpContractIdentity>of()));
        assertMessage("identities must not be null", () -> validator.as((List<HttpContractIdentity>) null));
        assertMessage("identities must not be null", () -> validator.as((Stream<HttpContractIdentity>) null));
        assertMessage("identity #2 must not be null", () -> validator.as(java.util.Arrays.asList(identity("a"), null)));
        assertMessage("identity #1 authentication must not be null", () -> validator.as(List.of(noAuth)));
        assertMessage("operation suppliers must not be empty", () -> validator.as(identities("a")).apiCalls(List.of()));
        assertMessage("operation suppliers must not be null", () -> validator.as(identities("a")).apiCalls((List<Supplier<?>>) null));
        assertMessage("operation supplier #2 must not be null", () -> validator.as(identities("a")).apiCalls(() -> new ReadOper(403), null));
        assertMessage("expected status must be a valid HTTP status code: 99", () -> validator.as(identities("a")).apiCalls(() -> new ReadOper(403)).expectedStatus(99));
    }

    @Test
    void supplierAndAuthenticationFailuresRemainTestErrors() {
        DynamicTest nullOperation = validator.as(identities("alice")).apiCalls(() -> null).tests().findFirst().orElseThrow();
        ContractHttpException nullError = assertThrows(ContractHttpException.class, () -> nullOperation.getExecutable().execute());
        assertTrue(nullError.getMessage().startsWith("operation supplier returned null"), nullError.getMessage());

        IllegalStateException cause = new IllegalStateException("no supplier");
        DynamicTest failingSupplier = validator.as(identities("alice")).apiCalls(() -> {
            throw cause;
        }).tests().findFirst().orElseThrow();
        ContractHttpException supplierError = assertThrows(ContractHttpException.class, () -> failingSupplier.getExecutable().execute());
        assertSame(cause, supplierError.getCause());

        AtomicInteger supplierCalls = new AtomicInteger();
        IllegalStateException authCause = new IllegalStateException("login failed");
        DynamicTest failingAuth = validator.as(List.of(new HttpContractIdentity("alice", () -> {
            throw authCause;
        }))).apiCalls(() -> {
            supplierCalls.incrementAndGet();
            return new ReadOper(403);
        }).tests().findFirst().orElseThrow();
        ContractHttpException authError = assertThrows(ContractHttpException.class, () -> failingAuth.getExecutable().execute());
        assertSame(authCause, authError.getCause());
        assertEquals(0, supplierCalls.get());

        DynamicTest transport = validator.as(identities("alice")).apiCalls(() -> new ReadOper(403) {
            @Override
            Response respond() {
                throw new IllegalStateException("connection refused");
            }
        }).tests().findFirst().orElseThrow();
        assertThrows(ContractHttpException.class, () -> transport.getExecutable().execute());
    }

    @Test
    void reportTargetIsRegisteredPerCaseBeforeTheRequestIsSent() throws Throwable {
        List<DynamicTest> tests = validator.as(identities("alice"))
                .apiCalls(() -> new ReadOper(403), () -> new WriteOper(403), () -> new ReadOper(403) {
                    @Override
                    Response respond() {
                        throw new IllegalStateException("connection refused");
                    }
                })
                .tests().toList();

        tests.get(0).getExecutable().execute();
        tests.get(1).getExecutable().execute();
        assertThrows(ContractHttpException.class, () -> tests.get(2).getExecutable().execute());

        assertEquals("GET /things/{id}", targetOf(tests.get(0)));
        assertEquals("POST /things", targetOf(tests.get(1)));
        assertEquals("GET /things/{id}", targetOf(tests.get(2)));
    }

    @Test
    void repeatedIdentityLabelsAreDisambiguatedAndConfigurationIsSnapshotted() {
        List<HttpContractIdentity> identities = new ArrayList<>(identities("same", "same"));
        List<Supplier<?>> operations = new ArrayList<>(List.of(() -> new ReadOper(403)));
        var stage = validator.as(identities).apiCalls(operations);
        identities.add(identity("late"));
        operations.add(() -> new ReadOper(403));

        List<String> names = stage.tests().map(DynamicTest::getDisplayName).toList();

        assertEquals(List.of(
                "[authorization] same / API | identity #1 | api #1 | expected 403",
                "[authorization] same / API | identity #2 | api #1 | expected 403"
        ), names);
    }

    @Test
    void streamOfIdentitiesIsMaterializedOnce() {
        AtomicInteger materializations = new AtomicInteger();
        Stream<HttpContractIdentity> stream = Stream.of("a", "b").peek(name -> materializations.incrementAndGet()).map(this::identity);

        var stage = validator.as(stream).apiCalls(() -> new ReadOper(403));
        stage.tests().toList();
        stage.tests().toList();

        assertEquals(2, materializations.get());
    }

    @Test
    void existingFuzzingEntryPointsAndConstructorsAreUnchanged() throws Exception {
        assertNotNull(HttpContractValidator.class.getMethod("apiCall", Supplier.class));
        assertNotNull(HttpContractValidator.class.getMethod("apiCall", HttpContractAuthentication.class, Supplier.class));
        assertNotNull(HttpContractValidator.class.getConstructor(
                ObjectMapper.class, FuzzEngine.class, FuzzEngine.class, ObjectGraphDecomposer.class, HttpContractPolicy.class));
        assertNotNull(HttpContractValidator.class.getConstructor(
                ObjectMapper.class, FuzzEngine.class, FuzzEngine.class, FuzzEngine.class, ObjectGraphDecomposer.class, HttpContractPolicy.class));
        assertFalse(validator.apiCall(() -> new ReadOper(200)).payload(() -> "x") == null);
    }

    private static HttpContractPolicy completePolicy() {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder().success(response -> {
        });
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenario(scenario, response -> {
            });
        }
        return builder.build();
    }

    private String targetOf(DynamicTest test) {
        String uri = test.getTestSourceUri().orElseThrow().toString();
        return ContractCaseTargetRegistry.find(ContractCaseTargetRegistry.caseId(uri).orElseThrow()).orElseThrow();
    }

    private void execute(Stream<DynamicTest> tests) throws Throwable {
        for (DynamicTest test : tests.toList()) {
            test.getExecutable().execute();
        }
    }

    private void assertMessage(String expected, org.junit.jupiter.api.function.Executable executable) {
        ContractHttpException exception = assertThrows(ContractHttpException.class, executable);
        assertEquals(expected, exception.getMessage());
    }

    private List<HttpContractIdentity> identities(String... names) {
        return java.util.Arrays.stream(names).map(this::identity).toList();
    }

    private HttpContractIdentity identity(String name) {
        return new HttpContractIdentity(name, () -> {
            currentIdentity.set(name);
            events.add("auth:" + name);
        });
    }

    private static void sleepBriefly() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    static class StubApi {
        static class ReadOper {
            public static final String REQ_METHOD = "GET";
            public static final String REQ_URI = "/things/{id}";
            private final int status;
            int executions;

            ReadOper(int status) {
                this.status = status;
            }

            Response respond() {
                Response response = Mockito.mock(Response.class);
                Mockito.when(response.getStatusCode()).thenReturn(status);
                Mockito.when(response.asString()).thenReturn("body-" + status);
                return response;
            }

            public <T> T execute(Function<Response, T> handler) {
                executions++;
                return handler.apply(respond());
            }
        }

        static class WriteOper extends ReadOper {
            public static final String REQ_METHOD = "POST";
            public static final String REQ_URI = "/things";

            WriteOper(int status) {
                super(status);
            }
        }

        static class PreparedOper extends ReadOper {
            String body;
            String path;
            String query;
            int reqSpecCalls;

            PreparedOper() {
                super(403);
            }

            PreparedOper body(String value) {
                this.body = value;
                return this;
            }

            PreparedOper path(String value) {
                this.path = value;
                return this;
            }

            PreparedOper query(String value) {
                this.query = value;
                return this;
            }

            public PreparedOper reqSpec(Consumer<RequestSpecBuilder> customizer) {
                reqSpecCalls++;
                return this;
            }
        }
    }
}
