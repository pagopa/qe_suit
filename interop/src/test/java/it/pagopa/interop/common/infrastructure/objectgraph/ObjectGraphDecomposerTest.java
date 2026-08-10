package it.pagopa.interop.common.infrastructure.objectgraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ObjectGraphDecomposerTest {

    private ObjectGraphDecomposer decomposer;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        decomposer = new DefaultObjectGraphDecomposer(new JacksonObjectDecomposer(objectMapper));
    }

    @Test
    void decompose_simple_pojo() {
        ObjectGraph graph = decomposer.decompose(new Person("Mario", 30));

        assertEquals(NodeKind.OBJECT, graph.root().kind());
        assertEquals(NodeKind.SCALAR, byPointer(graph, "/name").kind());
        assertEquals(NodeKind.SCALAR, byPointer(graph, "/age").kind());
    }

    @Test
    void decompose_nested_object_and_navigation() {
        ObjectGraph graph = decomposer.decompose(new PersonWithAddress("Mario", new Address("Roma")));
        Node root = graph.root();
        Node address = byPointer(graph, "/address");
        Node city = byPointer(graph, "/address/city");

        assertTrue(graph.find(NodePath.root()).isPresent());
        assertTrue(graph.find(address.path()).isPresent());
        assertEquals(2, graph.childrenOf(root).size());
        assertEquals(1, graph.childrenOf(address).size());
        assertTrue(city.path().parent().orElseThrow().equals(address.path()));
    }

    @Test
    void decompose_collection() {
        ObjectGraph graph = decomposer.decompose(new PersonWithTags(List.of("premium", "customer")));

        assertEquals(NodeKind.COLLECTION, byPointer(graph, "/tags").kind());
        assertEquals(NodeKind.SCALAR, byPointer(graph, "/tags/0").kind());
        assertEquals(NodeKind.SCALAR, byPointer(graph, "/tags/1").kind());
    }

    @Test
    void decompose_array() {
        ObjectGraph graph = decomposer.decompose(new PersonWithArray(new String[]{"a", "b"}));

        assertEquals(NodeKind.COLLECTION, byPointer(graph, "/tags").kind());
        assertEquals(NodeKind.SCALAR, byPointer(graph, "/tags/0").kind());
        assertEquals(NodeKind.SCALAR, byPointer(graph, "/tags/1").kind());
    }

    @Test
    void decompose_map() {
        Map<String, Address> addresses = new LinkedHashMap<>();
        addresses.put("home", new Address("Rome"));
        addresses.put("work", new Address("Milan"));

        ObjectGraph graph = decomposer.decompose(new MapHolder(addresses));

        assertEquals(NodeKind.OBJECT, byPointer(graph, "/addresses").kind());
        assertEquals(NodeKind.OBJECT, byPointer(graph, "/addresses/home").kind());
        assertEquals(NodeKind.OBJECT, byPointer(graph, "/addresses/work").kind());
        assertEquals(NodeKind.SCALAR, byPointer(graph, "/addresses/home/city").kind());
    }

    @Test
    void decompose_null_semantics() {
        ObjectGraph graph = decomposer.decompose(new NullableContainer(null, null, null));

        Node name = byPointer(graph, "/name");
        Node address = byPointer(graph, "/address");
        Node tags = byPointer(graph, "/tags");

        assertEquals(NodeKind.SCALAR, name.kind());
        assertNull(name.value());
        assertEquals(String.class, name.javaType());

        assertEquals(NodeKind.OBJECT, address.kind());
        assertNull(address.value());
        assertEquals(Address.class, address.javaType());
        assertTrue(graph.childrenOf(address).isEmpty());

        assertEquals(NodeKind.COLLECTION, tags.kind());
        assertNull(tags.value());
        assertEquals(List.class, tags.javaType());
        assertTrue(graph.childrenOf(tags).isEmpty());
    }

    @Test
    void empty_collection_is_not_leaf() {
        ObjectGraph graph = decomposer.decompose(new PersonWithTags(List.of()));
        Node tags = byPointer(graph, "/tags");

        assertEquals(NodeKind.COLLECTION, tags.kind());
        assertFalse(tags.isLeaf());
        assertTrue(graph.childrenOf(tags).isEmpty());
    }

    @Test
    void honors_jsonproperty_and_jsonignore() {
        ObjectGraph graph = decomposer.decompose(new AnnotatedPerson("Mario", "hidden"));

        assertEquals(NodeKind.SCALAR, byPointer(graph, "/first_name").kind());
        assertThrows(AssertionError.class, () -> byPointer(graph, "/firstName"));
        assertThrows(AssertionError.class, () -> byPointer(graph, "/internal"));
    }

    @Test
    void escapes_json_pointer_tokens() {
        ObjectGraph graph = decomposer.decompose(new EscapedProps("a", "b"));

        assertEquals(NodeKind.SCALAR, byPointer(graph, "/foo~1bar").kind());
        assertEquals(NodeKind.SCALAR, byPointer(graph, "/foo~0bar").kind());
    }

    @Test
    void selectors_work_and_are_composable() {
        ObjectGraph graph = decomposer.decompose(new PersonWithTags(List.of("premium", "customer")));

        assertEquals(graph.nodes().size(), graph.select(NodeSelectors.all()).size());
        assertFalse(graph.select(NodeSelectors.scalar()).isEmpty());
        assertFalse(graph.select(NodeSelectors.object()).isEmpty());
        assertFalse(graph.select(NodeSelectors.collection()).isEmpty());
        assertEquals(graph.select(NodeSelectors.scalar()).size(), graph.select(NodeSelectors.leaf()).size());
        assertEquals(1, graph.select(NodeSelectors.root()).size());

        List<Node> stringScalars = graph.select(NodeSelectors.scalar().and(NodeSelectors.valueType(String.class)));
        assertEquals(2, stringScalars.size());
    }

    @Test
    void supports_root_object_collection_and_scalar() {
        ObjectGraph objectRoot = decomposer.decompose(new Person("Mario", 20));
        ObjectGraph collectionRoot = decomposer.decompose(List.of("a", "b"));
        ObjectGraph scalarRoot = decomposer.decompose("value");

        assertEquals(NodeKind.OBJECT, objectRoot.root().kind());
        assertEquals(NodeKind.COLLECTION, collectionRoot.root().kind());
        assertEquals(NodeKind.SCALAR, scalarRoot.root().kind());
    }

    @Test
    void null_root_is_not_supported() {
        assertThrows(ObjectGraphException.class, () -> decomposer.decompose(null));
    }

    @Test
    void wraps_jackson_errors_fail_fast() {
        ObjectGraphException exception = assertThrows(ObjectGraphException.class, () -> decomposer.decompose(new ExplodingGetter()));
        assertTrue(exception.getMessage().contains("Failed to serialize source type"));
    }

    @Test
    void nullablePropertyExcludedByJsonIncludeNonNullIsStillCorrelated() {
        UUID eserviceId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID descriptorId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        ObjectGraph graph = decomposer.decompose(new GeneratedLikePayload(eserviceId, descriptorId, null));

        Node delegation = byPointer(graph, "/delegationId");
        assertEquals(NodeKind.SCALAR, delegation.kind());
        assertNull(delegation.value());
        assertEquals(UUID.class, delegation.javaType());
    }

    private Node byPointer(ObjectGraph graph, String pointer) {
        return graph.nodes().stream()
                .filter(node -> node.path().toString().equals(pointer))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Node not found for pointer: " + pointer));
    }

    record Person(String name, int age) { }
    record Address(String city) { }
    record PersonWithAddress(String name, Address address) { }
    record PersonWithTags(List<String> tags) { }
    record PersonWithArray(String[] tags) { }
    record MapHolder(Map<String, Address> addresses) { }
    record NullableContainer(String name, Address address, List<String> tags) { }
    record AnnotatedPerson(@JsonProperty("first_name") String firstName, @JsonIgnore String internal) { }
    record EscapedProps(@JsonProperty("foo/bar") String slash, @JsonProperty("foo~bar") String tilde) { }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    record GeneratedLikePayload(UUID eserviceId, UUID descriptorId, UUID delegationId) { }

    static class ExplodingGetter {
        public String getValue() {
            throw new IllegalStateException("boom");
        }
    }
}
