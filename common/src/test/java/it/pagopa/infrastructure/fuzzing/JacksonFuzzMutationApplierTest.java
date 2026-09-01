package it.pagopa.infrastructure.fuzzing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import it.pagopa.infrastructure.objectgraph.NodePath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonFuzzMutationApplierTest {

    private ObjectMapper objectMapper;
    private JacksonFuzzMutationApplier applier;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        applier = new JacksonFuzzMutationApplier(objectMapper);
    }

    @Test
    void mutates_object_children_for_replace_and_remove() throws Exception {
        JsonNode source = objectMapper.readTree("{\"name\":\"Mario\",\"age\":20,\"role\":\"admin\"}");
        assertEquals("x", applier.apply(source.deepCopy(), path("/name"), replace("x")).at("/name").asText());
        assertEquals(124, applier.apply(source.deepCopy(), path("/age"), replace(124)).at("/age").asInt());
        assertTrue(applier.apply(source.deepCopy(), path("/role"), replace(null)).at("/role").isNull());

        JsonNode removed = applier.apply(source.deepCopy(), path("/name"), remove());
        assertTrue(removed.at("/name").isMissingNode());
    }

    @Test
    void mutates_array_children_for_replace_and_remove() throws Exception {
        JsonNode source = objectMapper.readTree("{\"values\":[\"a\",\"b\",\"c\"]}");
        assertEquals("z", applier.apply(source.deepCopy(), path("/values/1"), replace("z")).at("/values/1").asText());
        assertTrue(applier.apply(source.deepCopy(), path("/values/1"), replace(null)).at("/values/1").isNull());

        JsonNode removed = applier.apply(source.deepCopy(), path("/values/1"), remove());
        assertEquals(2, removed.at("/values").size());
        assertEquals("a", removed.at("/values/0").asText());
        assertEquals("c", removed.at("/values/1").asText());
    }

    @Test
    void mutates_nested_structures() throws Exception {
        JsonNode source = objectMapper.readTree("{\"user\":{\"name\":\"Mario\",\"roles\":[{\"code\":\"A\"}]}}");
        assertEquals("Luigi", applier.apply(source.deepCopy(), path("/user/name"), replace("Luigi")).at("/user/name").asText());

        JsonNode removedName = applier.apply(source.deepCopy(), path("/user/name"), remove());
        assertTrue(removedName.at("/user/name").isMissingNode());

        assertEquals("B", applier.apply(source.deepCopy(), path("/user/roles/0/code"), replace("B")).at("/user/roles/0/code").asText());
        JsonNode removedRole = applier.apply(source.deepCopy(), path("/user/roles/0"), remove());
        assertEquals(0, removedRole.at("/user/roles").size());
    }

    @Test
    void mutates_root_for_replace_and_remove() throws Exception {
        JsonNode source = objectMapper.readTree("{\"name\":\"Mario\"}");
        JsonNode scalar = applier.apply(source.deepCopy(), NodePath.root(), replace("hello"));
        assertEquals("hello", scalar.asText());

        JsonNode objectLike = applier.apply(source.deepCopy(), NodePath.root(), replace(Map.of("a", 1)));
        assertEquals(1, objectLike.at("/a").asInt());

        JsonNode nullRoot = applier.apply(source.deepCopy(), NodePath.root(), replace(null));
        assertTrue(nullRoot instanceof NullNode);
        assertNull(applier.apply(source.deepCopy(), NodePath.root(), remove()));
    }

    @Test
    void handles_json_pointer_escaped_property_names() throws Exception {
        JsonNode source = objectMapper.readTree("{\"foo/bar\":\"a\",\"foo~bar\":\"b\"}");
        JsonNode slash = applier.apply(source.deepCopy(), path("/foo~1bar"), replace("x"));
        JsonNode tilde = applier.apply(source.deepCopy(), path("/foo~0bar"), replace("y"));

        assertEquals("x", slash.get("foo/bar").asText());
        assertEquals("y", tilde.get("foo~bar").asText());
    }

    @Test
    void fails_fast_on_invalid_paths_or_parent_types() throws Exception {
        JsonNode source = objectMapper.readTree("{\"user\":{\"name\":\"Mario\"},\"values\":[\"a\"]}");

        assertThrows(FuzzingException.class, () -> applier.apply(source.deepCopy(), path("/missing/name"), replace("x")));
        assertThrows(FuzzingException.class, () -> applier.apply(source.deepCopy(), path("/user/name/child"), replace("x")));
        assertThrows(FuzzingException.class, () -> applier.apply(source.deepCopy(), path("/values/not-an-index"), replace("x")));
        assertThrows(FuzzingException.class, () -> applier.apply(source.deepCopy(), path("/values/2"), remove()));
    }

    @Test
    void object_field_absent_in_json_can_be_replaced_or_removed() throws Exception {
        JsonNode source = objectMapper.readTree("{\"eserviceId\":\"a\",\"descriptorId\":\"b\"}");
        JsonNode replaced = applier.apply(source.deepCopy(), path("/delegationId"), replace("not-a-valid-uuid"));
        assertEquals("not-a-valid-uuid", replaced.at("/delegationId").asText());

        JsonNode removed = applier.apply(source.deepCopy(), path("/delegationId"), remove());
        assertTrue(removed.at("/delegationId").isMissingNode());
    }

    private FuzzMutation replace(Object value) {
        return new FuzzMutation(FuzzScenario.REPLACED_WITH_NULL, FuzzMutationKind.REPLACE, value);
    }

    private FuzzMutation remove() {
        return new FuzzMutation(FuzzScenario.REMOVED, FuzzMutationKind.REMOVE, null);
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
