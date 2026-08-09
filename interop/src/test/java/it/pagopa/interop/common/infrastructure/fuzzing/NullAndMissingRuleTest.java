package it.pagopa.interop.common.infrastructure.fuzzing;

import it.pagopa.interop.common.infrastructure.objectgraph.Node;
import it.pagopa.interop.common.infrastructure.objectgraph.NodeKind;
import it.pagopa.interop.common.infrastructure.objectgraph.NodePath;
import it.pagopa.interop.common.infrastructure.objectgraph.ObjectGraph;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class NullAndMissingRuleTest {

    private final NullAndMissingRule rule = new NullAndMissingRule();

    @Test
    void selector_matches_root_nodes() {
        Node node = new Node(NodeKind.OBJECT, NodePath.root(), new Object(), Object.class);
        assertTrue(rule.selector().matches(node));
    }

    @Test
    void selector_matches_scalar_nodes() {
        Node node = new Node(NodeKind.SCALAR, path("/name"), "x", String.class);
        assertTrue(rule.selector().matches(node));
    }

    @Test
    void selector_matches_object_nodes() {
        Node node = new Node(NodeKind.OBJECT, path("/address"), null, Object.class);
        assertTrue(rule.selector().matches(node));
    }

    @Test
    void selector_matches_collection_nodes() {
        Node node = new Node(NodeKind.COLLECTION, path("/items"), null, List.class);
        assertTrue(rule.selector().matches(node));
    }

    @Test
    void every_selected_node_generates_exactly_two_expected_mutations() {
        Node node = new Node(NodeKind.SCALAR, path("/name"), "x", String.class);
        ObjectGraph graph = mock(ObjectGraph.class);

        List<FuzzMutation> mutations = rule.mutationsFor(node, graph);

        assertEquals(2, mutations.size());
        assertEquals(new FuzzMutation(FuzzScenario.NULL, FuzzMutationKind.REPLACE, null), mutations.get(0));
        assertEquals(new FuzzMutation(FuzzScenario.MISSING, FuzzMutationKind.REMOVE, null), mutations.get(1));
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
}
