package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EServiceDescriptorParameterTypesTest {
    private final EServiceDescriptorParameterTypes parameterTypes =
            new EServiceDescriptorParameterTypes(new InMemoryEntityStore());

    @Test
    void mapsAllCatalogDescriptorStateWordsWithoutInventingTransitions() {
        Map<String, EServiceDescriptorState> states = Map.of(
                "in bozza", EServiceDescriptorState.DRAFT,
                "pubblicata", EServiceDescriptorState.PUBLISHED,
                "sospesa", EServiceDescriptorState.SUSPENDED,
                "deprecata", EServiceDescriptorState.DEPRECATED,
                "in attesa di approvazione", EServiceDescriptorState.WAITING_FOR_APPROVAL,
                "in archiviazione", EServiceDescriptorState.ARCHIVING,
                "in archiviazione sospesa", EServiceDescriptorState.ARCHIVING_SUSPENDED,
                "archiviata", EServiceDescriptorState.ARCHIVED
        );

        states.forEach((text, expected) ->
                assertThat(parameterTypes.eServiceDescriptorState(text)).isEqualTo(expected));
        assertThatThrownBy(() -> parameterTypes.eServiceDescriptorState("sconosciuta"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
