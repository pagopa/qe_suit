package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceRiskAnalysisAliasConfiguration;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EServiceRiskAnalysisStepsTest {
    private final InteropJourney interopJourney = mock(InteropJourney.class, RETURNS_SELF);
    private final EntityStore entityStore = mock(EntityStore.class);
    private final EServiceRiskAnalysisSteps steps = new EServiceRiskAnalysisSteps(interopJourney, entityStore);

    @Test
    void addsCompletedRiskAnalysesWithConfiguredNamesAndAliases() {
        when(entityStore.getByAlias(anyString(), eq(EServiceRiskAnalysis.class))).thenReturn(Optional.empty());

        steps.completeRiskAnalysesAssociatedToEService(List.of(
                config("AR-1", "Analisi A"),
                config("AR-2", "Analisi B")
        ));

        var order = inOrder(interopJourney);
        order.verify(interopJourney).addRiskAnalysis("Analisi A", "AR-1");
        order.verify(interopJourney).addRiskAnalysis("Analisi B", "AR-2");
        verifyNoMoreInteractions(interopJourney);
    }

    @Test
    void rejectsDuplicateAliasesBeforeExecuting() {
        assertThatThrownBy(() -> steps.completeRiskAnalysesAssociatedToEService(List.of(
                config("AR-1", "Analisi A"),
                config("AR-1", "Analisi B")
        ))).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unique");

        verifyNoInteractions(interopJourney);
    }

    private static EServiceRiskAnalysisAliasConfiguration config(String alias, String name) {
        var cfg = new EServiceRiskAnalysisAliasConfiguration();
        cfg.setAlias(alias);
        cfg.setName(name);
        return cfg;
    }
}
