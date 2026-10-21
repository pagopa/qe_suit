package it.pagopa.interop.common.eservice_template.application;

import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.bff.eservice_template.infrastructure.BffEServiceTemplateRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorGateway;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.eservice_template.domain.*;
import it.pagopa.interop.common.journey.infrastructure.EServiceTemplateInstanceJourneyImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceTemplateInstanceUseCaseTest {
    private final EServiceTemplateInstanceGateway gateway = mock(EServiceTemplateInstanceGateway.class);
    private final EServiceTemplateVersionGateway versionGateway = mock(EServiceTemplateVersionGateway.class);
    private final EServiceDescriptorGateway descriptorGateway = mock(EServiceDescriptorGateway.class);
    private final BffEServiceTemplateRequestFactory factory = new BffEServiceTemplateRequestFactory();
    private final EServiceTemplateInstanceUseCase instances = new EServiceTemplateInstanceUseCase(
            gateway, factory, new EServiceTemplateVersionUseCase(versionGateway, factory),
            new EServiceDescriptorUseCase(descriptorGateway, new BffEServiceRequestFactory()));
    private final EServiceTemplateVersion published = EServiceTemplateVersion.builder().id(UUID.randomUUID())
            .version("1").state(EServiceTemplateVersionState.PUBLISHED).build();
    private final EServiceTemplate template = EServiceTemplate.builder().id(UUID.randomUUID()).version(published)
            .technology(EServiceTechnology.SOAP).build();
    private final EServiceDescriptor draft = EServiceDescriptor.builder().id(UUID.randomUUID())
            .version("1").state(DRAFT).build();
    private final EService service = EService.builder().id(UUID.randomUUID())
            .descriptors(new java.util.ArrayList<>(List.of(draft))).build();

    @Test
    void ordinaryInstanceCreationStillOnlyCreatesDraftWithoutConfiguringOrPublishing() {
        when(gateway.createEServiceInstance(eq(template), any())).thenReturn(service);

        assertThat(instances.createEServiceInstance(template)).isSameAs(service);

        assertThat(service.getLastDraftDescriptor().getState()).isEqualTo(DRAFT);
        verify(gateway).createEServiceInstance(eq(template), any());
        verifyNoInteractions(versionGateway, descriptorGateway);
    }

    @ParameterizedTest
    @EnumSource(EServiceTechnology.class)
    void publishedInstanceConfiguresEndpointsAndMetadataWithoutReplacingInterfaceAndPreservesAlias(
            EServiceTechnology technology
    ) throws IOException {
        var source = template.toBuilder().technology(technology).build();
        var command = factory.defaultCreationEServiceTemplateInstanceCommand();
        var store = new InMemoryEntityStore();
        when(gateway.createEServiceInstance(source, command)).thenAnswer(invocation -> {
            store.upsert(service);
            return service;
        });
        var document = new EServiceTemplateInterface(UUID.randomUUID(), source.getRef(), published.getRef(),
                "fixture", "application/xml",
                Files.readAllBytes(Path.of("src/main/resources/assets/eservice-template/soap11-due-indirizzi.wsdl")));
        when(versionGateway.downloadInterface(source.getRef(), published.getRef())).thenReturn(document);
        var configured = draft.toBuilder().interfaceDocument(new EServiceDescriptorInterface(
                it.pagopa.interop.common.kernel.domain.Document.builder().id(UUID.randomUUID()).build(),
                service.getRef(), draft.getRef(), new byte[]{1})).build();
        when(descriptorGateway.configureTemplateInstanceSOAPInterface(eq(service.getRef()), eq(draft.getRef()), any()))
                .thenReturn(configured);
        when(descriptorGateway.configureTemplateInstanceRESTInterface(eq(service.getRef()), eq(draft.getRef()), any()))
                .thenReturn(configured);
        when(descriptorGateway.updateDescriptor(eq(service.getRef()), eq(draft.getRef()), any())).thenReturn(configured);
        var ready = configured.toBuilder().state(PUBLISHED).build();
        when(descriptorGateway.publishDescriptor(service.getRef(), draft.getRef())).thenAnswer(invocation -> {
            store.upsert(service.toBuilder().descriptors(List.of(ready)).build());
            return ready;
        });

        new EServiceTemplateInstanceJourneyImpl(instances, store)
                .createPublishedEServiceTemplateInstance(source, command, "ES-1");

        assertThat(store.getByAliasOrThrow("ES-1", EService.class).getDescriptorOrThrow(1)).isEqualTo(ready);
        assertThat(service.getLastDraftDescriptor()).isSameAs(draft);
        var metadata = ArgumentCaptor.forClass(it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand.class);
        var order = inOrder(descriptorGateway);
        if (technology == EServiceTechnology.SOAP) {
            order.verify(descriptorGateway).configureTemplateInstanceSOAPInterface(service.getRef(), draft.getRef(), List.of(
                    new SoapAddressConfiguration("ServizioPagamenti", "PortaUno", "https://erogatore.example/soap"),
                    new SoapAddressConfiguration("ServizioPagamenti", "PortaDue", "https://erogatore.example/soap")));
        } else {
            order.verify(descriptorGateway).configureTemplateInstanceRESTInterface(eq(service.getRef()), eq(draft.getRef()), any());
        }
        order.verify(descriptorGateway).updateDescriptor(eq(service.getRef()), eq(draft.getRef()), metadata.capture());
        order.verify(descriptorGateway).publishDescriptor(service.getRef(), draft.getRef());
        assertThat(metadata.getValue().getInterfaceDocumentPath()).isNull();
        assertThat(((BffUpdateEServiceDescriptorCommand) metadata.getValue()).getBffPayload().getDailyCallsTotal()).isPositive();
    }

    @Test
    void failedPublicationStillLeavesAliasRegisteredForCleanup() {
        var command = factory.defaultCreationEServiceTemplateInstanceCommand();
        var useCase = mock(EServiceTemplateInstanceUseCase.class);
        when(useCase.createEServiceInstance(template, command)).thenReturn(service);
        doThrow(new IllegalStateException("failed")).when(useCase).publishEServiceInstance(template, service);
        var store = new InMemoryEntityStore();
        store.upsert(service);

        assertThatThrownBy(() -> new EServiceTemplateInstanceJourneyImpl(useCase, store)
                .createPublishedEServiceTemplateInstance(template, command, "ES-1"))
                .isInstanceOf(IllegalStateException.class).hasMessage("failed");
        assertThat(store.getByAliasOrThrow("ES-1", EService.class)).isSameAs(service);
    }

    @Test
    void aliasRegistrationUsesGatewaySnapshotInsteadOfOverwritingItWithReturnedInstance() {
        var command = factory.defaultCreationEServiceTemplateInstanceCommand();
        var useCase = mock(EServiceTemplateInstanceUseCase.class);
        when(useCase.createEServiceInstance(template, command)).thenReturn(service);
        var stored = service.toBuilder().name("gateway snapshot").build();
        var store = new InMemoryEntityStore();
        store.upsert(stored);

        new EServiceTemplateInstanceJourneyImpl(useCase, store)
                .createPublishedEServiceTemplateInstance(template, command, "ES-1");

        assertThat(store.getByAliasOrThrow("ES-1", EService.class)).isSameAs(stored);
        verify(useCase).publishEServiceInstance(template, stored);
    }
}
