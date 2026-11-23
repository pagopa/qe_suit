package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.common.purpose_template.infrastructure.cucumber.dto.PurposeTemplateDocumentConfiguration;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PurposeTemplateSteps {

    private final InteropJourney journey;
    private final PurposeTemplateUseCase purposeTemplateUseCase;
    private final EntityStore entityStore;

    @Given("un template finalità creato da/dal {tenant} e pubblicato")
    public void createPurposeTemplate(Tenant creator) {
        journey.withProducer(creator)
                .createPurposeTemplate(Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.PUBLISHED);
    }

    @Given("un template finalità in bozza con una risposta annotata")
    public void draftPurposeTemplateWithAnnotatedAnswer() {
        journey.createPurposeTemplate(Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.DRAFT)
                .addAnnotationToPurposeTemplateRiskAnalysisAnswer();
    }

    @Given("i seguenti documenti PDF allegati all'annotazione del template finalità:")
    public void pdfDocumentsAttachedToPurposeTemplateAnnotation(List<PurposeTemplateDocumentConfiguration> documents) {
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("At least one document configuration is required");
        }
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        for (PurposeTemplateDocumentConfiguration document : documents) {
            purposeTemplate = purposeTemplateUseCase.addRiskAnalysisAnswerAnnotationDocument(
                    purposeTemplate,
                    document.getName(),
                    document.getFixture()
            );
        }
    }

    @Given("un template finalità in bozza con una domanda predisposta per le annotazioni")
    public void draftPurposeTemplateWithQuestionPreparedForAnnotations() {
        journey.createPurposeTemplate(Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.DRAFT);
    }

    @Given("il documento firmato di analisi del rischio del template finalità disponibile")
    public void signedRiskAnalysisDocumentForPurposeTemplateAvailable() {
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.awaitSignedRiskAnalysisDocumentAvailable(purposeTemplate);
    }

    @When("l'operatore tenta di riattivare il {currentPurposeTemplate}")
    public void attemptPurposeTemplateReactivation(PurposeTemplate purposeTemplate) {
        purposeTemplateUseCase.attemptPurposeTemplateReactivation(purposeTemplate);
    }

    @When("l'operatore tenta di pubblicare il {currentPurposeTemplate}")
    public void attemptPurposeTemplatePublication(PurposeTemplate purposeTemplate) {
        purposeTemplateUseCase.attemptPurposeTemplatePublication(purposeTemplate);
    }

    @When("l'operatore allega il file {string} all'annotazione del template finalità con il nome {string}")
    public void attachFileToPurposeTemplateAnnotation(String filePath, String documentName) {
        // Usato negli scenari con id ERROR_140, ERROR_141, ERROR_195, ERROR_196
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.attemptRiskAnalysisAnswerAnnotationDocumentUpload(
                purposeTemplate, documentName, filePath
        );
    }

    @When("l'operatore tenta di aggiungere l'e-service {string} agli e-service suggeriti del template finalità")
    public void attemptAddSuggestedEServiceToPurposeTemplate(String eServiceAlias) {
        // Usato negli scenari con id ERROR_206
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EService eService = entityStore.getByAliasOrThrow(eServiceAlias, EService.class);
        purposeTemplateUseCase.attemptLinkEService(purposeTemplate, eService);
    }

    @When("l'operatore tenta di modificare il template finalità inserendo {string} in una risposta a testo libero dell'analisi del rischio")
    public void attemptUpdatePurposeTemplateWithLinkInRiskAnalysisFreeTextAnswer(String answerText) {
        // Usato negli scenari con id ERROR_207
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.attemptUpdatePurposeTemplateWithFreeTextAnswer(
                purposeTemplate, purposeTemplate.getTargetTenantKind(), answerText
        );
    }

    @When("l'operatore tenta di aggiungere una risposta al template finalità con l'annotazione {string}")
    public void attemptAddAnnotatedAnswerToPurposeTemplate(String annotationText) {
        // Usato negli scenari con id ERROR_142
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.attemptAddRiskAnalysisAnswerWithAnnotation(
                purposeTemplate, annotationText
        );
    }

    @When("l'operatore tenta di aggiornare l'annotazione del template finalità con il testo {string}")
    public void attemptUpdatePurposeTemplateAnnotationText(String annotationText) {
        // Usato negli scenari con id ERROR_143
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.attemptUpdateRiskAnalysisAnswerAnnotation(
                purposeTemplate, annotationText
        );
    }

    @When("l'operatore tenta di rinominare l'allegato {string} dell'annotazione del template finalità in {string}")
    public void attemptRenamePurposeTemplateAnnotationDocument(String currentDocumentName, String newDocumentName) {
        // Usato negli scenari con id ERROR_144
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.attemptRenameRiskAnalysisAnswerAnnotationDocument(
                purposeTemplate, currentDocumentName, newDocumentName
        );
    }

    @When("l'utente tenta di scaricare il documento firmato di analisi del rischio del template finalità")
    public void attemptDownloadSignedRiskAnalysisDocument() {
        // Usato negli scenari con id ERROR_145
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        purposeTemplateUseCase.attemptSignedRiskAnalysisDocumentDownload(purposeTemplate);
    }
}
