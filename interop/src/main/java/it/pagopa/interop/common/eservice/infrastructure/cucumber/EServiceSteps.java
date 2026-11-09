package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.application.EServiceUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceArchivingFailureReason;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.eservice.domain.SoapInterfaceUploadFailureReason;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EServiceSteps {
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final InteropJourney interopJourney;
    private final EServiceUseCase eServiceUseCase;
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

    @Given("la versione {int} dell'{currentEService} è in fase di archiviazione con un periodo di preavviso di {gracePeriodDays} giorni")
    public void prepareArchivingDescriptor(int versionNumber, EService eService, GracePeriodDays gracePeriodDays) {
        descriptorUseCase.archiveDescriptor(eService, eService.getDescriptor(versionNumber), gracePeriodDays);
        interopJourney.waitUntilEService(refreshed ->
                refreshed.getDescriptor(versionNumber).getState() == EServiceDescriptorState.ARCHIVING
                        && refreshed.getLatestDescriptor().getState() == EServiceDescriptorState.PUBLISHED
        );
    }

    @When("l'erogatore archivia l'{currentEService} con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void archiveEService(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.archiveEService(eService, gracePeriodDays);
    }

    @When("l'erogatore tenta di archiviare l'{currentEService} con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void rejectEServiceArchiving(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.shouldFailToArchiveEService(
                eService, gracePeriodDays, EServiceArchivingFailureReason.ARCHIVING_BEFORE_DESCRIPTOR
        );
    }

    @When("il delegato tenta di richiedere al delegante l'archiviazione dell'intero {currentEService} con un periodo di preavviso di {gracePeriodDays} giorni e una motivazione valida")
    public void rejectEServiceArchivingRequest(EService eService, GracePeriodDays gracePeriodDays) {
        eServiceUseCase.shouldFailToRequestEServiceArchiving(
                eService, gracePeriodDays, EServiceArchivingFailureReason.ARCHIVING_BEFORE_DESCRIPTOR
        );
    }
}
