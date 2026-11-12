package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.eservice.application.EServiceDocumentUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceDocumentConfiguration;
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
}
