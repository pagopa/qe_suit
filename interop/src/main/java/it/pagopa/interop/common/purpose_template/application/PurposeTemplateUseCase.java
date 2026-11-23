package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PurposeTemplateUseCase {

    private final PurposeTemplateGateway purposeTemplateGateway;
    private final RiskAnalysisGateway riskAnalysisGateway;
    private final RiskAnalysisDataFactory riskAnalysisDataFactory;

    public PurposeTemplate getPurposeTemplate(PurposeTemplate purposeTemplate) {
        return purposeTemplateGateway.getPurposeTemplate(purposeTemplate.getId());
    }

    public PurposeTemplate updateMetadata(
            PurposeTemplate purposeTemplate,
            String name,
            String description,
            String targetDescription,
            Boolean purposeIsFreeOfCharge,
            Integer purposeDailyCalls,
            Boolean handlesPersonalData) {
        purposeTemplateGateway.updateMetadata(
                purposeTemplate,
                name,
                description,
                targetDescription,
                purposeIsFreeOfCharge,
                purposeDailyCalls,
                handlesPersonalData);

        return PollingUtils.pollUntil(
                () -> getPurposeTemplate(purposeTemplate),
                refreshed -> Objects.equals(name, refreshed.getTitle())
                        && Objects.equals(description, refreshed.getDescription())
                        && Objects.equals(targetDescription, refreshed.getTargetDescription())
                        && (purposeIsFreeOfCharge == null
                                || Objects.equals(purposeIsFreeOfCharge, refreshed.getPurposeIsFreeOfCharge()))
                        && (purposeDailyCalls == null
                                || Objects.equals(purposeDailyCalls, refreshed.getPurposeDailyCalls()))
                        && (handlesPersonalData == null
                                || Objects.equals(handlesPersonalData, refreshed.getHandlesPersonalData())),
                "Purpose template " + purposeTemplate.getId()
                        + " metadata did not reach the requested values"
        );
    }

    public void tryToUpdateMetadata(
            PurposeTemplate purposeTemplate,
            String name,
            String description,
            String targetDescription,
            Boolean purposeIsFreeOfCharge,
            Integer purposeDailyCalls,
            Boolean handlesPersonalData) {
        purposeTemplateGateway.tryToUpdateMetadata(
                purposeTemplate,
                name,
                description,
                targetDescription,
                purposeIsFreeOfCharge,
                purposeDailyCalls,
                handlesPersonalData);
    }

    public void shouldFailToUpdateMetadataAsMalformed() {
        purposeTemplateGateway.shouldFailToUpdateMetadataAsMalformed();
    }

    public void shouldFailToUpdateMetadataAsForbidden() {
        purposeTemplateGateway.shouldFailToUpdateMetadataAsForbidden();
    }

    public void shouldFailToUpdateMetadataAsConflict() {
        purposeTemplateGateway.shouldFailToUpdateMetadataAsConflict();
    }

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

    public PurposeTemplate suspendPurposeTemplate(PurposeTemplate purposeTemplate) {
        return purposeTemplateGateway.suspendPurposeTemplate(purposeTemplate);
    }

    public PurposeTemplate archivePurposeTemplate(PurposeTemplate purposeTemplate) {
        return purposeTemplateGateway.archivePurposeTemplate(purposeTemplate);
    }

    public PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EService eService) {
        var templateRef = eService.getLatestDescriptor().getTemplateRef();
        if (templateRef != null) {
            return purposeTemplateGateway.linkEServiceTemplate(purposeTemplate, templateRef);
        }
        return purposeTemplateGateway.linkEService(purposeTemplate, eService.getRef());
    }
}
