package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurposeTemplateUseCase {

    private final PurposeTemplateGateway purposeTemplateGateway;
    private final RiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;

    /**
     * Crea (con l'identità corrente come creatore) un purpose template DRAFT per il tenant target,
     * con risposte di riferimento e configurazione per distinguere suggerimenti e valori fissi.
     */
    public PurposeTemplate createPurposeTemplate(Tenant targetTenant) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(targetTenant);
        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(riskAnalysisDataFactory.getTemplateForTenant(targetTenant, true))
                .build();

        return purposeTemplateGateway.createPurposeTemplate(targetTenant, form, latestConfig);
    }

    public PurposeTemplate publishPurposeTemplate(PurposeTemplate purposeTemplate) {
        return purposeTemplateGateway.publishPurposeTemplate(purposeTemplate);
    }

    public PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EService eService) {
        var templateRef = eService.getLatestDescriptor().getTemplateRef();
        if (templateRef != null) {
            return purposeTemplateGateway.linkEServiceTemplate(purposeTemplate, templateRef);
        }
        return purposeTemplateGateway.linkEService(purposeTemplate, eService.getRef());
    }
}
