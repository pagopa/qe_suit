package it.pagopa.interop.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PurposeTemplateJourneyImplTest {

    private final PurposeTemplateUseCase useCase = mock(PurposeTemplateUseCase.class);
    private final EntityStore entityStore = mock(EntityStore.class);
    private final PurposeTemplateJourneyImpl journey = new PurposeTemplateJourneyImpl(useCase, entityStore);

    @Test
    void createsDraftPurposeTemplateAndRegistersAliasAgainstStoredSnapshot() {
        UUID id = UUID.randomUUID();
        PurposeTemplate returned = PurposeTemplate.builder().id(id).title("returned").build();
        PurposeTemplate stored = PurposeTemplate.builder().id(id).title("stored").build();
        when(useCase.createPurposeTemplate(Tenant.PAGO_PA)).thenReturn(returned);
        when(entityStore.getById(id, PurposeTemplate.class)).thenReturn(Optional.of(stored));

        journey.createPurposeTemplate(Tenant.PAGO_PA, PurposeTemplateState.DRAFT, "TF-1");

        verify(useCase).createPurposeTemplate(Tenant.PAGO_PA);
        verify(entityStore).upsert("TF-1", stored);
        verifyNoMoreInteractions(useCase);
    }

    @Test
    void createsPurposeTemplateWithoutAliasRegistrationWhenAliasIsNull() {
        UUID id = UUID.randomUUID();
        PurposeTemplate returned = PurposeTemplate.builder().id(id).build();
        when(useCase.createPurposeTemplate(Tenant.PAGO_PA)).thenReturn(returned);

        journey.createPurposeTemplate(Tenant.PAGO_PA, PurposeTemplateState.DRAFT);

        verify(useCase).createPurposeTemplate(Tenant.PAGO_PA);
        verifyNoInteractions(entityStore);
    }
}
