package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import it.pagopa.infrastructure.objectgraph.Node;
import it.pagopa.infrastructure.objectgraph.NodeKind;
import it.pagopa.infrastructure.objectgraph.NodePath;
import it.pagopa.infrastructure.objectgraph.NodeSelector;
import it.pagopa.infrastructure.objectgraph.NodeSelectors;
import it.pagopa.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultFuzzEngineTest {

    @Test
    void null_source_is_rejected() {
        DefaultFuzzEngine engine = new DefaultFuzzEngine(
                mock(ObjectGraphDecomposer.class),
                new ObjectMapper(),
                new JacksonFuzzMutationApplier(new ObjectMapper()),
                List.of()
        );
        assertThrows(FuzzingException.class, () -> engine.generate(null));
    }

    @Test
    void decomposes_and_serializes_source_once_and_builds_cases() {
        ObjectGraphDecomposer decomposer = mock(ObjectGraphDecomposer.class);
        ObjectMapper mapper = spy(new ObjectMapper());
        FuzzMutationApplier applier = mock(FuzzMutationApplier.class);
        FuzzRule rule = mock(FuzzRule.class);

        Node root = new Node(NodeKind.OBJECT, NodePath.root(), null, Payload.class);
        Node scalarNode = new Node(NodeKind.SCALAR, path("/name"), "Mario", String.class);
        ObjectGraph graph = objectGraph(List.of(root, scalarNode));
        when(decomposer.decompose(any())).thenReturn(graph);
        when(rule.selector()).thenReturn(NodeSelectors.scalar());
        FuzzMutation mutation = new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, "");
        when(rule.mutationsFor(same(scalarNode), same(graph))).thenReturn(List.of(mutation));
        when(applier.apply(any(), same(scalarNode.path()), same(mutation))).thenReturn(new ObjectMapper().createObjectNode().put("name", ""));

        DefaultFuzzEngine engine = new DefaultFuzzEngine(decomposer, mapper, applier, List.of(rule));
        List<FuzzCase> cases = engine.generate(new Payload("Mario", "admin"));

        assertEquals(1, cases.size());
        verify(decomposer, times(1)).decompose(any(Payload.class));
        verify(mapper, times(1)).valueToTree(any(Payload.class));
        verify(rule, times(1)).mutationsFor(same(scalarNode), same(graph));
    }

    @Test
    void executes_all_rules_and_applies_selectors_on_graph() {
        ObjectGraphDecomposer decomposer = mock(ObjectGraphDecomposer.class);
        ObjectMapper mapper = new ObjectMapper();
        FuzzMutationApplier applier = mock(FuzzMutationApplier.class);

        Node root = new Node(NodeKind.OBJECT, NodePath.root(), null, Payload.class);
        Node scalar = new Node(NodeKind.SCALAR, path("/name"), "Mario", String.class);
        ObjectGraph graph = objectGraph(List.of(root, scalar));
        when(decomposer.decompose(any())).thenReturn(graph);
        when(applier.apply(any(), any(), any())).thenReturn(mapper.createObjectNode());

        FuzzRule scalarRule = mock(FuzzRule.class);
        when(scalarRule.selector()).thenReturn(NodeSelectors.scalar());
        when(scalarRule.mutationsFor(same(scalar), same(graph)))
                .thenReturn(List.of(new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, "")));

        FuzzRule allRule = mock(FuzzRule.class);
        when(allRule.selector()).thenReturn(NodeSelectors.all());
        when(allRule.mutationsFor(any(), same(graph)))
                .thenReturn(List.of(new FuzzMutation(FuzzScenario.REPLACED_WITH_NULL, FuzzMutationKind.REPLACE, null)));

        DefaultFuzzEngine engine = new DefaultFuzzEngine(decomposer, mapper, applier, List.of(scalarRule, allRule));
        engine.generate(new Payload("Mario", "admin"));

        verify(scalarRule, times(1)).mutationsFor(same(scalar), same(graph));
        verify(allRule, times(2)).mutationsFor(any(), same(graph));
    }

    @Test
    void generated_cases_are_independent_and_single_mutation_each() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectGraphDecomposer decomposer = mock(ObjectGraphDecomposer.class);
        FuzzMutationApplier applier = new JacksonFuzzMutationApplier(mapper);
        Node root = new Node(NodeKind.OBJECT, NodePath.root(), null, Payload.class);
        Node name = new Node(NodeKind.SCALAR, path("/name"), "Mario", String.class);
        Node role = new Node(NodeKind.SCALAR, path("/role"), "admin", String.class);
        ObjectGraph graph = objectGraph(List.of(root, name, role));
        when(decomposer.decompose(any())).thenReturn(graph);

        FuzzRule firstRule = new StaticRule(NodeSelectors.scalar(), node -> {
            if (node.path().toString().equals("/name")) {
                return List.of(new FuzzMutation(FuzzScenario.REPLACED_WITH_SQL_INJECTION, FuzzMutationKind.REPLACE, "' OR '1'='1"));
            }
            return List.of(new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""));
        });
        FuzzRule secondRule = new StaticRule(NodeSelectors.scalar(), node -> {
            if (node.path().toString().equals("/name")) {
                return List.of(new FuzzMutation(FuzzScenario.REPLACED_WITH_WRONG_TYPE_NUMBER, FuzzMutationKind.REPLACE, 124));
            }
            return List.of();
        });

        DefaultFuzzEngine engine = new DefaultFuzzEngine(decomposer, mapper, applier, List.of(firstRule, secondRule));
        List<FuzzCase> cases = engine.generate(new Payload("Mario", "admin"));
        Map<String, FuzzCase> byKey = cases.stream()
                .collect(Collectors.toMap(c -> c.target() + "#" + c.mutation().scenario(), Function.identity()));

        assertEquals(3, cases.size());
        assertEquals("' OR '1'='1", byKey.get("/name#REPLACED_WITH_SQL_INJECTION").result().at("/name").asText());
        assertEquals("admin", byKey.get("/name#REPLACED_WITH_SQL_INJECTION").result().at("/role").asText());
        assertEquals("Mario", byKey.get("/role#REPLACED_WITH_EMPTY_STRING").result().at("/name").asText());
        assertEquals("", byKey.get("/role#REPLACED_WITH_EMPTY_STRING").result().at("/role").asText());
        assertEquals(124, byKey.get("/name#REPLACED_WITH_WRONG_TYPE_NUMBER").result().at("/name").asInt());
        assertEquals("admin", byKey.get("/name#REPLACED_WITH_WRONG_TYPE_NUMBER").result().at("/role").asText());
    }

    @Test
    void root_null_and_missing_are_distinct_cases() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectGraphDecomposer decomposer = mock(ObjectGraphDecomposer.class);
        Node root = new Node(NodeKind.OBJECT, NodePath.root(), Map.of("x", 1), Map.class);
        when(decomposer.decompose(any())).thenReturn(objectGraph(List.of(root)));

        DefaultFuzzEngine engine = new DefaultFuzzEngine(
                decomposer,
                mapper,
                new JacksonFuzzMutationApplier(mapper),
                List.of(new NullAndMissingRule())
        );
        List<FuzzCase> cases = engine.generate(new Payload("Mario", "admin"));
        Map<FuzzScenario, FuzzCase> byScenario = cases.stream().collect(Collectors.toMap(c -> c.mutation().scenario(), Function.identity()));

        assertNotNull(byScenario.get(FuzzScenario.REPLACED_WITH_NULL).result());
        assertTrue(byScenario.get(FuzzScenario.REPLACED_WITH_NULL).result() instanceof NullNode);
        assertNull(byScenario.get(FuzzScenario.REMOVED).result());
    }

    @Test
    void generate_does_not_mutate_original_source_object() {
        MutablePayload source = new MutablePayload("Mario", "admin");
        ObjectMapper mapper = new ObjectMapper();
        ObjectGraphDecomposer decomposer = mock(ObjectGraphDecomposer.class);
        Node root = new Node(NodeKind.OBJECT, NodePath.root(), null, MutablePayload.class);
        Node name = new Node(NodeKind.SCALAR, path("/name"), "Mario", String.class);
        when(decomposer.decompose(any())).thenReturn(objectGraph(List.of(root, name)));

        DefaultFuzzEngine engine = new DefaultFuzzEngine(
                decomposer,
                mapper,
                new JacksonFuzzMutationApplier(mapper),
                List.of(new StaticRule(NodeSelectors.scalar(), n -> List.of(new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""))))
        );
        engine.generate(source);

        assertEquals("Mario", source.name);
        assertEquals("admin", source.role);
    }

    @Test
    void fails_fast_without_partial_results() {
        ObjectGraphDecomposer explodingDecomposer = mock(ObjectGraphDecomposer.class);
        when(explodingDecomposer.decompose(any())).thenThrow(new RuntimeException("boom"));
        assertThrows(FuzzingException.class, () -> new DefaultFuzzEngine(
                explodingDecomposer,
                new ObjectMapper(),
                mock(FuzzMutationApplier.class),
                List.of()
        ).generate(new Payload("a", "b")));

        ObjectGraphDecomposer decomposer = mock(ObjectGraphDecomposer.class);
        ObjectMapper mapper = mock(ObjectMapper.class);
        FuzzMutationApplier applier = mock(FuzzMutationApplier.class);
        Node root = new Node(NodeKind.OBJECT, NodePath.root(), null, Payload.class);
        Node scalar = new Node(NodeKind.SCALAR, path("/name"), "Mario", String.class);
        ObjectGraph graph = objectGraph(List.of(root, scalar));
        when(decomposer.decompose(any())).thenReturn(graph);
        FuzzRule rule = new StaticRule(NodeSelectors.scalar(), n -> List.of(new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, "")));

        when(mapper.valueToTree(any())).thenThrow(new RuntimeException("serialization-error"));
        assertThrows(FuzzingException.class, () -> new DefaultFuzzEngine(mockReturningGraph(graph), mapper, applier, List.of(rule)).generate(new Payload("a", "b")));

        when(applier.apply(any(), any(), any())).thenThrow(new FuzzingException("apply-error"));
        assertThrows(FuzzingException.class, () -> new DefaultFuzzEngine(mockReturningGraph(graph), new ObjectMapper(), applier, List.of(rule)).generate(new Payload("a", "b")));

        FuzzRule failingRule = new StaticRule(NodeSelectors.scalar(), n -> {
            throw new RuntimeException("rule-error");
        });
        assertThrows(FuzzingException.class, () -> new DefaultFuzzEngine(mockReturningGraph(graph), new ObjectMapper(), new JacksonFuzzMutationApplier(new ObjectMapper()), List.of(failingRule)).generate(new Payload("a", "b")));
    }

    @Test
    void representative_result_contains_expected_target_mutation_and_json() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectGraph graph = objectGraph(List.of(
                new Node(NodeKind.OBJECT, NodePath.root(), null, Payload.class),
                new Node(NodeKind.SCALAR, path("/name"), "Mario", String.class)
        ));
        DefaultFuzzEngine engine = new DefaultFuzzEngine(
                mockReturningGraph(graph),
                mapper,
                new JacksonFuzzMutationApplier(mapper),
                List.of(new StaticRule(NodeSelectors.scalar(), n -> List.of(new FuzzMutation(FuzzScenario.REPLACED_WITH_EMPTY_STRING, FuzzMutationKind.REPLACE, ""))))
        );

        FuzzCase fuzzCase = engine.generate(new Payload("Mario", "admin")).get(0);

        assertEquals("/name", fuzzCase.target().toString());
        assertEquals(FuzzScenario.REPLACED_WITH_EMPTY_STRING, fuzzCase.mutation().scenario());
        assertEquals(FuzzMutationKind.REPLACE, fuzzCase.mutation().kind());
        assertEquals("", fuzzCase.mutation().value());
        assertEquals("", fuzzCase.result().at("/name").asText());
        assertEquals("admin", fuzzCase.result().at("/role").asText());
    }

    @Test
    void end_to_end_with_complex_object_generates_nested_cases() {
        ObjectMapper mapper = new ObjectMapper();
        ComplexPayload source = new ComplexPayload(
                new Profile("Mario", "Rossi"),
                List.of("admin", "ops"),
                new Limits(3, new BigDecimal("10.50")),
                List.of(new Flag(true, UUID.fromString("c0d9f3c0-9a43-4d8f-9a36-c97c870b13b9")))
        );

        ObjectGraph graph = objectGraph(List.of(
                new Node(NodeKind.OBJECT, NodePath.root(), source, ComplexPayload.class),
                new Node(NodeKind.OBJECT, path("/profile"), source.profile(), Profile.class),
                new Node(NodeKind.SCALAR, path("/profile/name"), source.profile().name(), String.class),
                new Node(NodeKind.COLLECTION, path("/roles"), source.roles(), List.class),
                new Node(NodeKind.SCALAR, path("/roles/0"), source.roles().get(0), String.class),
                new Node(NodeKind.OBJECT, path("/limits"), source.limits(), Limits.class),
                new Node(NodeKind.SCALAR, path("/limits/retryCount"), source.limits().retryCount(), Integer.class),
                new Node(NodeKind.SCALAR, path("/limits/threshold"), source.limits().threshold(), BigDecimal.class),
                new Node(NodeKind.COLLECTION, path("/flags"), source.flags(), List.class),
                new Node(NodeKind.OBJECT, path("/flags/0"), source.flags().get(0), Flag.class),
                new Node(NodeKind.SCALAR, path("/flags/0/enabled"), source.flags().get(0).enabled(), Boolean.class),
                new Node(NodeKind.SCALAR, path("/flags/0/id"), source.flags().get(0).id(), UUID.class)
        ));

        DefaultFuzzEngine engine = new DefaultFuzzEngine(
                mockReturningGraph(graph),
                mapper,
                new JacksonFuzzMutationApplier(mapper),
                List.of(new NullAndMissingRule(), new ScalarRule())
        );

        List<FuzzCase> cases = engine.generate(source);
        assertFalse(cases.isEmpty());

        Map<String, FuzzCase> byKey = cases.stream()
                .collect(Collectors.toMap(
                        c -> c.target() + "#" + c.mutation().scenario(),
                        Function.identity(),
                        (left, right) -> left
                ));

        assertEquals("", byKey.get("/profile/name#REPLACED_WITH_EMPTY_STRING").result().at("/profile/name").asText());
        assertEquals("' OR '1'='1", byKey.get("/roles/0#REPLACED_WITH_SQL_INJECTION").result().at("/roles/0").asText());
        assertEquals("not-a-number", byKey.get("/limits/retryCount#REPLACED_WITH_WRONG_TYPE_STRING").result().at("/limits/retryCount").asText());
        assertEquals("Mario", byKey.get("/limits/retryCount#REPLACED_WITH_WRONG_TYPE_STRING").result().at("/profile/name").asText());
        assertEquals(1, byKey.get("/flags/0/enabled#REPLACED_WITH_WRONG_TYPE_NUMBER").result().at("/flags/0/enabled").asInt());
        assertEquals("not-a-valid-uuid-12345", byKey.get("/flags/0/id#REPLACED_WITH_MALFORMED_UUID").result().at("/flags/0/id").asText());
        assertTrue(byKey.get("#REPLACED_WITH_NULL").result() instanceof NullNode);
        assertNull(byKey.get("#REMOVED").result());
    }

    private ObjectGraphDecomposer mockReturningGraph(ObjectGraph graph) {
        ObjectGraphDecomposer decomposer = mock(ObjectGraphDecomposer.class);
        when(decomposer.decompose(any())).thenReturn(graph);
        return decomposer;
    }

    @SuppressWarnings("unchecked")
    private ObjectGraph objectGraph(List<Node> nodes) {
        try {
            Constructor<ObjectGraph> constructor = ObjectGraph.class.getDeclaredConstructor(List.class);
            constructor.setAccessible(true);
            return constructor.newInstance(nodes);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
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

    record Payload(String name, String role) {
    }

    record ComplexPayload(Profile profile, List<String> roles, Limits limits, List<Flag> flags) {
    }

    record Profile(String name, String surname) {
    }

    record Limits(Integer retryCount, BigDecimal threshold) {
    }

    record Flag(Boolean enabled, UUID id) {
    }

    static class MutablePayload {
        public String name;
        public String role;

        MutablePayload(String name, String role) {
            this.name = name;
            this.role = role;
        }
    }

    record StaticRule(NodeSelector selector, java.util.function.Function<Node, List<FuzzMutation>> function) implements FuzzRule {
        @Override
        public List<FuzzMutation> mutationsFor(Node node, ObjectGraph graph) {
            return function.apply(node);
        }
    }
}
