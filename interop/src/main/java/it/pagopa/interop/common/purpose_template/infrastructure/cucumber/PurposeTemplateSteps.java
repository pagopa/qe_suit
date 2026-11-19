package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.application.context.EntityStore;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
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
}
