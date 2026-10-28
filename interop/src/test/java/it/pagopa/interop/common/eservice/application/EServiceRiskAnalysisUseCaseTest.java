package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.kernel.domain.EServiceRiskAnalysis;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceRiskAnalysisUseCaseTest {
    private final RiskAnalysisGateway configs = mock(RiskAnalysisGateway.class);
    private final EServiceRiskAnalysisGateway analyses = mock(EServiceRiskAnalysisGateway.class);
    private final RiskAnalysisDataFactory data = mock(RiskAnalysisDataFactory.class);
    private final EServiceRiskAnalysisUseCase useCase = new EServiceRiskAnalysisUseCase(configs, analyses, data);
    private final EService service = EService.builder().id(UUID.randomUUID()).mode(EServiceMode.RECEIVE)
            .producerId(Tenant.COMUNE_DI_MILANO.getOrganizationId().toString()).build();

    @Test
    void deliverDoesNotReadOrCreateRiskAnalyses() {
        useCase.ensureForPublication(service.toBuilder().mode(EServiceMode.DELIVER).build());

        verifyNoInteractions(configs, analyses, data);
    }

    @Test
    void receiveReusesAnalysisAssociatedWithTheExactEservice() {
        when(analyses.getRiskAnalysis(service.getRef()))
                .thenReturn(EServiceRiskAnalysis.builder().id(UUID.randomUUID()).build());

        useCase.ensureForPublication(service);

        verify(analyses).getRiskAnalysis(service.getRef());
        verifyNoMoreInteractions(analyses);
        verifyNoInteractions(configs, data);
    }

    @Test
    void receiveCreatesCompletedAnalysisUsingTheProducersLatestConfiguration() {
        var config = mock(RiskAnalysisFormConfig.class);
        when(config.getVersion()).thenReturn("3.0");
        when(configs.getLatestRiskAnalysisConfig(Tenant.COMUNE_DI_MILANO)).thenReturn(config);
        Map<String, List<String>> answers = Map.of("purpose", List.of("test"));
        when(data.getTemplateForTenant(Tenant.COMUNE_DI_MILANO, true)).thenReturn(answers);

        useCase.ensureForPublication(service);

        var form = ArgumentCaptor.forClass(RiskAnalysisForm.class);
        var order = inOrder(analyses, configs, data);
        order.verify(analyses).getRiskAnalysis(service.getRef());
        order.verify(configs).getLatestRiskAnalysisConfig(Tenant.COMUNE_DI_MILANO);
        order.verify(data).getTemplateForTenant(Tenant.COMUNE_DI_MILANO, true);
        order.verify(analyses).addRiskAnalysis(eq(service.getRef()), form.capture());
        assertThat(form.getValue().getVersion()).isEqualTo("3.0");
        assertThat(form.getValue().getAnswers()).isEqualTo(answers);
        order.verifyNoMoreInteractions();
    }

    @Test
    void readFailureIsNotTreatedAsAMissingAnalysis() {
        var failure = new IllegalStateException("risk analysis lookup failed");
        when(analyses.getRiskAnalysis(service.getRef())).thenThrow(failure);

        assertThatThrownBy(() -> useCase.ensureForPublication(service)).isSameAs(failure);

        verify(analyses).getRiskAnalysis(service.getRef());
        verifyNoMoreInteractions(analyses);
        verifyNoInteractions(configs, data);
    }
}
