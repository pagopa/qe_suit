package it.pagopa.interop.bff.eservice.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.response.RawResponse;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.bff.infrastructure.mapping.BffCommonMapper;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.EServiceTemplateRef;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffEServiceDescriptorUpdateGatewayTest {
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void sameApplicationCommandRoutesToCorrectEndpointAndRefreshesContext(boolean templateInstance) {
        var store = new InMemoryEntityStore();
        var chains = new TestChainFactory(mock(LastApiResponseStore.class), store);
        var apiClient = mock(ApiClient.class);
        var api = mock(EservicesApi.class);
        var operation = mock(EservicesApi.UpdateDraftDescriptorTemplateInstanceOper.class);
        when(apiClient.eservices()).thenReturn(api);
        when(api.updateDraftDescriptorTemplateInstance()).thenReturn(operation);
        when(operation.eServiceIdPath(any())).thenReturn(operation);
        when(operation.descriptorIdPath(any())).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON)
                    .setBody("{\"id\":\"" + UUID.randomUUID() + "\"}").build());
        });
        var restClient = mock(BffEServiceRestClient.class);
        var instanceClient = new BffEServiceTemplateInstanceDescriptorRestClient(chains, apiClient);
        var mapper = mock(BffEServiceDescriptorMapper.class);
        var interfaces = mock(BffEServiceDescriptorInterfaceGateway.class);
        var gateway = new BffEServiceDescriptorGateway(restClient, store, mapper, interfaces,
                mock(BffEServiceDescriptorArchiveGateway.class),
                new BffEServiceDescriptorUpdateGateway(restClient, instanceClient, interfaces, store));
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID())
                .templateRef(templateInstance ? EServiceTemplateRef.of(UUID.randomUUID()) : null)
                .dailyCallsPerConsumer(1).dailyCallsTotal(10).build();
        var service = EService.builder().id(UUID.randomUUID()).asyncExchange(false)
                .descriptors(List.of(descriptor)).build();
        store.upsert("ES-1", service);
        var refreshed = service.toBuilder().name("refreshed by gateway").build();
        var source = new ProducerEServiceDescriptor();
        RawResponse readResponse = mock(RawResponse.class);
        when(readResponse.as(ProducerEServiceDescriptor.class)).thenReturn(source);
        when(restClient.readDescriptor(service.getId(), descriptor.getId()))
                .thenAnswer(invocation -> chains.build(() -> readResponse, ProducerEServiceDescriptor.class));
        when(mapper.toEServiceWithUpsert(eq(source), any())).thenReturn(refreshed);
        RawResponse updateResponse = mock(RawResponse.class);
        when(restClient.updateDescriptor(eq(service.getId()), eq(descriptor.getId()), any()))
                .thenAnswer(invocation -> chains.build(() -> updateResponse, CreatedResource.class));
        var command = new BffUpdateEServiceDescriptorCommand();
        command.dailyCallsPerConsumer(1).dailyCallsTotal(10).audience(List.of("QA"))
                .description("ordinary descriptor description").voucherLifespan(60);
        command.getBffPayload().agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC)
                .attributes(new DescriptorAttributesSeed())
                .asyncExchangeProperties(new AsyncExchangeProperties().responseTime(30)
                        .resourceAvailableTime(60).maxResultSet(2).confirmation(true).bulk(true));

        assertThat(gateway.updateDescriptor(service.getRef(), descriptor.getRef(), command)).isSameAs(descriptor);
        assertThat(store.getByAliasOrThrow("ES-1", EService.class)).isSameAs(refreshed);
        if (templateInstance) {
            var payload = ArgumentCaptor.forClass(UpdateEServiceDescriptorTemplateInstanceSeed.class);
            verify(operation).eServiceIdPath(service.getId());
            verify(operation).descriptorIdPath(descriptor.getId());
            verify(operation).body(payload.capture());
            assertThat(payload.getValue().getAudience()).containsExactly("QA");
            assertThat(payload.getValue().getDailyCallsPerConsumer()).isEqualTo(1);
            assertThat(payload.getValue().getDailyCallsTotal()).isEqualTo(10);
            assertThat(payload.getValue().getAgreementApprovalPolicy()).isEqualTo(AgreementApprovalPolicy.AUTOMATIC);
            assertThat(payload.getValue().getAttributes()).isSameAs(command.getBffPayload().getAttributes());
            assertThat(payload.getValue().getAsyncExchangeProperties()).isEqualTo(
                    new AsyncExchangePropertiesInstanceSeed().responseTime(30).resourceAvailableTime(60).maxResultSet(2));
            verify(restClient, never()).updateDescriptor(any(), any(), any());
        } else {
            verify(restClient).updateDescriptor(service.getId(), descriptor.getId(), command.getBffPayload());
            verifyNoInteractions(operation);
        }
        verifyNoInteractions(interfaces);
    }

    @Test
    void templateInstanceWithoutAsyncOverridesPreservesInheritedConfiguration() {
        var store = new InMemoryEntityStore();
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID())
                .templateRef(EServiceTemplateRef.of(UUID.randomUUID())).build();
        var service = EService.builder().id(UUID.randomUUID()).asyncExchange(true)
                .descriptors(List.of(descriptor)).build();
        store.upsert(service);
        var restClient = mock(BffEServiceRestClient.class);
        var instanceClient = mock(BffEServiceTemplateInstanceDescriptorRestClient.class, RETURNS_DEEP_STUBS);
        var interfaces = mock(BffEServiceDescriptorInterfaceGateway.class);
        var command = new BffUpdateEServiceDescriptorCommand();

        new BffEServiceDescriptorUpdateGateway(restClient, instanceClient, interfaces, store)
                .update(service.getRef(), descriptor.getRef(), command);

        var payload = ArgumentCaptor.forClass(UpdateEServiceDescriptorTemplateInstanceSeed.class);
        verify(instanceClient).updateDraftDescriptor(eq(service.getId()), eq(descriptor.getId()), payload.capture());
        assertThat(payload.getValue().getAsyncExchangeProperties()).isNull();
        verifyNoInteractions(restClient, interfaces);
    }

    @Test
    void ordinaryAsyncDescriptorStillGetsCallbackAndDefaultProperties() {
        var store = new InMemoryEntityStore();
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID()).build();
        var service = EService.builder().id(UUID.randomUUID()).asyncExchange(true)
                .descriptors(List.of(descriptor)).build();
        store.upsert(service);
        var restClient = mock(BffEServiceRestClient.class, RETURNS_DEEP_STUBS);
        var instanceClient = mock(BffEServiceTemplateInstanceDescriptorRestClient.class);
        var interfaces = mock(BffEServiceDescriptorInterfaceGateway.class);
        var command = new BffUpdateEServiceDescriptorCommand();

        new BffEServiceDescriptorUpdateGateway(restClient, instanceClient, interfaces, store)
                .update(service.getRef(), descriptor.getRef(), command);

        verify(interfaces).linkDocument(service.getRef(), descriptor.getRef(), "assets/origin-interface.yaml",
                it.pagopa.interop.common.kernel.domain.DocumentKind.ASYNC_EXCHANGE_CALLBACK_INTERFACE);
        assertThat(command.getBffPayload().getAsyncExchangeProperties()).isEqualTo(new AsyncExchangeProperties()
                .responseTime(60).resourceAvailableTime(60).maxResultSet(1).confirmation(false).bulk(true));
        verify(restClient).updateDescriptor(service.getId(), descriptor.getId(), command.getBffPayload());
        verifyNoInteractions(instanceClient);
    }

    @Test
    void mapsTemplateReferenceIdFromBffContract() {
        UUID templateId = UUID.randomUUID();
        var reference = Mappers.getMapper(BffCommonMapper.class).toTemplateRef(
                new it.pagopa.interop.generated.openapi.clients.bff.model.EServiceTemplateRef()
                        .templateId(templateId));

        assertThat(reference.id()).isEqualTo(templateId);
    }

    @Test
    void rejectsReplacingTemplateInstanceInterfaceBeforeMakingAnyRequest() {
        var store = new InMemoryEntityStore();
        var descriptor = EServiceDescriptor.builder().id(UUID.randomUUID())
                .templateRef(EServiceTemplateRef.of(UUID.randomUUID())).build();
        var service = EService.builder().id(UUID.randomUUID()).descriptors(List.of(descriptor)).build();
        store.upsert(service);
        var restClient = mock(BffEServiceRestClient.class);
        var instanceClient = mock(BffEServiceTemplateInstanceDescriptorRestClient.class);
        var interfaces = mock(BffEServiceDescriptorInterfaceGateway.class);
        var command = new BffUpdateEServiceDescriptorCommand();
        command.interfaceDocumentPath("assets/origin-interface.yaml");

        assertThatThrownBy(() -> new BffEServiceDescriptorUpdateGateway(
                restClient, instanceClient, interfaces, store).update(service.getRef(), descriptor.getRef(), command))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("configured, not replaced");
        verifyNoInteractions(restClient, instanceClient, interfaces);
    }
}
