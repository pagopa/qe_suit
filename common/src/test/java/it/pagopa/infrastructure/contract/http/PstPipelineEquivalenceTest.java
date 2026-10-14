package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import it.pagopa.infrastructure.fuzzing.DefaultFuzzEngine;
import it.pagopa.infrastructure.fuzzing.FuzzCase;
import it.pagopa.infrastructure.fuzzing.FuzzCasePlanner;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.fuzzing.JacksonFuzzMutationApplier;
import it.pagopa.infrastructure.fuzzing.NullAndMissingRule;
import it.pagopa.infrastructure.fuzzing.ScalarRule;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.infrastructure.openapi.GeneratedApiConfiguration;
import it.pagopa.infrastructure.openapi.OpenApiOperationDiscovery;
import it.pagopa.infrastructure.openapi.seed.DeterministicSeedFactory;
import it.pagopa.infrastructure.openapi.seed.OperationSeed;
import it.pagopa.infrastructure.openapi.seed.OperationSeedFactory;
import it.pagopa.infrastructure.pst.PstConfig;
import it.pagopa.infrastructure.pst.PstGenerator;
import it.pagopa.infrastructure.pst.model.PstScenario;
import it.pagopa.infrastructure.pst.fixture.PstFixtureApi;
import it.pagopa.infrastructure.pst.fixture.PstFixturePayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PstPipelineEquivalenceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectGraphDecomposer graphDecomposer = new DefaultObjectGraphDecomposer(
            new JacksonObjectDecomposer(objectMapper)
    );
    private final FuzzCasePlanner payloadPlanner = new FuzzCasePlanner(
            List.of(new NullAndMissingRule(), new ScalarRule())
    );
    private final FuzzCasePlanner pathPlanner = new FuzzCasePlanner(List.of(new ScalarRule()));
    private final FuzzCasePlanner queryPlanner = new FuzzCasePlanner(
            List.of(new NullAndMissingRule(), new ScalarRule())
    );
    private static final Map<String, Boolean> QUERY_METADATA = Map.of("filter", true, "page", false);

    @TempDir
    Path temporaryDirectory;

    @Test
    void pstMatchesRuntimePlanningValidityExpectedStatusAndOrigin() throws Exception {
        PstConfig config = loadConfig(false);
        PstGenerator pstGenerator = generator();
        var document = pstGenerator.generate(config);

        OperationSeed seed = seedFactory().create(
                new OpenApiOperationDiscovery()
                        .discover(
                                apiConfiguration(),
                                java.util.Set.of("updateResource")
                        )
                        .get(0)
        );

        Object payload = seed.requestBody().orElseThrow();
        Object pathParams = seed.pathParameters();

        ScopePlanState<?> payloadState = runtimeState(payload, payloadPlanner);
        ScopePlanState<?> pathState = runtimeState(pathParams, pathPlanner);
        ScopePlanState<?> queryState = runtimeState(
                seed.queryParameters(),
                queryPlanner,
                new QueryParameterValidityResolver(QUERY_METADATA)
        );

        HttpContractPolicy policy = runtimePolicy(config);
        ContractCasePlanner runtimePlanner = new ContractCasePlanner(
                objectMapper,
                new MockitoObjectGraphQueryResolver(),
                policy
        );

        List<GeneratedContractCase> runtimeCases = runtimePlanner.planCases(
                payloadState,
                pathState,
                queryState
        );

        List<ScenarioTuple> runtime = runtimeCases.stream()
                .map(testCase ->
                        runtimeTuple(testCase, payloadState, pathState, queryState))
                .toList();

        List<ScenarioTuple> pst = document.operations().get(0).scenarios().stream()
                .map(PstPipelineEquivalenceTest::pstTuple)
                .toList();

        assertEquals(runtime, pst);

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.validity() == ContractValidity.VALID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.validity() == ContractValidity.INVALID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.validity() == ContractValidity.UNKNOWN));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/simple")
                        && tuple.scenario() == FuzzScenario.REPLACED_WITH_EMPTY_STRING
                        && tuple.validity() == ContractValidity.UNKNOWN));

        ScenarioTuple sqlInjection = pst.stream()
                .filter(tuple -> tuple.scope() == RequestScope.PAYLOAD)
                .filter(tuple -> tuple.target().equals("/sized"))
                .filter(tuple ->
                        tuple.scenario() == FuzzScenario.REPLACED_WITH_SQL_INJECTION)
                .findFirst()
                .orElseThrow();

        assertEquals(ContractValidity.UNKNOWN, sqlInjection.validity());
        assertEquals(ExpectationOrigin.POLICY_UNKNOWN, sqlInjection.origin());
        assertEquals(
                config.statusFor(FuzzScenario.REPLACED_WITH_SQL_INJECTION),
                sqlInjection.expectedStatus()
        );

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/sized")
                        && tuple.scenario() == FuzzScenario.REPLACED_WITH_EMPTY_STRING
                        && tuple.validity() == ContractValidity.INVALID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/nullable")
                        && tuple.scenario() == FuzzScenario.REPLACED_WITH_NULL
                        && tuple.validity() == ContractValidity.VALID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/optional")
                        && tuple.scenario() == FuzzScenario.REMOVED
                        && tuple.validity() == ContractValidity.VALID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/required")
                        && tuple.scenario() == FuzzScenario.REMOVED
                        && tuple.validity() == ContractValidity.INVALID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/id")
                        && tuple.scenario() == FuzzScenario.REPLACED_WITH_MALFORMED_UUID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/count")
                        && tuple.scenario() == FuzzScenario.REPLACED_WITH_ZERO));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/kind")
                        && tuple.scenario() == FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/nested/value")));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.target().equals("/items/0/value")));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.scope() == RequestScope.PATH_PARAMS
                        && tuple.validity() == ContractValidity.UNKNOWN));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.scope() == RequestScope.PATH_PARAMS
                        && tuple.target().equals("/id")
                        && tuple.scenario() == FuzzScenario.REPLACED_WITH_MALFORMED_UUID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.scope() == RequestScope.QUERY_PARAMS
                        && tuple.target().equals("/page")
                        && tuple.scenario() == FuzzScenario.REMOVED
                        && tuple.validity() == ContractValidity.VALID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.scope() == RequestScope.QUERY_PARAMS
                        && tuple.target().equals("/filter")
                        && tuple.scenario() == FuzzScenario.REMOVED
                        && tuple.validity() == ContractValidity.INVALID));

        assertTrue(pst.stream().anyMatch(tuple ->
                tuple.scope() == RequestScope.QUERY_PARAMS
                        && tuple.target().equals("/filter")
                        && tuple.scenario() == FuzzScenario.REPLACED_WITH_SQL_INJECTION));
    }

    @Test
    void targetOverrideWinsInBothPipelinesWhilePstStillRecordsValidity() throws Exception {
        PstConfig config = loadConfig(true);
        var document = generator().generate(config);
        OperationSeed seed = seedFactory().create(
                new OpenApiOperationDiscovery().discover(apiConfiguration(), java.util.Set.of("updateResource")).get(0)
        );
        PstFixturePayload payload = (PstFixturePayload) seed.requestBody().orElseThrow();
        ObjectGraph graph = graphDecomposer.decompose(payload);
        List<FuzzCase> fuzzCases = runtimeCases(payload, payloadPlanner);

        ScopeOverrides overrides = new ScopeOverrides();
        TargetExpression<PstFixturePayload> sizedTarget = PstFixturePayload::getSized;
        overrides.addTargets(
                List.of(FuzzScenario.REPLACED_WITH_EMPTY_STRING),
                response -> response.then().statusCode(202),
                List.of(sizedTarget)
        );
        ScopePlanState<PstFixturePayload> payloadState = new ScopePlanState<>(
                payload,
                PstFixturePayload.class,
                graph,
                fuzzCases,
                overrides
        );
        List<GeneratedContractCase> runtimeCases = new ContractCasePlanner(
                objectMapper,
                new MockitoObjectGraphQueryResolver(),
                runtimePolicy(config)
        ).planCases(payloadState, null);
        GeneratedContractCase runtimeTarget = runtimeCases.stream()
                .filter(testCase -> testCase.target().toString().equals("/sized"))
                .filter(testCase -> testCase.mutation().scenario() == FuzzScenario.REPLACED_WITH_EMPTY_STRING)
                .findFirst()
                .orElseThrow();
        PstScenario pstTarget = document.operations().get(0).scenarios().stream()
                .filter(testCase -> testCase.target().toString().equals("/sized"))
                .filter(testCase -> testCase.scenario() == FuzzScenario.REPLACED_WITH_EMPTY_STRING)
                .findFirst()
                .orElseThrow();
        ContractValidity validity = new JacksonMutationValidityResolver(objectMapper, payload.getClass())
                .resolve(graph.find(runtimeTarget.target()).orElseThrow(), runtimeTarget.mutation());

        assertEquals(ContractValidity.INVALID, validity);
        assertEquals(validity, pstTarget.validity());
        assertEquals(ExpectationOrigin.TARGET_OVERRIDE, runtimeTarget.expectationOrigin());
        assertEquals(ExpectationOrigin.TARGET_OVERRIDE, pstTarget.expectationOrigin());
        assertEquals(202, expectedStatus(runtimeTarget.expectation()));
        assertEquals(202, pstTarget.expectedStatus());
    }

    private PstGenerator generator() {
        return new PstGenerator(
                new OpenApiOperationDiscovery(),
                apiConfiguration(),
                seedFactory(),
                graphDecomposer,
                objectMapper,
                payloadPlanner,
                pathPlanner,
                queryPlanner
        );
    }

    private OperationSeedFactory seedFactory() {
        return new OperationSeedFactory(new DeterministicSeedFactory());
    }

    private GeneratedApiConfiguration apiConfiguration() {
        try {
            URL spec = getClass().getResource("/openapi/pst-equivalence.yaml");
            if (spec == null) throw new IllegalStateException("Missing PST equivalence OpenAPI fixture");
            return new GeneratedApiConfiguration(
                    "fixture-codegen",
                    Path.of(spec.toURI()).toString(),
                    PstFixtureApi.class.getPackageName(),
                    PstFixturePayload.class.getPackageName()
            );
        } catch (java.net.URISyntaxException exception) {
            throw new IllegalStateException("Invalid PST OpenAPI fixture URL", exception);
        }
    }

    private ScopePlanState<?> runtimeState(Object source, FuzzCasePlanner planner) {
        return runtimeState(source, planner, null);
    }

    private ScopePlanState<?> runtimeState(
            Object source,
            FuzzCasePlanner planner,
            MutationValidityResolver validityResolver
    ) {
        ObjectGraph graph = graphDecomposer.decompose(source);
        List<FuzzCase> fuzzCases = runtimeCases(source, planner);
        return new ScopePlanState(
                source,
                source.getClass(),
                graph,
                fuzzCases,
                new ScopeOverrides(),
                validityResolver
        );
    }

    private List<FuzzCase> runtimeCases(Object source, FuzzCasePlanner planner) {
        return new DefaultFuzzEngine(
                graphDecomposer,
                objectMapper,
                new JacksonFuzzMutationApplier(objectMapper),
                planner
        ).generate(source);
    }

    private HttpContractPolicy runtimePolicy(PstConfig config) {
        HttpContractPolicy.Builder builder = HttpContractPolicy.builder()
                .successStatus(config.successStatus());
        for (FuzzScenario scenario : FuzzScenario.values()) {
            builder.scenarioStatus(scenario, config.statusFor(scenario));
        }
        return builder.build();
    }

    private ScenarioTuple runtimeTuple(
            GeneratedContractCase testCase,
            ScopePlanState<?> payloadState,
            ScopePlanState<?> pathState,
            ScopePlanState<?> queryState
    ) {
        ScopePlanState<?> state = switch (testCase.scope()) {
            case PAYLOAD -> payloadState;
            case PATH_PARAMS -> pathState;
            case QUERY_PARAMS -> queryState;
        };
        Node node = state.graph().find(testCase.target()).orElseThrow();
        MutationValidityResolver resolver = state.validityResolver() != null
                ? state.validityResolver()
                : new JacksonMutationValidityResolver(objectMapper, state.sourceType());
        ContractValidity validity = resolver.resolve(node, testCase.mutation());
        return new ScenarioTuple(
                testCase.scope(),
                testCase.target().toString(),
                testCase.mutation().scenario(),
                validity,
                expectedStatus(testCase.expectation()),
                testCase.expectationOrigin()
        );
    }

    private int expectedStatus(Consumer<Response> expectation) {
        Response response = mock(Response.class);
        ValidatableResponse validatable = mock(ValidatableResponse.class);
        when(response.then()).thenReturn(validatable);
        when(validatable.statusCode(anyInt())).thenReturn(validatable);
        expectation.accept(response);
        ArgumentCaptor<Integer> status = ArgumentCaptor.forClass(Integer.class);
        verify(validatable).statusCode(status.capture());
        return status.getValue();
    }

    private static ScenarioTuple pstTuple(PstScenario scenario) {
        return new ScenarioTuple(
                scenario.scope(),
                scenario.target().toString(),
                scenario.scenario(),
                scenario.validity(),
                scenario.expectedStatus(),
                scenario.expectationOrigin()
        );
    }

    private PstConfig loadConfig(boolean withOverride) throws Exception {
        StringBuilder yaml = new StringBuilder("successStatus: 201\nscenarioStatus:\n");
        for (FuzzScenario scenario : FuzzScenario.values()) {
            yaml.append("  ").append(scenario).append(": ").append(400 + scenario.ordinal()).append("\n");
        }
        if (withOverride) {
            yaml.append("overrides:\n")
                    .append("  - operationId: updateResource\n")
                    .append("    scope: PAYLOAD\n")
                    .append("    target: /sized\n")
                    .append("    scenario: REPLACED_WITH_EMPTY_STRING\n")
                    .append("    status: 202\n");
        }
        Path file = temporaryDirectory.resolve("pst-" + withOverride + ".yaml");
        Files.writeString(file, yaml);
        return PstConfig.load(file);
    }

    private record ScenarioTuple(
            RequestScope scope,
            String target,
            FuzzScenario scenario,
            ContractValidity validity,
            int expectedStatus,
            ExpectationOrigin origin
    ) {
    }
}
