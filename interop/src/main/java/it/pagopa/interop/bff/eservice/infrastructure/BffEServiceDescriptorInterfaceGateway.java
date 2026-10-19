package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.kernel.domain.DocumentKind;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.model.ProducerEServiceDescriptor;
import it.pagopa.utils.FileUtils;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.DelayUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BffEServiceDescriptorInterfaceGateway {
    private final BffEServiceRestClient restClient;
    private final EntityStore entityStore;
    private final BffEServiceDescriptorMapper mapper;

    public EServiceDescriptor configureSOAP(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef, List<SoapAddressConfiguration> addresses) {
        restClient.addEServiceTemplateInstanceInterfaceSoap(
                        serviceRef.id(), descriptorRef.id(), BffEServiceInterfacePayloadFactory.soapPayload(addresses)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> readAndUpdate(serviceRef, descriptorRef),
                descriptor -> descriptor.getInterfaceDocument() != null
        );
    }

    public EServiceDescriptor configureREST(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef, RestInterfaceConfiguration configuration) {
        restClient.addEServiceTemplateInstanceInterfaceRest(
                        serviceRef.id(), descriptorRef.id(), BffEServiceInterfacePayloadFactory.restPayload(configuration)
                )
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();

        return PollingUtils.pollUntil(
                () -> readAndUpdate(serviceRef, descriptorRef),
                descriptor -> descriptor.getInterfaceDocument() != null
        );
    }

    public void shouldFailToConfigureSOAP(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef, List<SoapAddressConfiguration> addresses, EServiceTemplateSOAPInterfaceFailureReason reason) {
        int expectedStatus = switch (reason) {
            case URL_COUNT_MISMATCH, URL_NOT_ALLOWED -> 400;
        };

        restClient.addEServiceTemplateInstanceInterfaceSoap(
                        serviceRef.id(), descriptorRef.id(), BffEServiceInterfacePayloadFactory.soapPayload(addresses)
                )
                .withPolling(PollingStrategy.UNTIL_ERROR)
                .assertStatusCode(expectedStatus);
    }

    public EServiceDescriptor downloadInterface(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef) {
        EService existing = entityStore.getById(serviceRef.id(), EService.class).orElse(null);
        ProducerEServiceDescriptor response = readDescriptor(serviceRef, descriptorRef);
        EService aggregate = mapper.toEServiceWithUpsert(response, existing);
        EServiceDescriptor metadata = aggregate.findDescriptor(descriptorRef.id());
        if (metadata == null || metadata.getInterfaceDocument() == null) {
            throw new IllegalStateException("Descriptor does not contain a root interface document");
        }
        EServiceDescriptor enriched = metadata.toBuilder()
                .interfaceDocument(metadata.getInterfaceDocument().withContent(
                        download(serviceRef, descriptorRef, metadata.getInterfaceDocument().getId())
                ))
                .build();
        aggregate.addDescriptor(enriched);
        entityStore.upsert(aggregate);
        return enriched;
    }

    public Optional<EServiceDescriptorInterface> readSnapshot(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef
    ) {
        EServiceDescriptor descriptor = readDescriptorWithoutContext(serviceRef, descriptorRef);
        if (descriptor.getInterfaceDocument() == null) return Optional.empty();
        EServiceDescriptorInterface metadata = descriptor.getInterfaceDocument();
        return Optional.of(metadata.withContent(download(serviceRef, descriptorRef, metadata.getId())));
    }

    public Optional<EServiceDescriptorInterface> readSnapshotAfterDelay(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef
    ) {
        DelayUtils.waitForSeconds(15);
        return readSnapshot(serviceRef, descriptorRef);
    }

    public EServiceDescriptor linkDocument(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef,
            String path,
            DocumentKind kind
    ) {
        File document = FileUtils.loadClasspathResourceAsTempFile(path);
        String name = RandomUtils.randomAlphanumericName("interface") + ".yaml";
        DelayUtils.waitForSeconds(1);
        return restClient.addDocument(serviceRef.id(), descriptorRef.id(), kind.name(), name, document)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .map(ignored -> readAndUpdate(serviceRef, descriptorRef))
                .get();
    }

    private EServiceDescriptor readAndUpdate(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef) {
        EService existing = entityStore.getById(serviceRef.id(), EService.class).orElse(null);
        EService aggregate = mapper.toEServiceWithUpsert(readDescriptor(serviceRef, descriptorRef), existing);
        entityStore.upsert(aggregate);
        return aggregate.findDescriptor(descriptorRef.id());
    }

    private ProducerEServiceDescriptor readDescriptor(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef) {
        return restClient.readDescriptor(serviceRef.id(), descriptorRef.id())
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

    private EServiceDescriptor readDescriptorWithoutContext(
            EServiceRef serviceRef,
            EServiceDescriptorRef descriptorRef
    ) {
        EServiceDescriptor descriptor = mapper.toEServiceDescriptor(readDescriptor(serviceRef, descriptorRef));
        EServiceDescriptorInterface document = descriptor.getInterfaceDocument();
        if (document == null) return descriptor;
        return descriptor.toBuilder().interfaceDocument(new EServiceDescriptorInterface(
                document.getMetadata(), serviceRef, descriptorRef, null
        )).build();
    }

    private byte[] download(EServiceRef serviceRef, EServiceDescriptorRef descriptorRef, UUID documentId) {
        return restClient.getEServiceDocumentById(serviceRef.id(), descriptorRef.id(), documentId)
                .withPolling(PollingStrategy.UNTIL_SUCCESS)
                .get();
    }

}
