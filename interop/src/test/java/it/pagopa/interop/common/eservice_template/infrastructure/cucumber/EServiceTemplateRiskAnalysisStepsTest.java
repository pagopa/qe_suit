package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import it.pagopa.interop.common.journey.application.InteropJourney;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class EServiceTemplateRiskAnalysisStepsTest {
    private final InteropJourney interopJourney = mock(InteropJourney.class, RETURNS_SELF);
    private final EServiceTemplateRiskAnalysisSteps steps = new EServiceTemplateRiskAnalysisSteps(interopJourney);

    @Test
    void addsCompletedRiskAnalysisToTemplateViaJourney() {
        steps.completeRiskAnalysisAssociatedToTemplate();

        verify(interopJourney).addTemplateRiskAnalysis();
        verifyNoMoreInteractions(interopJourney);
    }
}
