package it.pagopa.interop.common.purpose_template.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.domain.Document;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisDataFactory;
import it.pagopa.interop.common.risk_analysis.application.RiskAnalysisGateway;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisFormConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public PurposeTemplate suspendPurposeTemplate(PurposeTemplate purposeTemplate) {
        return purposeTemplateGateway.suspendPurposeTemplate(purposeTemplate);
    }

    public PurposeTemplate linkEService(PurposeTemplate purposeTemplate, EService eService) {
        var templateRef = eService.getLatestDescriptor().getTemplateRef();
        if (templateRef != null) {
            return purposeTemplateGateway.linkEServiceTemplate(purposeTemplate, templateRef);
        }
        return purposeTemplateGateway.linkEService(purposeTemplate, eService.getRef());
    }

    public void attemptPurposeTemplateReactivation(PurposeTemplate purposeTemplate) {
        purposeTemplateGateway.attemptPurposeTemplateReactivation(purposeTemplate);
    }

    public void attemptPurposeTemplatePublication(PurposeTemplate purposeTemplate) {
        purposeTemplateGateway.attemptPurposeTemplatePublication(purposeTemplate);
    }

    public PurposeTemplate addRiskAnalysisAnswerAnnotation(PurposeTemplate purposeTemplate) {
        UUID answerId = purposeTemplate.getFirstRiskAnalysisAnswerId();
        return purposeTemplateGateway.addRiskAnalysisAnswerAnnotation(purposeTemplate, answerId);
    }

    public PurposeTemplate addRiskAnalysisAnswerAnnotationDocument(
            PurposeTemplate purposeTemplate,
            String prettyName,
            String fixturePath
    ) {
        if (prettyName == null || prettyName.isBlank() || fixturePath == null || fixturePath.isBlank()) {
            throw new IllegalArgumentException("Annotation document name and fixture path must not be blank");
        }
        UUID answerId = purposeTemplate.getFirstRiskAnalysisAnswerId();
        return purposeTemplateGateway.addRiskAnalysisAnswerAnnotationDocument(
                purposeTemplate,
                answerId,
                prettyName,
                fixturePath
        );
    }

    public void awaitSignedRiskAnalysisDocumentAvailable(PurposeTemplate purposeTemplate) {
        purposeTemplateGateway.awaitSignedRiskAnalysisDocumentAvailable(purposeTemplate);
    }

    public void attemptRiskAnalysisAnswerAnnotationDocumentUpload(
            PurposeTemplate purposeTemplate,
            String prettyName,
            String fixturePath
    ) {
        if (prettyName == null || prettyName.isBlank() || fixturePath == null || fixturePath.isBlank()) {
            throw new IllegalArgumentException("Annotation document name and fixture path must not be blank");
        }
        UUID answerId = purposeTemplate.getFirstRiskAnalysisAnswerId();
        purposeTemplateGateway.attemptRiskAnalysisAnswerAnnotationDocumentUpload(
                purposeTemplate,
                answerId,
                prettyName,
                fixturePath
        );
    }

    public void attemptLinkEService(PurposeTemplate purposeTemplate, EService eService) {
        purposeTemplateGateway.attemptLinkEService(purposeTemplate, eService.getRef());
    }

    public void attemptUpdatePurposeTemplateWithFreeTextAnswer(
            PurposeTemplate purposeTemplate,
            Tenant targetTenant,
            String freeTextAnswer
    ) {
        RiskAnalysisFormConfig latestConfig = riskAnalysisGateway.getLatestRiskAnalysisConfig(targetTenant);
        Map<String, List<String>> answers = new LinkedHashMap<>(
                riskAnalysisDataFactory.getTemplateForTenant(targetTenant, true)
        );
        String freeTextKey = resolveFreeTextQuestionKey(latestConfig, answers);
        answers.put(freeTextKey, List.of(freeTextAnswer));

        RiskAnalysisForm form = RiskAnalysisForm.builder()
                .version(latestConfig.getVersion())
                .answers(answers)
                .build();

        purposeTemplateGateway.attemptUpdatePurposeTemplate(purposeTemplate, targetTenant, form);
    }

    public void attemptAddRiskAnalysisAnswerWithAnnotation(
            PurposeTemplate purposeTemplate,
            String annotationText
    ) {
        purposeTemplateGateway.attemptAddRiskAnalysisAnswerWithAnnotation(purposeTemplate, annotationText);
    }

    public void attemptUpdateRiskAnalysisAnswerAnnotation(
            PurposeTemplate purposeTemplate,
            String annotationText
    ) {
        UUID answerId = purposeTemplate.getFirstRiskAnalysisAnswerId();
        purposeTemplateGateway.attemptUpdateRiskAnalysisAnswerAnnotation(
                purposeTemplate, answerId, annotationText
        );
    }

    public void attemptRenameRiskAnalysisAnswerAnnotationDocument(
            PurposeTemplate purposeTemplate,
            String currentDocumentName,
            String newDocumentName
    ) {
        List<Document> docs = purposeTemplate.getAnnotationDocuments() != null
                ? purposeTemplate.getAnnotationDocuments()
                : List.of();
        List<Document> matches = docs.stream()
                .filter(doc -> currentDocumentName.equals(doc.getPrettyName()))
                .toList();
        if (matches.size() != 1) {
            throw new IllegalStateException("Expected exactly one annotation document named '" + currentDocumentName
                    + "' in PurposeTemplate " + purposeTemplate.getId() + ", found " + matches.size());
        }
        UUID answerId = purposeTemplate.getFirstRiskAnalysisAnswerId();
        purposeTemplateGateway.attemptRenameRiskAnalysisAnswerAnnotationDocument(
                purposeTemplate,
                answerId,
                matches.get(0).getId(),
                newDocumentName
        );
    }

    public void attemptSignedRiskAnalysisDocumentDownload(PurposeTemplate purposeTemplate) {
        purposeTemplateGateway.attemptSignedRiskAnalysisDocumentDownload(purposeTemplate);
    }

    private static String resolveFreeTextQuestionKey(
            RiskAnalysisFormConfig config,
            Map<String, List<String>> answers
    ) {
        if (config != null && config.getQuestions() != null) {
            for (RiskAnalysisFormConfig.Question question : config.getQuestions()) {
                if ("FREETEXT".equals(question.getDataType()) && answers.containsKey(question.getId())) {
                    return question.getId();
                }
            }
            for (RiskAnalysisFormConfig.Question question : config.getQuestions()) {
                if ("FREETEXT".equals(question.getDataType())) {
                    return question.getId();
                }
            }
        }
        return "institutionalPurpose";
    }
}
