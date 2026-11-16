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
    void updatesEntityWithoutChangingInsertionOrderOrDuplicatingIt(EntityStore store) {
        assertThat(store.getFirst(Model.class)).isEmpty();
        assertThat(store.getLast(Model.class)).isEmpty();
        Model first = new Model(UUID.randomUUID());
        Model second = new Model(UUID.randomUUID());
        store.upsert(first);
        store.upsert(second);
        var firstSnapshot = store.find(Model.class, model -> model.getId().equals(first.getId()));

        assertThat(firstSnapshot).containsSame(first);

        Model updated = new Model(first.getId());
        store.upsert(updated);

        assertThat(firstSnapshot).containsSame(first);
        assertThat(store.getFirst(Model.class)).containsSame(updated);
        assertThat(store.getLast(Model.class)).containsSame(second);
        assertThat(store.find(Model.class, model -> model.getId().equals(first.getId())))
                .containsSame(updated);

        Model third = new Model(UUID.randomUUID());
        store.upsert(third);

        assertThat(store.getFirst(Model.class)).containsSame(updated);
        assertThat(store.getLast(Model.class)).containsSame(third);
        assertThat(store.find(Model.class, model -> model.getId().equals(second.getId())))
                .containsSame(second);
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
