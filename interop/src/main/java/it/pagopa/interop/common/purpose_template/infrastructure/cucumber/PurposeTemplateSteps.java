package it.pagopa.interop.common.purpose_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.application.PurposeTemplateUseCase;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PurposeTemplateSteps {

    private final PurposeTemplateUseCase purposeTemplateUseCase;
    private final InteropJourney interopJourney;
    private final CurrentUserSession currentUserSession;
    private final EntityStore entityStore;

    @Given("un template di finalità avviato in creazione da {tenant}")
    public void startPurposeTemplateCreation(Tenant tenant) {
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        interopJourney.withProducer(tenant, UserRole.ADMIN)
            .createPurposeTemplate(tenant, PurposeTemplateState.DRAFT);
    }

    @Given("un template di finalità creato e pubblicato da {tenant}")
    public void createAndPublishPurposeTemplate(Tenant tenant) {
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        interopJourney.withProducer(tenant, UserRole.ADMIN)
            .createPurposeTemplate(tenant, PurposeTemplateState.PUBLISHED);
    }

    @When("{tenant} tenta di associare al proprio template di finalità l'e-service in modalità di ricezione")
    public void tryLinkEServiceToPurposeTemplate(Tenant tenant) {
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EService eService = entityStore.getLastOrThrow(EService.class);
        purposeTemplateUseCase.tryLinkEService(purposeTemplate, eService);
    }

    @When("{tenant} tenta di associare al proprio template di finalità il template e-service in modalità di ricezione")
    public void tryLinkEServiceTemplateToPurposeTemplate(Tenant tenant) {
        currentUserSession.set(User.getTenantAdmin(tenant), tenant);
        PurposeTemplate purposeTemplate = entityStore.getLastOrThrow(PurposeTemplate.class);
        EServiceTemplate eServiceTemplate = entityStore.getLastOrThrow(EServiceTemplate.class);
        purposeTemplateUseCase.tryLinkEServiceTemplate(purposeTemplate, eServiceTemplate);
    }

    @Then("l'associazione dell'e-service al template di finalità è negata")
    public void assertEServiceLinkDenied() {
        purposeTemplateUseCase.shouldFailLinkToReceiveEService();
    }

    @Then("l'associazione del template e-service al template di finalità è negata")
    public void assertEServiceTemplateLinkDenied() {
        purposeTemplateUseCase.shouldFailLinkToReceiveEService();
    }
}
