package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import org.junit.jupiter.api.Test;
import it.pagopa.interop.common.purpose_template.infrastructure.cucumber.dto.PurposeTemplateDocumentConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PurposeTemplateStepsTest {

    private final InteropJourney journey = mock(InteropJourney.class, RETURNS_SELF);
    private final PurposeTemplateUseCase useCase = mock(PurposeTemplateUseCase.class);
    private final EntityStore entityStore = mock(EntityStore.class);
    private final PurposeTemplateSteps steps = new PurposeTemplateSteps(journey, useCase, entityStore);

    @ParameterizedTest
    @EnumSource(value = Tenant.class, names = {"COMUNE_DI_MILANO", "PAGO_PA"})
    void createsAPublishedTemplateForPublicAdministrationsAsTheRequestedCreator(Tenant creator) {
        steps.createPurposeTemplate(creator);

        var order = inOrder(journey);
        order.verify(journey).withProducer(creator);
        order.verify(journey).createPurposeTemplate(
                Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.PUBLISHED);
        verifyNoMoreInteractions(journey);
    }

    @Test
    void delegatesReactivationWithoutChangingTheActorOrChannel() {
        var journey = mock(InteropJourney.class);
        var useCase = mock(PurposeTemplateUseCase.class);
        var template = PurposeTemplate.builder().build();

        new PurposeTemplateSteps(journey, useCase, entityStore).attemptPurposeTemplateReactivation(template);

        verify(useCase).attemptPurposeTemplateReactivation(template);
        verifyNoMoreInteractions(useCase);
        verifyNoInteractions(journey);
    }

    @Test
    void delegatesPublicationWithoutChangingTheActorOrChannel() {
        var journey = mock(InteropJourney.class);
        var useCase = mock(PurposeTemplateUseCase.class);
        var template = PurposeTemplate.builder().build();

        new PurposeTemplateSteps(journey, useCase, entityStore).attemptPurposeTemplatePublication(template);

        verify(useCase).attemptPurposeTemplatePublication(template);
        verifyNoMoreInteractions(useCase);
        verifyNoInteractions(journey);
    }

    @Test
    void createsDraftTemplateWithAnnotatedAnswer() {
        steps.draftPurposeTemplateWithAnnotatedAnswer();

        var order = inOrder(journey);
        order.verify(journey).createPurposeTemplate(
                Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.DRAFT);
        order.verify(journey).addAnnotationToPurposeTemplateRiskAnalysisAnswer();
        verifyNoMoreInteractions(journey);
    }

    @Test
    void attachesPdfDocumentsToPurposeTemplateAnnotation() {
        var template = PurposeTemplate.builder().id(UUID.randomUUID()).build();
        var updated = PurposeTemplate.builder().id(template.getId()).build();
        when(entityStore.getLastOrThrow(PurposeTemplate.class)).thenReturn(template);
        when(useCase.addRiskAnalysisAnswerAnnotationDocument(template, "Allegato A", "allegato-a.pdf"))
                .thenReturn(updated);

        var docA = new PurposeTemplateDocumentConfiguration();
        docA.setName("Allegato A");
        docA.setFixture("allegato-a.pdf");
        var docB = new PurposeTemplateDocumentConfiguration();
        docB.setName("Allegato B");
        docB.setFixture("allegato-b.pdf");

        steps.pdfDocumentsAttachedToPurposeTemplateAnnotation(List.of(docA, docB));

        verify(useCase).addRiskAnalysisAnswerAnnotationDocument(template, "Allegato A", "allegato-a.pdf");
        verify(useCase).addRiskAnalysisAnswerAnnotationDocument(updated, "Allegato B", "allegato-b.pdf");
    }

    @Test
    void createsDraftTemplateWithQuestionPreparedForAnnotations() {
        steps.draftPurposeTemplateWithQuestionPreparedForAnnotations();

        verify(journey).createPurposeTemplate(
                Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.DRAFT);
        verifyNoMoreInteractions(journey);
    }

    @Test
    void waitsForSignedRiskAnalysisDocumentAvailability() {
        var template = PurposeTemplate.builder().id(UUID.randomUUID()).build();
        when(entityStore.getLastOrThrow(PurposeTemplate.class)).thenReturn(template);

        steps.signedRiskAnalysisDocumentForPurposeTemplateAvailable();

        verify(useCase).awaitSignedRiskAnalysisDocumentAvailable(template);
    }
}
