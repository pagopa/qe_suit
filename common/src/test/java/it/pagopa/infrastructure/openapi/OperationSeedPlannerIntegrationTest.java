package it.pagopa.infrastructure.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzCasePlanner;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.fuzzing.NullAndMissingRule;
import it.pagopa.infrastructure.fuzzing.ScalarRule;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.openapi.seed.DeterministicSeedFactory;
import it.pagopa.infrastructure.openapi.seed.OperationSeedFactory;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationSeedPlannerIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OpenApiOperationDiscovery discovery = new OpenApiOperationDiscovery();
    private final OperationSeedFactory seedFactory = new OperationSeedFactory(new DeterministicSeedFactory());
    private final FuzzCasePlanner payloadPlanner = new FuzzCasePlanner(List.of(new NullAndMissingRule(), new ScalarRule()));
    private final FuzzCasePlanner pathParameterPlanner = new FuzzCasePlanner(List.of(new ScalarRule()));
    private final DefaultObjectGraphDecomposer decomposer = new DefaultObjectGraphDecomposer(
            new JacksonObjectDecomposer(objectMapper)
    );

    @Test
    void generated_body_seed_flows_through_shared_object_graph_and_fuzz_planner() {
        DiscoveredOperation operation = discovery.discover(configuration(), Set.of("createWidget")).get(0);
        Object bodySeed = seedFactory.create(operation).requestBody().orElseThrow();
        Set<String> cases = payloadPlanner.plan(decomposer.decompose(bodySeed)).stream()
                .map(testCase -> testCase.target() + "#" + testCase.mutation().scenario())
                .collect(Collectors.toSet());

        assertTrue(cases.contains("/name#" + FuzzScenario.REPLACED_WITH_EMPTY_STRING));
        assertTrue(cases.contains("/id#" + FuzzScenario.REPLACED_WITH_MALFORMED_UUID));
        assertTrue(cases.contains("/kind#" + FuzzScenario.REPLACED_WITH_UNKNOWN_ENUM));
        assertTrue(cases.contains("/count#" + FuzzScenario.REPLACED_WITH_ZERO));
        assertTrue(cases.contains("/detail/revision#" + FuzzScenario.REPLACED_WITH_ZERO));
        assertTrue(cases.contains("/labels/0#" + FuzzScenario.REPLACED_WITH_EMPTY_STRING));
        assertTrue(cases.contains("/children/0/name#" + FuzzScenario.REPLACED_WITH_EMPTY_STRING));
    }

    @Test
    void typed_path_seed_flows_through_the_same_runtime_planner() {
        DiscoveredOperation operation = discovery.discover(configuration(), Set.of("getWidgetBySlug")).get(0);
        Object pathSeed = seedFactory.create(operation).pathParameters();
        Set<String> cases = pathParameterPlanner.plan(decomposer.decompose(pathSeed)).stream()
                .map(testCase -> testCase.target() + "#" + testCase.mutation().scenario())
                .collect(Collectors.toSet());

        assertTrue(cases.contains("/slug#" + FuzzScenario.REPLACED_WITH_EMPTY_STRING));
        assertTrue(cases.contains("/slug#" + FuzzScenario.REPLACED_WITH_XSS));

        DiscoveredOperation uuidOperation = discovery.discover(configuration(), Set.of("getWidget")).get(0);
        Set<String> uuidCases = pathParameterPlanner.plan(decomposer.decompose(seedFactory.create(uuidOperation).pathParameters())).stream()
                .map(testCase -> testCase.target() + "#" + testCase.mutation().scenario())
                .collect(Collectors.toSet());
        assertTrue(uuidCases.contains("/widgetId#" + FuzzScenario.REPLACED_WITH_MALFORMED_UUID));
        assertTrue(uuidCases.contains("/revision#" + FuzzScenario.REPLACED_WITH_ZERO));
    }

    private GeneratedApiConfiguration configuration() {
        URL spec = getClass().getResource("/openapi/operation-discovery.yaml");
        if (spec == null) throw new IllegalStateException("Missing operation discovery fixture");
        return new GeneratedApiConfiguration(
                "fixture-codegen",
                Path.of(spec.getPath()).toString(),
                DiscoveryFixtureApi.class.getPackageName(),
                WidgetKind.class.getPackageName()
        );
    }
}
