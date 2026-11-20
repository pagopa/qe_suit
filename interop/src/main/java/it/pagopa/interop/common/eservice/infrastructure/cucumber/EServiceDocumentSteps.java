package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceDocumentUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceDocumentConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.SelectedEServiceDescriptor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EServiceDocumentSteps {
    private final EServiceDocumentUseCase useCase;

    @Given("i seguenti documenti allegati alla versione dell'{currentEService}:")
    public void attachDocuments(EService service, List<EServiceDocumentConfiguration> configurations) {
        if (configurations.isEmpty()) {
            throw new IllegalArgumentException("At least one document is required");
        }
        var descriptor = service.getLatestDescriptor();
        for (var configuration : configurations) {
            descriptor = useCase.addDocument(service, descriptor, configuration.getFixture(), configuration.getName());
        }
    }

    @When("l'erogatore tenta di caricare un file valido come documentazione per {eServiceDescriptorByVersionAndAlias}")
    @When("l'utente tenta di caricare un file valido come documentazione per {eServiceDescriptorByVersionAndAlias}")
    public void attemptValidDocumentUpload(SelectedEServiceDescriptor selection) {
        EService service = selection.getEService();
        useCase.attemptValidDocumentUpload(service, service.getDescriptorOrThrow(selection.getVersion()));
    }

    @When("l'erogatore tenta di caricare il file {string} come documentazione per {eServiceDescriptorByVersionAndAlias} con il nome {string}")
    public void attemptDocumentUpload(String path, SelectedEServiceDescriptor selection, String name) {
        EService service = selection.getEService();
        useCase.attemptDocumentUpload(service, service.getDescriptorOrThrow(selection.getVersion()), path, name);
    }

    @When("l'erogatore tenta di rinominare il documento {string} del{eServiceDescriptorByVersionAndAlias} in {string}")
    public void attemptDocumentRename(String documentName, SelectedEServiceDescriptor selection, String newName) {
        EService service = selection.getEService();
        useCase.attemptDocumentRename(service, service.getDescriptorOrThrow(selection.getVersion()), documentName, newName);
    }

    @When("l'erogatore carica il file {string} come {string} sulla versione dell'e-service con il nome {string}")
    public void uploadFileToEServiceVersion(String filePath, String documentType, String documentName) {
        // TODO
        // Usato negli scenari con id ERROR_181, ERROR_182, ERROR_183, ERROR_185, ERROR_186, ERROR_187
    }
}
