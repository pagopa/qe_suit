package it.pagopa.infrastructure.pst;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.contract.http.ContractValidity;
import it.pagopa.infrastructure.contract.http.ExpectationResolver;
import it.pagopa.infrastructure.contract.http.JacksonMutationValidityResolver;
import it.pagopa.infrastructure.contract.http.MutationValidityResolver;
import it.pagopa.infrastructure.contract.http.QueryParameterValidityResolver;
import it.pagopa.infrastructure.contract.http.RequestScope;
import it.pagopa.infrastructure.fuzzing.FuzzCasePlanner;
import it.pagopa.infrastructure.fuzzing.PlannedFuzzCase;
import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import it.pagopa.infrastructure.openapi.DiscoveredOperation;
import it.pagopa.infrastructure.openapi.GeneratedApiConfiguration;
import it.pagopa.infrastructure.openapi.OpenApiOperationDiscovery;
import it.pagopa.infrastructure.openapi.QueryParameterDescriptor;
import it.pagopa.infrastructure.openapi.seed.OperationSeed;
import it.pagopa.infrastructure.openapi.seed.OperationSeedFactory;
import it.pagopa.infrastructure.pst.model.PstDocument;
import it.pagopa.infrastructure.pst.model.PstOperation;
import it.pagopa.infrastructure.pst.model.PstScenario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class PstGenerator {
    private final OpenApiOperationDiscovery operationDiscovery;
    private final GeneratedApiConfiguration apiConfiguration;
    private final OperationSeedFactory seedFactory;
    private final ObjectGraphDecomposer graphDecomposer;
    private final ObjectMapper objectMapper;
    private final FuzzCasePlanner payloadPlanner;
    private final FuzzCasePlanner pathParamsPlanner;
    private final FuzzCasePlanner queryParamsPlanner;

    public PstGenerator(
            OpenApiOperationDiscovery operationDiscovery,
            GeneratedApiConfiguration apiConfiguration,
            OperationSeedFactory seedFactory,
            ObjectGraphDecomposer graphDecomposer,
            ObjectMapper objectMapper,
            FuzzCasePlanner payloadPlanner,
            FuzzCasePlanner pathParamsPlanner
    ) {
        this(
                operationDiscovery,
                apiConfiguration,
                seedFactory,
                graphDecomposer,
                objectMapper,
                payloadPlanner,
                pathParamsPlanner,
                pathParamsPlanner
        );
    }

    public PstGenerator(
            OpenApiOperationDiscovery operationDiscovery,
            GeneratedApiConfiguration apiConfiguration,
            OperationSeedFactory seedFactory,
            ObjectGraphDecomposer graphDecomposer,
            ObjectMapper objectMapper,
            FuzzCasePlanner payloadPlanner,
            FuzzCasePlanner pathParamsPlanner,
            FuzzCasePlanner queryParamsPlanner
    ) {
        this.operationDiscovery = Objects.requireNonNull(operationDiscovery, "operationDiscovery must not be null");
        this.apiConfiguration = Objects.requireNonNull(apiConfiguration, "apiConfiguration must not be null");
        this.seedFactory = Objects.requireNonNull(seedFactory, "seedFactory must not be null");
        this.graphDecomposer = Objects.requireNonNull(graphDecomposer, "graphDecomposer must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.payloadPlanner = Objects.requireNonNull(payloadPlanner, "payloadPlanner must not be null");
        this.pathParamsPlanner = Objects.requireNonNull(pathParamsPlanner, "pathParamsPlanner must not be null");
        this.queryParamsPlanner = Objects.requireNonNull(queryParamsPlanner, "queryParamsPlanner must not be null");
    }

    public PstDocument generate(PstConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        List<DiscoveredOperation> discovered = operationDiscovery.discover(apiConfiguration, config.operations());
        validateOverrideOperations(config, discovered);
        ExpectationResolver<Integer> expectationResolver =
                new ExpectationResolver<>(config::successStatus, config::statusFor);

        List<PstOperation> operations = new ArrayList<>();
        for (DiscoveredOperation operation : discovered) {
            OperationSeed seed = seedFactory.create(operation);
            List<PstScenario> scenarios = new ArrayList<>();
            seed.requestBody().ifPresent(body -> scenarios.addAll(planScope(
                    operation,
                    RequestScope.PAYLOAD,
                    body,
                    payloadPlanner,
                    config,
                    expectationResolver
            )));
            if (!seed.pathParameters().isEmpty()) {
                scenarios.addAll(planScope(
                        operation,
                        RequestScope.PATH_PARAMS,
                        seed.pathParameters(),
                        pathParamsPlanner,
                        config,
                        expectationResolver
                ));
            }
            if (!seed.queryParameters().isEmpty()) {
                scenarios.addAll(planScope(
                        operation,
                        RequestScope.QUERY_PARAMS,
                        seed.queryParameters(),
                        queryParamsPlanner,
                        config,
                        expectationResolver
                ));
            }
            operations.add(new PstOperation(
                    operation.operationId(),
                    operation.httpMethod(),
                    operation.path(),
                    scenarios
            ));
        }
        return new PstDocument(operations);
    }

    private List<PstScenario> planScope(
            DiscoveredOperation operation,
            RequestScope scope,
            Object source,
            FuzzCasePlanner planner,
            PstConfig config,
            ExpectationResolver<Integer> expectationResolver
    ) {
        ObjectGraph graph = graphDecomposer.decompose(source);
        MutationValidityResolver validityResolver = validityResolver(operation, scope, source);
        List<PstScenario> scenarios = new ArrayList<>();
        for (PlannedFuzzCase planned : planner.plan(graph)) {
            Node node = graph.find(planned.target()).orElseThrow(
                    () -> new IllegalStateException("Cannot resolve node for target path " + planned.target())
            );
            ContractValidity validity = validityResolver.resolve(node, planned.mutation());
            Optional<Integer> targetOverride = findOverride(config, operation, scope, planned);
            ExpectationResolver.Resolution<Integer> expected = expectationResolver.resolve(
                    planned.mutation().scenario(),
                    targetOverride,
                    Optional.empty(),
                    () -> validity
            );
            scenarios.add(new PstScenario(
                    scope,
                    planned.target(),
                    planned.mutation().scenario(),
                    validity,
                    expected.expectation(),
                    expected.origin()
            ));
        }
        return scenarios;
    }

    /**
     * Query parameters carry no validation annotation: their only contract metadata is
     * {@code required}, taken from the OpenAPI specification.
     */
    private MutationValidityResolver validityResolver(DiscoveredOperation operation, RequestScope scope, Object source) {
        if (scope != RequestScope.QUERY_PARAMS) {
            return new JacksonMutationValidityResolver(objectMapper, source.getClass());
        }
        Map<String, Boolean> requiredByName = new LinkedHashMap<>();
        for (QueryParameterDescriptor parameter : operation.queryParameters()) {
            requiredByName.put(parameter.name(), parameter.required());
        }
        return new QueryParameterValidityResolver(requiredByName);
    }

    private Optional<Integer> findOverride(
            PstConfig config,
            DiscoveredOperation operation,
            RequestScope scope,
            PlannedFuzzCase planned
    ) {
        return config.overrides().stream()
                .filter(override -> override.operationId().equals(operation.operationId()))
                .filter(override -> override.scope() == scope)
                .filter(override -> override.target().equals(planned.target()))
                .filter(override -> override.scenario() == planned.mutation().scenario())
                .map(PstOverride::status)
                .findFirst();
    }

    private void validateOverrideOperations(PstConfig config, List<DiscoveredOperation> operations) {
        var operationIds = operations.stream().map(DiscoveredOperation::operationId).collect(java.util.stream.Collectors.toSet());
        config.overrides().stream()
                .map(PstOverride::operationId)
                .filter(operationId -> !operationIds.contains(operationId))
                .findFirst()
                .ifPresent(operationId -> {
                    throw new PstConfigurationException(
                            "PST override references operationId that is not selected or does not exist: " + operationId
                    );
                });
    }
}
