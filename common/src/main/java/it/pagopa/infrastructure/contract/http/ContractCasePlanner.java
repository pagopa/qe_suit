package it.pagopa.infrastructure.contract.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.infrastructure.fuzzing.FuzzCase;
import it.pagopa.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.NodePath;
import it.pagopa.infrastructure.objectgraph.ObjectGraphQuery;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

final class ContractCasePlanner {
    private final ObjectMapper objectMapper;
    private final ObjectGraphQueryResolver queryResolver;
    private final ExpectationResolver<Consumer<Response>> expectationResolver;

    ContractCasePlanner(ObjectMapper objectMapper, ObjectGraphQueryResolver queryResolver, HttpContractPolicy policy) {
        this.objectMapper = objectMapper;
        this.queryResolver = queryResolver;
        this.expectationResolver = new ExpectationResolver<>(policy::success, policy::expectationFor);
    }

    List<GeneratedContractCase> planCases(ScopePlanState<?> payload, ScopePlanState<?> pathParams) {
        return planCases(payload, pathParams, null);
    }

    List<GeneratedContractCase> planCases(
            ScopePlanState<?> payload,
            ScopePlanState<?> pathParams,
            ScopePlanState<?> queryParams
    ) {
        List<GeneratedContractCase> out = new ArrayList<>();
        if (payload != null) {
            out.addAll(planScope(RequestScope.PAYLOAD, payload));
        }
        if (pathParams != null) {
            out.addAll(planScope(RequestScope.PATH_PARAMS, pathParams));
        }
        if (queryParams != null) {
            out.addAll(planScope(RequestScope.QUERY_PARAMS, queryParams));
        }
        return out;
    }

    private List<GeneratedContractCase> planScope(RequestScope scope, ScopePlanState<?> state) {
        MutationValidityResolver validityResolver = validityResolver(scope, state);
        Map<Key, Consumer<Response>> targetOverrides = resolveTargetOverrides(state);
        List<GeneratedContractCase> out = new ArrayList<>();

        for (FuzzCase fuzzCase : state.fuzzCases()) {
            Node node = state.graph().find(fuzzCase.target())
                    .orElseThrow(() -> new ContractHttpException("Cannot resolve node for target path " + fuzzCase.target()));
            ExpectationResolver.Resolution<Consumer<Response>> selection =
                    resolveExpectation(state, targetOverrides, validityResolver, fuzzCase, node);
            out.add(new GeneratedContractCase(scope, fuzzCase.target(), fuzzCase.mutation(), selection.expectation(), selection.origin()));
        }
        return out;
    }

    /**
     * Query parameters have no generated DTO carrying validation annotations, so their contract
     * metadata is the {@code required} flag. When the caller does not provide it, every declared
     * parameter is treated as optional, which mirrors the convention that a call without query
     * parameters succeeds.
     */
    private MutationValidityResolver validityResolver(RequestScope scope, ScopePlanState<?> state) {
        if (state.validityResolver() != null) {
            return state.validityResolver();
        }
        if (scope == RequestScope.QUERY_PARAMS) {
            return new QueryParameterValidityResolver(optionalParameters(state.source()));
        }
        return new JacksonMutationValidityResolver(objectMapper, state.sourceType());
    }

    private Map<String, Boolean> optionalParameters(Object source) {
        if (!(source instanceof Map<?, ?> parameters)) {
            return Map.of();
        }
        Map<String, Boolean> optional = new LinkedHashMap<>();
        for (Object name : parameters.keySet()) {
            optional.put(String.valueOf(name), false);
        }
        return optional;
    }

    private ExpectationResolver.Resolution<Consumer<Response>> resolveExpectation(
            ScopePlanState<?> state,
            Map<Key, Consumer<Response>> targetOverrides,
            MutationValidityResolver validityResolver,
            FuzzCase fuzzCase,
            Node node
    ) {
        Key key = new Key(fuzzCase.mutation().scenario(), node.path());
        return expectationResolver.resolve(
                fuzzCase.mutation().scenario(),
                Optional.ofNullable(targetOverrides.get(key)),
                Optional.ofNullable(state.overrides().scenario(fuzzCase.mutation().scenario())),
                () -> validityResolver.resolve(node, fuzzCase.mutation())
        );
    }

    private Map<Key, Consumer<Response>> resolveTargetOverrides(ScopePlanState<?> state) {
        Map<Key, Consumer<Response>> out = new java.util.HashMap<>();
        for (ScopeOverrides.TargetOverride targetOverride : state.overrides().targets()) {
            for (TargetExpression<?> expression : targetOverride.targets()) {
                ObjectGraphQuery query = resolveQuery(state.sourceType(), expression);
                NodePath nodePath = state.graph().find(query).path();
                for (FuzzScenario scenario : targetOverride.scenarios()) {
                    Key key = new Key(scenario, nodePath);
                    Consumer<Response> existing = out.putIfAbsent(key, targetOverride.expectation());
                    if (existing != null && !Objects.equals(existing, targetOverride.expectation())) {
                        throw new ContractHttpException("Conflicting target override for " + scenario + " @ " + nodePath);
                    }
                }
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private <T> ObjectGraphQuery resolveQuery(Class<T> sourceType, TargetExpression<?> expression) {
        return queryResolver.resolve(sourceType, (TargetExpression<T>) expression);
    }

    private record Key(FuzzScenario scenario, NodePath path) {
    }
}
