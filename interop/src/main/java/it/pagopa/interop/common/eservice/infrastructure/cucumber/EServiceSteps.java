package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorApprovalUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

import static it.pagopa.utils.RandomUtils.randomAlphanumericName;

@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final InteropJourney interopJourney;
    private final EServiceUseCase eServiceUseCase;
    private final EServiceDescriptorApprovalUseCase approvalUseCase;
    private final EntityStore entityStore;
    private String lastImportedEServiceName;

    @Given("un e-service SOAP in bozza creato dall'erogatore")
    public void createDraftSoapEService() {
        interopJourney.createEService(
                cmd -> cmd.technology(EServiceTechnology.SOAP),
                EServiceDescriptorState.DRAFT
        );
    }

    @When("l'erogatore tenta di importare un e-service SOAP con interfaccia {string} ottenendo un rifiuto per \"{soapInterfaceUploadFailureReason}\"")
    public void rejectSoapEServiceImport(String fixture, SoapInterfaceUploadFailureReason reason) {
        lastImportedEServiceName = RandomUtils.randomAlphanumericName("import-eservice", 20);
        interopJourney.shouldFailToImportSOAPEService(lastImportedEServiceName, "assets/wsdl/" + fixture, reason);
    }

    @Then("l'e-service importato non è presente tra gli e-service dell'erogatore")
    public void assertImportedEServiceNotPresentInProducerCatalog() {
        interopJourney.assertEServiceNotPresentInProducerCatalog(lastImportedEServiceName);
    }

    @When("l'utente apre la pagina di creazione di un e-service")
    public void openEServiceCreation() {
        eServiceUseCase.openEServiceCreation();
    }

    @When("l'utente apre la modifica dei dati generali in bozza dell'{currentEService}")
    public void openDraftEServiceGeneralData(EService eService) {
        eServiceUseCase.openDraftEServiceGeneralData(eService);
    }

    @When("l'utente apre la modifica dei dati generali in bozza per {eServiceByAlias}")
    public void openDraftEServiceGeneralDataByAlias(EService eService) {
        eServiceUseCase.openDraftEServiceGeneralData(eService);
    }

    @When("l'utente tenta di salvare nella bozza il nome di un altro e-service: {eServiceByAlias}")
    public void attemptToUpdateDraftEServiceWithDuplicateName(EService existingEService) {
        eServiceUseCase.shouldFailToUpdateDraftEServiceName(
                existingEService.getName(), EServiceUpdateFailureReason.DUPLICATE_NAME
        );
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

    @Given("la versione in bozza appena creata dell'{currentEService} viene eliminata")
    public void deleteDraftVersion(EService eService) {
        descriptorUseCase.deleteDraftVersion(eService);
    }

    @When("l'utente tenta di consultare i dettagli dell'{currentEService} in erogazione")
    public void attemptProducerEServiceDetailsRead(EService eService) {
        eServiceUseCase.attemptProducerEServiceDetailsRead(eService);
    }

    @When("l'utente tenta di consultare i dettagli della versione {int} dell'{currentEService} in erogazione")
    public void attemptProducerDescriptorDetailsRead(int version, EService eService) {
        descriptorUseCase.attemptProducerDescriptorDetailsRead(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'utente tenta di esportare la versione {int} dell'{currentEService}")
    public void attemptDescriptorExport(int version, EService eService) {
        descriptorUseCase.attemptDescriptorExport(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di modificare il nome del{eServiceByAlias} con quello del{eServiceByAlias}")
    public void attemptNameUpdateWithAnotherEServiceName(EService eService, EService existing) {
        eServiceUseCase.attemptNameUpdate(eService, existing.getName());
    }

    @When("l'erogatore tenta di modificare il nome del{eServiceByAlias} con quello del {eServiceTemplateByAlias}")
    public void attemptNameUpdateWithTemplateName(EService eService, EServiceTemplate template) {
        eServiceUseCase.attemptNameUpdate(eService, template.getName());
    }

    @When("l'erogatore tenta di modificare il nome del{eServiceByAlias} in {string}")
    public void attemptNameUpdate(EService eService, String name) {
        eServiceUseCase.attemptNameUpdate(eService, name);
    }

    @When("il delegato richiede al delegante l'archiviazione dell'{currentEService} con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void attemptDelegatedArchivingRequest(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.attemptDelegatedArchivingRequest(eService, gracePeriodDays);
    }

    @When("l'erogatore tenta di modificare la descrizione dell'{currentEService} in {string}")
    public void attemptDescriptionUpdate(EService eService, String description) {
        eServiceUseCase.attemptDescriptionUpdate(eService, description);
    }

    @When("l'erogatore tenta di aggiornare parzialmente i dati dell'{currentEService} impostando il nome {string}")
    public void attemptPartialNameUpdate(EService eService, String name) {
        eServiceUseCase.attemptPartialNameUpdate(eService, name);
    }

    @When("l'erogatore tenta di aggiornare parzialmente i dati del{eServiceByAlias} con il nome del{eServiceByAlias}")
    public void attemptPartialNameUpdateWithAnotherEServiceName(EService eService, EService existing) {
        eServiceUseCase.attemptPartialNameUpdate(eService, existing.getName());
    }

    @When("l'erogatore tenta di aggiornare parzialmente i dati dell'{currentEService} con il nome del {eServiceTemplate}")
    public void attemptPartialNameUpdateWithTemplateName(EService eService, EServiceTemplate template) {
        eServiceUseCase.attemptPartialNameUpdate(eService, template.getName());
    }

    @When("l'erogatore tenta di eliminare l'{currentEService} in bozza")
    public void attemptDraftEServiceDeletion(EService eService) {
        eServiceUseCase.attemptEServiceDeletion(eService);
    }

    @Given("una richiesta di archiviazione dell'intero {currentEService} inoltrata al delegante con {gracePeriodDays} giorni di preavviso e una motivazione valida")
    public void submitDelegatedEntireEServiceArchivingRequest(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.submitDelegatedArchivingRequest(eService, gracePeriodDays);
    }

    @When("il delegato tenta di annullare la richiesta di archiviazione dell'intero {currentEService}")
    public void attemptCancelDelegatedEntireEServiceArchivingRequest(EService eService) {
        eServiceUseCase.attemptCancelDelegatedArchivingRequest(eService);
    }

    @When("il delegato richiede al delegante l'archiviazione della versione {int} dell'{currentEService} con {gracePeriodDays} giorni di preavviso e una motivazione valida")
    public void attemptDelegatedDescriptorArchivingRequest(
            int version, EService eService, GracePeriodDays gracePeriodDays
    ) {
        descriptorUseCase.attemptDelegatedArchivingRequest(
                eService, eService.getDescriptorOrThrow(version), gracePeriodDays
        );
    }

    @Given("una richiesta di archiviazione della versione {int} dell'{currentEService} inoltrata al delegante con {gracePeriodDays} giorni di preavviso e una motivazione valida")
    public void submitDelegatedDescriptorArchivingRequest(
            int version, EService eService, GracePeriodDays gracePeriodDays
    ) {
        descriptorUseCase.submitDelegatedArchivingRequest(
                eService, eService.getDescriptorOrThrow(version), gracePeriodDays
        );
    }

    @When("il delegato tenta di annullare la richiesta di archiviazione della versione {int} dell'{currentEService}")
    public void attemptCancelDelegatedDescriptorArchivingRequest(int version, EService eService) {
        descriptorUseCase.attemptCancelDelegatedArchivingRequest(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di creare una nuova versione dell'{currentEService}")
    public void attemptDescriptorCreation(EService eService) {
        descriptorUseCase.attemptDescriptorCreation(eService);
    }

    @When("l'erogatore tenta di eliminare la versione {int} dell'{currentEService}")
    public void attemptDraftDescriptorDeletion(int version, EService eService) {
        descriptorUseCase.attemptDraftVersionDeletion(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di modificare la configurazione della versione {int} dell'{currentEService} con dati validi")
    public void attemptDescriptorConfigurationUpdate(int version, EService eService) {
        descriptorUseCase.attemptDescriptorConfigurationUpdate(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di modificare la configurazione della versione {int} del{eServiceByAlias} con dati validi")
    public void attemptDescriptorConfigurationUpdateByAlias(int version, EService eService) {
        descriptorUseCase.attemptDescriptorConfigurationUpdate(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di pubblicare la versione {int} dell'{currentEService}")
    public void attemptDescriptorPublication(int version, EService eService) {
        descriptorUseCase.attemptDescriptorPublication(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di sospendere la versione {int} dell'{currentEService}")
    public void attemptDescriptorSuspension(int version, EService eService) {
        descriptorUseCase.attemptDescriptorSuspension(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di riattivare la versione {int} dell'{currentEService}")
    public void attemptDescriptorActivation(int version, EService eService) {
        descriptorUseCase.attemptDescriptorActivation(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di modificare la modalità di approvazione delle richieste di fruizione della versione {int} dell'{currentEService} con dati validi")
    public void attemptAgreementApprovalPolicyUpdate(int version, EService eService) {
        descriptorUseCase.attemptAgreementApprovalPolicyUpdate(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di modificare le impostazioni di delega dell'{currentEService} con dati validi")
    public void attemptDelegationFlagsUpdate(EService eService) {
        eServiceUseCase.attemptDelegationFlagsUpdate(eService);
    }

    @When("l'erogatore tenta di modificare il nome dell'{currentEService} con un nome disponibile")
    public void attemptAvailableNameUpdate(EService eService) {
        eServiceUseCase.attemptNameUpdate(eService, randomAlphanumericName("eservice-name", 15));
    }

    @When("l'erogatore tenta di modificare l'adesione dell'{currentEService} a Signal Hub con dati validi")
    public void attemptSignalHubUpdate(EService eService) {
        eServiceUseCase.attemptSignalHubUpdate(eService);
    }

    @When("l'erogatore tenta di modificare gli attributi richiesti dalla versione {int} dell'{currentEService} con dati validi")
    public void attemptDescriptorAttributesUpdate(int version, EService eService) {
        descriptorUseCase.attemptAttributesUpdate(eService, eService.getDescriptorOrThrow(version));
    }

    @When("l'erogatore tenta di aggiornare l'{currentEService} alla nuova versione del template")
    public void attemptInstanceUpgrade(EService eService) {
        eServiceUseCase.attemptInstanceUpgrade(eService);
    }

    @When("l'erogatore tenta di modificare l'indicazione sul trattamento dei dati personali dell'{currentEService} con dati validi")
    public void attemptPersonalDataFlagUpdate(EService eService) {
        eServiceUseCase.attemptPersonalDataFlagUpdate(eService);
    }

    @When("l'erogatore tenta di rifiutare la pubblicazione della versione {int} dell'{currentEService} con motivazione {string}")
    public void attemptDescriptorRejection(int version, EService eService, String reason) {
        approvalUseCase.attemptDescriptorRejection(eService, eService.getDescriptorOrThrow(version), reason);
    }

    @When("l'erogatore tenta di importare un e-service dal file {string}")
    public void attemptEServiceImportFromFile(String filePath) {
        // Usato negli scenari con id ERROR_173
        eServiceUseCase.attemptEServiceImportFromFile(filePath);
    }

    /**
     * Tenta di creare una nuova versione (descrittore) per l'ultimo e-service nel contesto.
     * <p>
     * DUBBIO SULLA FATTIBILITÀ DEL TEST (ERROR_085):
     * L'endpoint BFF {@code POST /eservices/{eServiceId}/descriptors} ({@code createDescriptor}) non accetta
     * alcun payload di richiesta: clona la descrizione e gli altri metadati dalla versione precedente.
     * Tuttavia, la creazione/modifica e la conseguente pubblicazione della versione precedente vengono
     * già bloccate a monte dal backend se la descrizione contiene un URL ({@code hyperlinkDetectionError}).
     * Non è quindi chiaro come sia possibile mettersi nella condizione di avere una versione precedente
     * pubblicata contenente un link per poter poi fallire sulla creazione della nuova versione.
     */
    @When("l'erogatore tenta di creare una nuova versione dell'e-service con un link nella descrizione e gli altri dati validi")
    public void attemptCreateDescriptorWithLinkInDescription() {
        // Usato negli scenari con id ERROR_085
        EService eService = entityStore.getLastOrThrow(EService.class);
        descriptorUseCase.attemptDescriptorCreation(eService);
    }
}
