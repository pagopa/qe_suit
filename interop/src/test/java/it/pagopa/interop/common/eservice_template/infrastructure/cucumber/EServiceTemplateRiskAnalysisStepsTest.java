package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class EServiceTemplateRiskAnalysisStepsTest {
    private final InteropJourney interopJourney = mock(InteropJourney.class, RETURNS_SELF);
    private final EntityStore entityStore = mock(EntityStore.class);
    private final EServiceTemplateUseCase templateUseCase = mock(EServiceTemplateUseCase.class);
    private final AuthSession authSession = mock(AuthSession.class);
    private final EServiceTemplateRiskAnalysisSteps steps = new EServiceTemplateRiskAnalysisSteps(
            interopJourney, entityStore, templateUseCase, authSession
    );

    @Test
    void addsCompletedRiskAnalysisToTemplateViaJourney() {
        steps.completeRiskAnalysisAssociatedToTemplate();

        verify(interopJourney).addTemplateRiskAnalysis();
        verifyNoMoreInteractions(interopJourney);
    }

    @Test
    void attemptsToAddTemplateRiskAnalysisWithExistingName() {
        var existingAnalysis = EServiceRiskAnalysis.builder()
                .id(UUID.randomUUID())
                .name("Analisi esistente")
                .build();
        var template = EServiceTemplate.builder()
                .id(UUID.randomUUID())
                .riskAnalyses(List.of(existingAnalysis))
                .build();
        when(entityStore.getLastOrThrow(EServiceTemplate.class)).thenReturn(template);
        when(authSession.getTenant()).thenReturn(Tenant.COMUNE_DI_MILANO);

        steps.attemptAddTemplateRiskAnalysisWithExistingName();

        verify(templateUseCase).attemptAddRiskAnalysisWithName(
                Tenant.COMUNE_DI_MILANO, template, "Analisi esistente"
        );
    }
}
