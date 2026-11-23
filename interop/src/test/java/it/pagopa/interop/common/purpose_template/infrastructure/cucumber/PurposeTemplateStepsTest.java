package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import io.cucumber.datatable.DataTable;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PurposeTemplateStepsTest {

    @ParameterizedTest
    @EnumSource(value = Tenant.class, names = {"COMUNE_DI_MILANO", "PAGO_PA"})
    void createsTemplateInRequestedStateForPublicAdministrationsAsTheRequestedCreator(Tenant creator) {
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var entityStore = mock(EntityStore.class);
        var useCase = mock(PurposeTemplateUseCase.class);
        var steps = new PurposeTemplateSteps(entityStore, journey, useCase);

        steps.createPurposeTemplate(creator, PurposeTemplateState.SUSPENDED);

        var order = inOrder(journey);
        order.verify(journey).withProducer(creator, UserRole.ADMIN);
        order.verify(journey).createPurposeTemplate(
                Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.SUSPENDED);
        verifyNoMoreInteractions(journey);
    }

    @Test
    void publishedStepDelegatesToGenericCreationStep() {
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var steps = new PurposeTemplateSteps(
                mock(EntityStore.class), journey, mock(PurposeTemplateUseCase.class));

        steps.createPurposeTemplate(Tenant.COMUNE_DI_MILANO);

        var order = inOrder(journey);
        order.verify(journey).withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        order.verify(journey).createPurposeTemplate(
                Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.PUBLISHED);
        verifyNoMoreInteractions(journey);
    }

    @Test
    void savesARefreshedCopyOfTheCurrentPurposeTemplateAsReference() {
        var entityStore = mock(EntityStore.class);
        var useCase = mock(PurposeTemplateUseCase.class);
        var steps = new PurposeTemplateSteps(entityStore, mock(InteropJourney.class), useCase);
        var storedTemplate = PurposeTemplate.builder().id(UUID.randomUUID()).title("old").build();
        var refreshedTemplate = storedTemplate.toBuilder().title("current").build();
        when(entityStore.getLastOrThrow(PurposeTemplate.class)).thenReturn(storedTemplate);
        when(useCase.getPurposeTemplate(storedTemplate)).thenReturn(refreshedTemplate);

        steps.savePurposeTemplateReference();

        verify(entityStore).getLastOrThrow(PurposeTemplate.class);
        verify(useCase).getPurposeTemplate(storedTemplate);
        verifyNoMoreInteractions(entityStore, useCase);
    }

    @Test
    void rejectsUnsupportedMetadataFieldsBeforeChangingTheTemplate() {
        var entityStore = mock(EntityStore.class);
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var useCase = mock(PurposeTemplateUseCase.class);
        var template = PurposeTemplate.builder()
                .id(UUID.randomUUID())
                .title("Titolo iniziale")
                .description("Descrizione iniziale")
                .targetDescription("Destinatari iniziali")
                .build();
        when(entityStore.getLastOrThrow(PurposeTemplate.class)).thenReturn(template);
        var steps = new PurposeTemplateSteps(entityStore, journey, useCase);
        DataTable dataTable = DataTable.create(List.of(
                List.of("campo", "valore unico"),
                List.of("non supportato", "valore unico")
        ));

        assertThatThrownBy(() ->
                steps.updatePurposeTemplateMetadata(UserRole.ADMIN, Tenant.COMUNE_DI_POZZALLO, dataTable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non supportato: non supportato");

        verifyNoInteractions(journey, useCase);
    }

    @Test
    void mapsPurposeTemplateMetadataFieldsFromTheDataTable() {
        var entityStore = mock(EntityStore.class);
        var journey = mock(InteropJourney.class, RETURNS_SELF);
        var useCase = mock(PurposeTemplateUseCase.class);
        var template = PurposeTemplate.builder()
                .id(UUID.randomUUID())
                .title("Titolo iniziale")
                .description("Descrizione iniziale")
                .targetDescription("Destinatari iniziali")
                .build();
        when(entityStore.getLastOrThrow(PurposeTemplate.class)).thenReturn(template);
        var steps = new PurposeTemplateSteps(entityStore, journey, useCase);
        DataTable dataTable = DataTable.create(List.of(
                List.of("campo", "valore"),
                List.of("purposeTitle", "Titolo aggiornato"),
                List.of("purposeDescription", "Descrizione aggiornata"),
                List.of("targetDescription", "Destinatari aggiornati")
        ));

        steps.updatePurposeTemplateMetadata(UserRole.ADMIN, Tenant.COMUNE_DI_POZZALLO, dataTable);

        verify(journey).withConsumer(Tenant.COMUNE_DI_POZZALLO);
        verify(useCase).updateMetadata(
                template, "Titolo aggiornato", "Descrizione aggiornata", "Destinatari aggiornati");
    }
}
