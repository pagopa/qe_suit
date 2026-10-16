package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.application.context.EntityStore;
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
    private final EntityStore entityStore;

    /**
     * Crea (con l'identità corrente come creatore) un purpose template DRAFT per il tenant target,
     * con risk analysis completata.
     */
    public PurposeTemplate createPurposeTemplate(Tenant targetTenant) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(targetTenant);
        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(riskAnalysisDataFactory.getTemplateForTenant(targetTenant, true))
                .build();

        return store(purposeTemplateGateway.createPurposeTemplate(targetTenant, form));
    }

    public PurposeTemplate publishPurposeTemplate(PurposeTemplate purposeTemplate) {
        return store(purposeTemplateGateway.publishPurposeTemplate(purposeTemplate));
    }

    public PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EService eService) {
        return store(purposeTemplateGateway.linkEService(purposeTemplate, eService.getRef()));
    }

    private PurposeTemplate store(PurposeTemplate purposeTemplate) {
        entityStore.upsert(purposeTemplate);
        return purposeTemplate;
    }
}

