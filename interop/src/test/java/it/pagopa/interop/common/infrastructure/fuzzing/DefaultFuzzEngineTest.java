package it.pagopa.interop.common.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import it.pagopa.interop.common.infrastructure.objectgraph.Node;
import it.pagopa.interop.common.infrastructure.objectgraph.NodeKind;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;
import it.pagopa.interop.common.infrastructure.objectgraph.NodeSelector;
import it.pagopa.interop.common.infrastructure.objectgraph.NodeSelectors;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraphDecomposer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        FuzzMutation mutation = new FuzzMutation(FuzzScenario.EMPTY_STRING, FuzzMutationKind.REPLACE, "");
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
                .thenReturn(List.of(new FuzzMutation(FuzzScenario.EMPTY_STRING, FuzzMutationKind.REPLACE, "")));

        FuzzRule allRule = mock(FuzzRule.class);
        when(allRule.selector()).thenReturn(NodeSelectors.all());
        when(allRule.mutationsFor(any(), same(graph)))
                .thenReturn(List.of(new FuzzMutation(FuzzScenario.NULL, FuzzMutationKind.REPLACE, null)));

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
                return List.of(new FuzzMutation(FuzzScenario.SQL_INJECTION, FuzzMutationKind.REPLACE, "' OR '1'='1"));
            }
            return List.of(new FuzzMutation(FuzzScenario.EMPTY_STRING, FuzzMutationKind.REPLACE, ""));
        });
        FuzzRule secondRule = new StaticRule(NodeSelectors.scalar(), node -> {
            if (node.path().toString().equals("/name")) {
                return List.of(new FuzzMutation(FuzzScenario.WRONG_TYPE_NUMBER, FuzzMutationKind.REPLACE, 124));
            }
            return List.of();
        });

        DefaultFuzzEngine engine = new DefaultFuzzEngine(decomposer, mapper, applier, List.of(firstRule, secondRule));
        List<FuzzCase> cases = engine.generate(new Payload("Mario", "admin"));
        Map<String, FuzzCase> byKey = cases.stream()
                .collect(Collectors.toMap(c -> c.target() + "#" + c.mutation().scenario(), Function.identity()));

        assertEquals(3, cases.size());
        assertEquals("' OR '1'='1", byKey.get("/name#SQL_INJECTION").result().at("/name").asText());
        assertEquals("admin", byKey.get("/name#SQL_INJECTION").result().at("/role").asText());
        assertEquals("Mario", byKey.get("/role#EMPTY_STRING").result().at("/name").asText());
        assertEquals("", byKey.get("/role#EMPTY_STRING").result().at("/role").asText());
        assertEquals(124, byKey.get("/name#WRONG_TYPE_NUMBER").result().at("/name").asInt());
        assertEquals("admin", byKey.get("/name#WRONG_TYPE_NUMBER").result().at("/role").asText());
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

        assertNotNull(byScenario.get(FuzzScenario.NULL).result());
        assertTrue(byScenario.get(FuzzScenario.NULL).result() instanceof NullNode);
        assertNull(byScenario.get(FuzzScenario.MISSING).result());
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
                List.of(new StaticRule(NodeSelectors.scalar(), n -> List.of(new FuzzMutation(FuzzScenario.EMPTY_STRING, FuzzMutationKind.REPLACE, ""))))
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
        FuzzRule rule = new StaticRule(NodeSelectors.scalar(), n -> List.of(new FuzzMutation(FuzzScenario.EMPTY_STRING, FuzzMutationKind.REPLACE, "")));

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
                List.of(new StaticRule(NodeSelectors.scalar(), n -> List.of(new FuzzMutation(FuzzScenario.EMPTY_STRING, FuzzMutationKind.REPLACE, ""))))
        );

        FuzzCase fuzzCase = engine.generate(new Payload("Mario", "admin")).get(0);

        assertEquals("/name", fuzzCase.target().toString());
        assertEquals(FuzzScenario.EMPTY_STRING, fuzzCase.mutation().scenario());
        assertEquals(FuzzMutationKind.REPLACE, fuzzCase.mutation().kind());
        assertEquals("", fuzzCase.mutation().value());
        assertEquals("", fuzzCase.result().at("/name").asText());
        assertEquals("admin", fuzzCase.result().at("/role").asText());
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
