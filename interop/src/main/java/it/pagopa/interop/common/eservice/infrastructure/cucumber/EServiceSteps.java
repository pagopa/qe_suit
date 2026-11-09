package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.journey.application.InteropJourney;
import lombok.RequiredArgsConstructor;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;

@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final InteropJourney interopJourney;
    private final EServiceUseCase eServiceUseCase;

    @When("l'utente apre la pagina di creazione di un e-service")
    public void openEServiceCreation() {
        eServiceUseCase.openEServiceCreation();
    }

    @When("l'utente apre la modifica dei dati generali in bozza dell'{currentEService}")
    public void openDraftEServiceGeneralData(EService eService) {
        eServiceUseCase.openDraftEServiceGeneralData(eService);
    }

    @When("l'utente tenta di salvare un link nella descrizione dell'e-service lasciando validi gli altri dati")
    public void attemptToUpdateDraftEServiceDescriptionWithLink() {
        eServiceUseCase.shouldFailToUpdateDraftEServiceDescription(
                "Descrizione di test con https://example.com",
                EServiceUpdateFailureReason.HYPERLINK_DETECTED
        );
    }

    @When("l'utente tenta di creare un e-service con il nome dell'{currentEService} e gli altri dati validi")
    public void attemptToCreateEServiceWithSameName(EService existingEService) {
        eServiceUseCase.shouldFailToCreateEService(
                command -> command.name(existingEService.getName()), EServiceCreationFailureReason.DUPLICATE_NAME
        );
    }

    @When("l'utente tenta di creare un e-service con un link nel nome e gli altri dati validi")
    public void attemptToCreateEServiceWithLinkInName() {
        eServiceUseCase.shouldFailToCreateEService(
                command -> command.name(randomAlphanumericName("eservice", 15) + " https://example.com"),
                EServiceCreationFailureReason.HYPERLINK_DETECTED
        );
    }

    @When("l'utente tenta di creare un e-service con il nome del {eServiceTemplate} e gli altri dati validi")
    public void attemptToCreateEServiceWithTemplateName(EServiceTemplate template) {
        eServiceUseCase.shouldFailToCreateEService(
                command -> command.name(template.getName()),
                EServiceCreationFailureReason.TEMPLATE_NAME_CONFLICT
        );
    }

    @Given("la versione {int} dell'{currentEService} (è )in fase di archiviazione con un periodo di preavviso di {gracePeriodDays} giorni")
    public void prepareArchivingDescriptor(int versionNumber, EService eService, GracePeriodDays gracePeriodDays) {
        descriptorUseCase.archiveDescriptor(
                eService,
                eService.findDescriptor(versionNumber).orElseThrow(() -> new IllegalStateException("Descriptor version not found")),
                gracePeriodDays
        );

        interopJourney.waitUntilEService(refreshed -> {
                    EServiceDescriptor descriptor = refreshed.findDescriptor(versionNumber).orElseThrow(() -> new IllegalStateException("Descriptor version not found"));
                    return descriptor.getState() == EServiceDescriptorState.ARCHIVING
                            && refreshed.getLatestDescriptor().getState() == EServiceDescriptorState.PUBLISHED;
                }
        );
    }

    @When("l'erogatore tenta di archiviare l'{currentEService} con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void archiveEService(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.archiveEService(eService, gracePeriodDays);
    }

    @Then("il sistema rifiuta l'archiviazione dell'{currentEService} da parte dell'erogatore con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void rejectEServiceArchiving(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.shouldFailToArchiveEService(
                eService, gracePeriodDays, EServiceArchivingFailureReason.ARCHIVING_BEFORE_DESCRIPTOR
        );
    }

    @Then("il sistema rifiuta la richiesta di archiviazione dell'{currentEService} da parte del delegato con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void rejectEServiceArchivingRequest(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.shouldFailToRequestEServiceArchiving(
                eService, gracePeriodDays, EServiceArchivingFailureReason.ARCHIVING_BEFORE_DESCRIPTOR
        );
    }
}
