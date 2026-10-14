package it.pagopa.infrastructure.openapi;

import it.pagopa.infrastructure.openapi.seed.DeterministicSeedFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DeterministicSeedFactoryTest {

    private final DeterministicSeedFactory factory = new DeterministicSeedFactory();

    @Test
    void supports_required_scalars_enums_nested_objects_and_collections() {
        WidgetPayload seed = factory.create(WidgetPayload.class);

        assertEquals("seed", seed.getName());
        assertEquals(UUID.fromString("11111111-1111-4111-8111-111111111111"), seed.getId());
        assertEquals(WidgetKind.STANDARD, seed.getKind());
        assertEquals(1, seed.getCount());
        assertEquals(1L, seed.getLongCount());
        assertEquals((short) 1, seed.getShortCount());
        assertEquals((byte) 1, seed.getByteCount());
        assertEquals(BigInteger.ONE, seed.getBigInteger());
        assertEquals(BigDecimal.ONE, seed.getBigDecimal());
        assertEquals(1.0f, seed.getFloatValue());
        assertEquals(1.0d, seed.getDoubleValue());
        assertEquals(true, seed.getEnabled());
        assertNotNull(seed.getDetail());
        assertEquals(1L, seed.getDetail().getRevision());
        assertEquals(List.of("seed"), seed.getLabels());
        assertEquals(1, seed.getDetails().size());
        assertEquals(1, seed.getMoreDetails().size());
        assertEquals(1, seed.getNestedCollections().size());
        assertEquals(1, seed.getNestedCollections().get(0).size());
        assertEquals(1, seed.getMetadata().size());
        assertEquals(1, seed.getSamples().length);
        OptionalSeedFixture optionalSeed = factory.create(OptionalSeedFixture.class);
        assertEquals(1L, optionalSeed.getDetail().orElseThrow().getRevision());
    }

    @Test
    void permits_a_finite_recursive_branch_and_is_deterministic() {
        WidgetPayload first = factory.create(WidgetPayload.class);
        WidgetPayload second = factory.create(WidgetPayload.class);

        assertNotNull(first.getChildren());
        assertEquals(1, first.getChildren().size());
        assertNotNull(first.getChildren().get(0));
        assertNotNull(first.getChildren().get(0).getChildren());
        assertEquals(1, first.getChildren().get(0).getChildren().size());
        assertNull(first.getChildren().get(0).getChildren().get(0));
        assertEquals(first.getName(), second.getName());
        assertEquals(first.getId(), second.getId());
        assertEquals(first.getChildren().size(), second.getChildren().size());
        assertEquals(first.getChildren().get(0).getName(), second.getChildren().get(0).getName());
    }

    @Test
    void directly_seeds_simple_collection_array_and_enum_types() {
        assertEquals("seed", factory.create(String.class));
        assertEquals(UUID.fromString("11111111-1111-4111-8111-111111111111"), factory.create(UUID.class));
        assertEquals(WidgetKind.STANDARD, factory.create(WidgetKind.class));
    }

    @Test
    void creates_deterministic_seeds_for_jdk_value_types() {
        assertEquals(java.time.Instant.parse("2020-01-01T00:00:00Z"), factory.create(java.time.Instant.class));
        assertEquals(
                java.time.OffsetDateTime.parse("2020-01-01T00:00:00Z"),
                factory.create(java.time.OffsetDateTime.class)
        );
        assertEquals(java.time.LocalDate.parse("2020-01-01"), factory.create(java.time.LocalDate.class));
        assertEquals(java.net.URI.create("https://example.org/seed"), factory.create(java.net.URI.class));
        assertEquals(factory.create(java.net.URI.class), factory.create(java.net.URI.class));
        assertEquals(java.time.Duration.ofSeconds(1), factory.create(java.time.Duration.class));
    }
}
