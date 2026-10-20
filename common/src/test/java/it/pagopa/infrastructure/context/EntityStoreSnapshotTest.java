package it.pagopa.infrastructure.context;

import it.pagopa.application.context.EntityStore;
import it.pagopa.domain.Identifiable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EntityStoreSnapshotTest {
    static Stream<EntityStore> stores() {
        return Stream.of(new InMemoryEntityStore(), new CucumberEntityStore());
    }

    @ParameterizedTest
    @MethodSource("stores")
    void returnsOrderedSnapshotWithoutDuplicatesOnUpdate(EntityStore store) {
        assertThat(store.getAll(Model.class)).isEmpty();
        Model first = new Model(UUID.randomUUID());
        Model second = new Model(UUID.randomUUID());
        store.upsert(first);
        store.upsert(second);
        var snapshot = store.getAll(Model.class);
        Model updated = new Model(first.getId());
        store.upsert(updated);
        Model third = new Model(UUID.randomUUID());
        store.upsert(third);

        assertThat(snapshot).containsExactly(first, second);
        assertThat(store.getAll(Model.class)).containsExactly(updated, second, third);
    }

    private static class Model implements Identifiable {
        private final UUID id;

        Model(UUID id) {
            this.id = id;
        }

        @Override
        public UUID getId() {
            return id;
        }
    }
}
