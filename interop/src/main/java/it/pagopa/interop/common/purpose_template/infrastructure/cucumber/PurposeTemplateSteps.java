package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantKind;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.common.purpose_template.infrastructure.cucumber.dto.PurposeTemplateDocumentConfiguration;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PurposeTemplateSteps {

    private final InteropJourney journey;
    private final PurposeTemplateUseCase purposeTemplateUseCase;

    @Given("un template finalità creato da/dal {tenant} e pubblicato")
    public void createPurposeTemplate(Tenant creator) {
        journey.withProducer(creator)
                .createPurposeTemplate(Tenant.fromTenantType(TenantKind.PA), PurposeTemplateState.PUBLISHED);
    }

    @Given("un template finalità in bozza con una risposta annotata")
    public void draftPurposeTemplateWithAnnotatedAnswer() {
        // TODO
    }

    @Given("i seguenti documenti PDF allegati all'annotazione del template finalità:")
    public void pdfDocumentsAttachedToPurposeTemplateAnnotation(List<PurposeTemplateDocumentConfiguration> documents) {
        // TODO
    }

    @Given("un template finalità in bozza con una domanda predisposta per le annotazioni")
    public void draftPurposeTemplateWithQuestionPreparedForAnnotations() {
        // TODO
    }

    @Given("il documento firmato di analisi del rischio del template finalità disponibile")
    public void signedRiskAnalysisDocumentForPurposeTemplateAvailable() {
        // TODO
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
