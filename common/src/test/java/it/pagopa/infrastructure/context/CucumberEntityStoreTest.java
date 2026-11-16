package it.pagopa.infrastructure.context;

import it.pagopa.domain.Identifiable;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CucumberEntityStoreTest {

    @Test
    void preservesAliasAndResolvesUpdatedEntityWhenUpsertedWithoutAlias() {
        CucumberEntityStore store = new CucumberEntityStore();
        Model original = new Model(UUID.randomUUID(), "original");
        store.upsert("ES-1", original);

        Model updated = new Model(original.getId(), "updated");
        store.upsert(updated);

        assertThat(store.getByAlias("ES-1", Model.class)).containsSame(updated);
        assertThat(store.getById(original.getId(), Model.class)).containsSame(updated);
    }

    @Test
    void replacesAliasWhenEntityIsUpsertedWithANewAlias() {
        CucumberEntityStore store = new CucumberEntityStore();
        Model model = new Model(UUID.randomUUID(), "initial");
        store.upsert("ES-1", model);

        Model updated = new Model(model.getId(), "updated");
        store.upsert("ES-2", updated);

        assertThat(store.getByAlias("ES-1", Model.class)).isEmpty();
        assertThat(store.getByAlias("ES-2", Model.class)).containsSame(updated);
    }

    private static final class Model implements Identifiable {
        private final UUID id;
        private final String value;

        private Model(UUID id, String value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public UUID getId() {
            return id;
        }
    }
}
