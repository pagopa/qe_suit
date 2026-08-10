package it.pagopa.interop.common.infrastructure.contract.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzCase;
import it.pagopa.interop.common.infrastructure.fuzzing.FuzzScenario;
import it.pagopa.interop.common.infrastructure.objectgraph.Node;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

final class ContractCasePlanner {
    private final ObjectMapper objectMapper;
    private final ObjectGraphQueryResolver queryResolver;
    private final HttpContractPolicy policy;

    ContractCasePlanner(ObjectMapper objectMapper, ObjectGraphQueryResolver queryResolver, HttpContractPolicy policy) {
        this.objectMapper = objectMapper;
        this.queryResolver = queryResolver;
        this.policy = policy;
    }

    List<GeneratedContractCase> planCases(ScopeState<?> payload, ScopeState<?> pathParams) {
        JsonNode payloadBaseline = payload == null ? null : objectMapper.valueToTree(payload.source());
        JsonNode pathBaseline = pathParams == null ? null : objectMapper.valueToTree(pathParams.source());
        List<GeneratedContractCase> out = new ArrayList<>();
        if (payload != null) {
            out.addAll(planScope(RequestScope.PAYLOAD, payload, payloadBaseline, pathBaseline, pathParams != null));
        }
        if (pathParams != null) {
            out.addAll(planScope(RequestScope.PATH_PARAMS, pathParams, payloadBaseline, pathBaseline, payload != null));
        }
        return out;
    }

    private List<GeneratedContractCase> planScope(RequestScope scope, ScopeState<?> state, JsonNode payloadBaseline, JsonNode pathBaseline, boolean payloadPresent) {
        MutationValidityResolver validityResolver = new JacksonMutationValidityResolver(objectMapper, state.sourceType());
        Map<Key, Consumer<Response>> targetOverrides = resolveTargetOverrides(state);
        List<GeneratedContractCase> out = new ArrayList<>();

        for (FuzzCase fuzzCase : state.fuzzCases()) {
            Node node = state.graph().find(fuzzCase.target())
                    .orElseThrow(() -> new ContractHttpException("Cannot resolve node for target path " + fuzzCase.target()));
            ExpectationSelection selection = resolveExpectation(state, targetOverrides, validityResolver, fuzzCase, node);
            HttpContractRequest request = buildRequest(scope, fuzzCase.result(), payloadBaseline, pathBaseline, payloadPresent);
            out.add(new GeneratedContractCase(scope, fuzzCase.target(), fuzzCase, request, selection.expectation(), selection.origin()));
        }
        return out;
    }

    private HttpContractRequest buildRequest(RequestScope scope, JsonNode mutated, JsonNode payloadBaseline, JsonNode pathBaseline, boolean payloadPresent) {
        if (scope == RequestScope.PAYLOAD) {
            return new HttpContractRequest(mutated, mutated != null, pathBaseline);
        }
        return new HttpContractRequest(payloadBaseline, payloadPresent, mutated);
    }

    private ExpectationSelection resolveExpectation(
            ScopeState<?> state,
            Map<Key, Consumer<Response>> targetOverrides,
            MutationValidityResolver validityResolver,
            FuzzCase fuzzCase,
            Node node
    ) {
        Key key = new Key(fuzzCase.mutation().scenario(), node.path());
        Consumer<Response> targetExpectation = targetOverrides.get(key);
        if (targetExpectation != null) return new ExpectationSelection(targetExpectation, ExpectationOrigin.TARGET_OVERRIDE);

        Consumer<Response> scenarioExpectation = state.overrides().scenario(fuzzCase.mutation().scenario());
        if (scenarioExpectation != null) return new ExpectationSelection(scenarioExpectation, ExpectationOrigin.SCENARIO_OVERRIDE);

        ContractValidity validity = validityResolver.resolve(node, fuzzCase.mutation());
        if (validity == ContractValidity.VALID) {
            return new ExpectationSelection(policy.success(), ExpectationOrigin.INFERRED_VALID);
        }
        if (validity == ContractValidity.INVALID) {
            return new ExpectationSelection(policy.expectationFor(fuzzCase.mutation().scenario()), ExpectationOrigin.POLICY_INVALID);
        }
        return new ExpectationSelection(policy.expectationFor(fuzzCase.mutation().scenario()), ExpectationOrigin.POLICY_UNKNOWN);
    }

    private Map<Key, Consumer<Response>> resolveTargetOverrides(ScopeState<?> state) {
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

    private record ExpectationSelection(Consumer<Response> expectation, ExpectationOrigin origin) {
    }
}
