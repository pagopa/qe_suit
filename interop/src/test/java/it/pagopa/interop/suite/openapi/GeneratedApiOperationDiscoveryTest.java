package it.pagopa.interop.suite.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.fuzzing.FuzzCasePlanner;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.fuzzing.NullAndMissingRule;
import it.pagopa.infrastructure.fuzzing.ScalarRule;
import it.pagopa.infrastructure.objectgraph.DefaultObjectGraphDecomposer;
import it.pagopa.infrastructure.objectgraph.JacksonObjectDecomposer;
import it.pagopa.infrastructure.openapi.DiscoveredOperation;
import it.pagopa.infrastructure.openapi.GeneratedApiConfiguration;
import it.pagopa.infrastructure.openapi.OpenApiOperationDiscovery;
import it.pagopa.infrastructure.openapi.seed.DeterministicSeedFactory;
import it.pagopa.infrastructure.openapi.seed.OperationSeedFactory;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementPayload;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratedApiOperationDiscoveryTest {

    @Test
    void resolves_actual_generated_body_type_and_uses_runtime_fuzz_planner() throws Exception {
        OpenApiOperationDiscovery discovery = new OpenApiOperationDiscovery();
        GeneratedApiConfiguration configuration = configuration();
        DiscoveredOperation create = discovery.discover(configuration, Set.of("createAgreement")).get(0);
        assertEquals(AgreementPayload.class, create.requestBodyType().orElseThrow());

        var seeds = new OperationSeedFactory(new DeterministicSeedFactory());
        AgreementPayload body = (AgreementPayload) seeds.create(create).requestBody().orElseThrow();
        ObjectMapper mapper = new ObjectMapper();
        var decomposer = new DefaultObjectGraphDecomposer(new JacksonObjectDecomposer(mapper));
        var payloadPlanner = new FuzzCasePlanner(List.of(new NullAndMissingRule(), new ScalarRule()));
        var pathParameterPlanner = new FuzzCasePlanner(List.of(new ScalarRule()));
        Set<String> bodyCases = payloadPlanner.plan(decomposer.decompose(body)).stream()
                .map(testCase -> testCase.target() + "#" + testCase.mutation().scenario())
                .collect(Collectors.toSet());
        assertTrue(bodyCases.contains("/eserviceId#" + FuzzScenario.REPLACED_WITH_MALFORMED_UUID));
        assertTrue(bodyCases.contains("/descriptorId#" + FuzzScenario.REPLACED_WITH_NIL_UUID));

        DiscoveredOperation getById = discovery.discover(configuration, Set.of("getAgreementById")).get(0);
        var pathSeed = seeds.create(getById).pathParameters();
        assertEquals(
                UUID.fromString("11111111-1111-4111-8111-111111111111"),
                pathSeed.get("agreementId")
        );
        Set<String> pathCases = pathParameterPlanner.plan(decomposer.decompose(pathSeed)).stream()
                .map(testCase -> testCase.target() + "#" + testCase.mutation().scenario())
                .collect(Collectors.toSet());
        assertTrue(pathCases.contains("/agreementId#" + FuzzScenario.REPLACED_WITH_MALFORMED_UUID));
    }

    private GeneratedApiConfiguration configuration() throws Exception {
        URL spec = getClass().getResource("/openapi/generated-api-discovery.yaml");
        if (spec == null) throw new IllegalStateException("Missing generated API discovery fixture");
        return new GeneratedApiConfiguration(
                "generate-bff-client",
                Path.of(spec.toURI()).toString(),
                "it.pagopa.interop.generated.openapi.clients.bff.api",
                "it.pagopa.interop.generated.openapi.clients.bff.model"
        );
    }
}
