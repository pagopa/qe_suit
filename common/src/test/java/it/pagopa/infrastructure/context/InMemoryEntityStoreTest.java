package it.pagopa.infrastructure.context;

import it.pagopa.domain.Identifiable;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryEntityStoreTest {
    private final InMemoryEntityStore store = new InMemoryEntityStore();

    @Test
    void registersAliasAndResolvesLatestEntityAfterUpdatesWithoutAlias() {
        Model original = new Model(UUID.randomUUID());
        store.upsert("ES-1", original);
        Model updated = new Model(original.getId());
        store.upsert(updated);

        assertThat(store.getByAlias("ES-1", Model.class)).containsSame(updated);
        assertThat(store.getFirst(Model.class)).containsSame(updated);
        assertThat(store.getLast(Model.class)).containsSame(updated);
    }

    @Test
    void registersAliasForExistingEntityAndAllowsRepeatedAssociation() {
        Model model = new Model(UUID.randomUUID());
        store.upsert(model);
        store.upsert("ES-1", model);
        store.upsert("ES-1", model);

        assertThat(store.getByAlias("ES-1", Model.class)).containsSame(model);
        assertThat(store.getById(model.getId(), Model.class)).containsSame(model);
    }

    @Test
    void rejectsDuplicateAliasBeforeInsertingOrUpdatingEntity() {
        Model first = new Model(UUID.randomUUID());
        Model second = new Model(UUID.randomUUID());
        store.upsert("ES-1", first);

        assertThatThrownBy(() -> store.upsert("ES-1", second))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("ES-1");
        assertThat(store.getById(second.getId(), Model.class)).isEmpty();

        store.upsert(second);
        Model replacement = new Model(second.getId());
        assertThatThrownBy(() -> store.upsert("ES-1", replacement))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(store.getById(second.getId(), Model.class)).containsSame(second);
        assertThat(store.getByAlias("ES-1", Model.class)).containsSame(first);
    }

    @Test
    void keepsAliasesSeparateByType() {
        Model model = new Model(UUID.randomUUID());
        OtherModel other = new OtherModel(UUID.randomUUID());
        store.upsert("same-alias", model);
        store.upsert("same-alias", other);

        assertThat(store.getByAlias("same-alias", Model.class)).containsSame(model);
        assertThat(store.getByAlias("same-alias", OtherModel.class)).containsSame(other);
        assertThat(store.getByAlias("unknown", Model.class)).isEmpty();
    }

    @Test
    void rejectsBlankAliasesAndMissingIdsWithoutSaving() {
        Model model = new Model(UUID.randomUUID());
        for (String alias : new String[]{"", " ", "\t"}) {
            assertThatThrownBy(() -> store.upsert(alias, model))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        assertThat(store.getFirst(Model.class)).isEmpty();
        assertThatThrownBy(() -> store.upsert("ES-1", new Model(null)))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("model.id");
        assertThat(store.getByAlias("ES-1", Model.class)).isEmpty();
    }

    @Test
    void preservesInsertionOrderOnUpdate() {
        Model first = new Model(UUID.randomUUID());
        Model second = new Model(UUID.randomUUID());
        store.upsert("ES-1", first);
        store.upsert(second);
        Model updatedFirst = new Model(first.getId());
        store.upsert("ES-1", updatedFirst);

        assertThat(store.getFirst(Model.class)).containsSame(updatedFirst);
        assertThat(store.getLast(Model.class)).containsSame(second);
    }

    @Test
    void isolatesAliasesAndEntitiesBetweenThreads() throws Exception {
        Model model = new Model(UUID.randomUUID());
        store.upsert("ES-1", model);
        var executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(() -> {
                assertThat(store.getByAlias("ES-1", Model.class)).isEmpty();
                assertThat(store.getById(model.getId(), Model.class)).isEmpty();
                Model other = new Model(UUID.randomUUID());
                store.upsert("ES-1", other);
                assertThat(store.getByAlias("ES-1", Model.class)).containsSame(other);
            }).get();
        } finally {
            executor.shutdownNow();
        }
        assertThat(store.getByAlias("ES-1", Model.class)).containsSame(model);
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

    private static class OtherModel extends Model {
        OtherModel(UUID id) {
            super(id);
        }
    }
}
