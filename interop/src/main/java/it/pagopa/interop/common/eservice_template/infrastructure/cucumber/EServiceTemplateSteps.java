package it.pagopa.interop.common.eservice_template.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor
public class EServiceTemplateSteps {

    private final EServiceTemplateUseCase eServiceTemplateUseCase;
    private final InteropJourney interopJourney;
    private final CurrentUserSession currentUserSession;

    @Given("un EService Template in stato DRAFT creato da/dal {tenant}")
    public void createDraftEServiceTemplate(Tenant producer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.DRAFT);
    }

    @Given("un EService Template in stato PUBLISHED creato da/dal {tenant}")
    public void createPublishedEServiceTemplate(Tenant producer) {
        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED);
    }

    @When("il {tenant} carica l'interfaccia OpenAPI standard sul {currentEServiceTemplate}")
    public void linkStandardInterface(Tenant producer, EServiceTemplate template) {
        currentUserSession.set(User.getTenantAdmin(producer), producer);
        eServiceTemplateUseCase.linkStandardInterface(template);
    }

    @When("il {tenant} crea un EService a partire dal {currentEServiceTemplate}")
    public void instantiateEService(Tenant producer, EServiceTemplate template) {
        currentUserSession.set(User.getTenantAdmin(producer), producer);
        eServiceTemplateUseCase.instantiateEService(template);
    }

    @Then("il {currentEServiceTemplate} contiene l'interfaccia OpenAPI caricata")
    public void templateContainsInterface(EServiceTemplate template) {
        EServiceTemplate refreshed = eServiceTemplateUseCase.getEServiceTemplate(template);
        assertNotNull(
                refreshed.lastVersion().getInterfaceDocument(),
                "Il Template dovrebbe contenere il documento di interfaccia"
        );
    }

    @Then("l'{currentEService} creato dal Template risulta in stato DRAFT")
    public void eServiceFromTemplateIsDraft(EService eService) {
        assertEquals(
                EServiceDescriptorState.DRAFT,
                eService.getLastDraftDescriptor().getState(),
                "L'EService istanziato dal Template dovrebbe avere un descrittore in stato DRAFT"
        );
    }
}

