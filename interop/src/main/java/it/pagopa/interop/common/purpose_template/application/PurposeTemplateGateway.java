package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import org.springframework.plugin.core.Plugin;

import java.util.UUID;

public interface PurposeTemplateGateway extends Plugin<Channel> {

    PurposeTemplate getPurposeTemplate(UUID purposeTemplateId);

    PurposeTemplate createPurposeTemplate(
            Tenant targetTenant, RiskAnalysisForm riskAnalysisForm, RiskAnalysisFormConfig config);

    PurposeTemplate createPurposeTemplate(Tenant targetTenant, RiskAnalysisForm riskAnalysisForm);

    PurposeTemplate publishPurposeTemplate(PurposeTemplate purposeTemplate);

    PurposeTemplate suspendPurposeTemplate(PurposeTemplate purposeTemplate);

    PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef);

    PurposeTemplate linkEServiceTemplate(PurposeTemplate purposeTemplate, EServiceTemplateRef templateRef);

    void attemptPurposeTemplateReactivation(PurposeTemplate purposeTemplate);

    void attemptPurposeTemplatePublication(PurposeTemplate purposeTemplate);

    PurposeTemplate addRiskAnalysisAnswerAnnotation(PurposeTemplate purposeTemplate, UUID answerId);

    PurposeTemplate addRiskAnalysisAnswerAnnotationDocument(
            PurposeTemplate purposeTemplate,
            UUID answerId,
            String prettyName,
            String fixturePath
    );

    void awaitSignedRiskAnalysisDocumentAvailable(PurposeTemplate purposeTemplate);

    void attemptRiskAnalysisAnswerAnnotationDocumentUpload(
            PurposeTemplate purposeTemplate,
            UUID answerId,
            String prettyName,
            String fixturePath
    );

    void attemptLinkEService(PurposeTemplate purposeTemplate, EServiceRef eServiceRef);

    void attemptUpdatePurposeTemplate(
            PurposeTemplate purposeTemplate,
            TenantKind targetTenantKind,
            RiskAnalysisForm riskAnalysisForm
    );

    void attemptAddRiskAnalysisAnswerWithAnnotation(
            PurposeTemplate purposeTemplate,
            String annotationText
    );

    void attemptUpdateRiskAnalysisAnswerAnnotation(
            PurposeTemplate purposeTemplate,
            UUID answerId,
            String annotationText
    );

    void attemptRenameRiskAnalysisAnswerAnnotationDocument(
            PurposeTemplate purposeTemplate,
            UUID answerId,
            UUID documentId,
            String newPrettyName
    );

    void attemptSignedRiskAnalysisDocumentDownload(PurposeTemplate purposeTemplate);

}
