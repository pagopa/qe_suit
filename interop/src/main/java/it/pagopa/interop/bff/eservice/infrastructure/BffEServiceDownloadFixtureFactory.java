package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.infrastructure.contract.DocumentDownloadFixture;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDoc;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BffEServiceDownloadFixtureFactory {
    private final InteropJourney journey;
    private final EServiceDescriptorUseCase descriptorUseCase;
    private final BffEServiceRestClient restClient;

    public DocumentDownloadFixture create(Tenant producer, boolean async, boolean published) {
        EService service = journey.switchChannel(Channel.BFF)
                .withProducer(producer, UserRole.ADMIN)
                .createEService(command -> command.isAsync(async).technology(EServiceTechnology.REST)
                        .mode(EServiceMode.DELIVER).handlePersonalData(false), EServiceDescriptorState.DRAFT)
                .updateDescriptor(command -> command.description("Document accessibility fixture")
                        .audience(List.of("QA")).voucherLifespan(60)
                        .dailyCallsPerConsumer(1).dailyCallsTotal(10), EServiceDescriptorState.DRAFT)
                .get(EService.class);
        var descriptor = service.getLastDraftDescriptor();
        UUID serviceId = service.getId();
        UUID descriptorId = descriptor.getId();
        List<File> documents = new ArrayList<>();
        try {
            UUID interfaceId = upload(serviceId, descriptorId, "INTERFACE",
                    RandomUtils.randomAlphanumericName("Specifica API"), uniqueDocument(documents));
            List<UUID> additionalIds = List.of(
                    upload(serviceId, descriptorId, "DOCUMENT", "Guida all'integrazione", uniqueDocument(documents)),
                    upload(serviceId, descriptorId, "DOCUMENT", "Informazioni aggiuntive", uniqueDocument(documents)));
            var metadata = PollingUtils.pollUntil(
                    () -> restClient.readDescriptor(serviceId, descriptorId)
                            .withPolling(PollingStrategy.UNTIL_SUCCESS).get(),
                    current -> current.getInterface() != null
                            && interfaceId.equals(current.getInterface().getId())
                            && current.getDocs().stream().map(EServiceDoc::getId).toList().containsAll(additionalIds)
                            && (!async || current.getAsyncExchangeCallbackInterface() != null),
                    "Documenti della fixture e-service non ancora disponibili");
            if (published) {
                descriptorUseCase.publishDescriptor(service, descriptor);
                metadata = restClient.readDescriptor(serviceId, descriptorId)
                        .withPolling(PollingStrategy.UNTIL_SUCCESS).get();
            }
            return new DocumentDownloadFixture(serviceId, descriptorId,
                    metadata.getInterface().getPrettyName(),
                    metadata.getDocs().stream().filter(doc -> additionalIds.contains(doc.getId()))
                            .map(EServiceDoc::getPrettyName).toList(),
                    async ? metadata.getAsyncExchangeCallbackInterface().getPrettyName() : null);
        } finally {
            documents.forEach(file -> {
                if (!file.delete()) file.deleteOnExit();
            });
        }
    }

    /** Crea una copia con checksum univoco: il backend rifiuta contenuti duplicati (409). */
    private File uniqueDocument(List<File> created) {
        File file = FileUtils.loadClasspathResourceAsTempFile("assets/origin-interface.yaml");
        created.add(file);
        try {
            Files.writeString(file.toPath(), Files.readString(file.toPath())
                    + System.lineSeparator() + "# " + UUID.randomUUID() + System.lineSeparator());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return file;
    }

    private UUID upload(UUID serviceId, UUID descriptorId, String kind, String name, File document) {
        return restClient.addDocument(serviceId, descriptorId, kind, name, document)
                .withPolling(PollingStrategy.UNTIL_SUCCESS).get().getId();
    }
}
