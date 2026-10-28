package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PurposeTemplateUseCaseTest {
    @Test
    void linksTheOriginTemplateForAnInstanceInsteadOfTheEService() {
        var gateway = mock(PurposeTemplateGateway.class);
        var useCase = new PurposeTemplateUseCase(
                gateway, mock(RiskAnalysisGateway.class), mock(RiskAnalysisDataFactory.class));
        var templateRef = new EServiceTemplateRef(UUID.randomUUID());
        var service = EService.builder().id(UUID.randomUUID()).descriptors(List.of(
                EServiceDescriptor.builder().version("1").templateRef(templateRef).build(),
                EServiceDescriptor.builder().version("2").templateRef(templateRef).build())).build();
        var purpose = PurposeTemplate.builder().id(UUID.randomUUID()).build();
        when(gateway.linkEServiceTemplate(purpose, templateRef)).thenReturn(purpose);

        assertThat(useCase.linkEService(purpose, service)).isSameAs(purpose);

        verify(gateway).linkEServiceTemplate(purpose, templateRef);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void linksAnOrdinaryEServiceDirectly() {
        var gateway = mock(PurposeTemplateGateway.class);
        var useCase = new PurposeTemplateUseCase(
                gateway, mock(RiskAnalysisGateway.class), mock(RiskAnalysisDataFactory.class));
        var service = EService.builder().id(UUID.randomUUID()).descriptors(List.of(
                EServiceDescriptor.builder().version("1").build())).build();
        var purpose = PurposeTemplate.builder().id(UUID.randomUUID()).build();
        when(gateway.linkEService(purpose, service.getRef())).thenReturn(purpose);

        assertThat(useCase.linkEService(purpose, service)).isSameAs(purpose);

        verify(gateway).linkEService(purpose, service.getRef());
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void passesTheSameLatestConfigurationAndMatchingAnswersToTheGateway() {
        var gateway = mock(PurposeTemplateGateway.class);
        var configs = mock(RiskAnalysisGateway.class);
        var data = mock(RiskAnalysisDataFactory.class);
        var config = RiskAnalysisFormConfig.builder().version("2.1").questions(List.of()).build();
        Map<String, List<String>> answers = Map.of("institutionalPurpose", List.of("test purpose"));
        when(configs.getLatestRiskAnalysisConfig(Tenant.PAGO_PA)).thenReturn(config);
        when(data.getTemplateForTenant(Tenant.PAGO_PA, true)).thenReturn(answers);
        var expected = mock(PurposeTemplate.class);
        when(gateway.createPurposeTemplate(eq(Tenant.PAGO_PA), any(), same(config))).thenReturn(expected);

        var result = new PurposeTemplateUseCase(gateway, configs, data).createPurposeTemplate(Tenant.PAGO_PA);

        assertThat(result).isSameAs(expected);
        var form = ArgumentCaptor.forClass(RiskAnalysisForm.class);
        verify(gateway).createPurposeTemplate(eq(Tenant.PAGO_PA), form.capture(), same(config));
        assertThat(form.getValue().getVersion()).isEqualTo("2.1");
        assertThat(form.getValue().getAnswers()).isEqualTo(answers);
    }
}
