package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceRiskAnalysisUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceRiskAnalysisAliasConfiguration;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EServiceRiskAnalysisStepsTest {
    private final InteropJourney interopJourney = mock(InteropJourney.class, RETURNS_SELF);
    private final EntityStore entityStore = mock(EntityStore.class);
    private final EServiceRiskAnalysisUseCase riskAnalysisUseCase = mock(EServiceRiskAnalysisUseCase.class);
    private final AuthSession authSession = mock(AuthSession.class);
    private final EServiceRiskAnalysisSteps steps = new EServiceRiskAnalysisSteps(
            interopJourney, entityStore, riskAnalysisUseCase, authSession
    );

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

    @Test
    void attemptsToAddRiskAnalysisWithFreeTextAnswer() {
        var eService = EService.builder().id(UUID.randomUUID()).build();
        when(entityStore.getLastOrThrow(EService.class)).thenReturn(eService);
        when(authSession.getTenant()).thenReturn(Tenant.COMUNE_DI_MILANO);

        steps.attemptAddRiskAnalysisWithFreeTextAnswer("Motivazione https://example.org");

        verify(riskAnalysisUseCase).attemptAddRiskAnalysisWithFreeTextAnswer(
                Tenant.COMUNE_DI_MILANO, eService, "Motivazione https://example.org"
        );
    }

    @Test
    void attemptsToRenameRiskAnalysisWithTargetAnalysisName() {
        var eService = EService.builder().id(UUID.randomUUID()).build();
        var source = EServiceRiskAnalysis.builder().id(UUID.randomUUID()).name("Analisi B").build();
        var target = EServiceRiskAnalysis.builder().id(UUID.randomUUID()).name("Analisi A").build();
        when(entityStore.getLastOrThrow(EService.class)).thenReturn(eService);
        when(entityStore.getByAliasOrThrow("AR-2", EServiceRiskAnalysis.class)).thenReturn(source);
        when(entityStore.getByAliasOrThrow("AR-1", EServiceRiskAnalysis.class)).thenReturn(target);
        when(authSession.getTenant()).thenReturn(Tenant.COMUNE_DI_MILANO);

        steps.attemptRenameEServiceRiskAnalysis("AR-2", "AR-1");

        verify(riskAnalysisUseCase).attemptRenameRiskAnalysis(
                Tenant.COMUNE_DI_MILANO, eService, source, "Analisi A"
        );
    }

    private static EServiceRiskAnalysisAliasConfiguration config(String alias, String name) {
        var cfg = new EServiceRiskAnalysisAliasConfiguration();
        cfg.setAlias(alias);
        cfg.setName(name);
        return cfg;
    }
}
