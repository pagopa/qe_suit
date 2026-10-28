package it.pagopa.interop.bff.eservice.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.RejectDelegatedEServiceDescriptorSeed;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

import java.util.UUID;
import java.util.function.Function;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffEServiceDescriptorRejectionTest {
    @ParameterizedTest
    @CsvSource({"204,false", "400,false", "403,false", "409,false", "500,false",
            "400,true", "409,true", "500,true", "403,true"})
    void submitsRejectionOnceAndReconcilesOnlyConfirmedDraftRaces(int status, boolean alreadyDraft) {
        var client = mock(ApiClient.class);
        var api = mock(EservicesApi.class);
        var operation = mock(EservicesApi.RejectDelegatedEServiceDescriptorOper.class);
        var serviceRef = EServiceRef.of(UUID.randomUUID());
        var descriptorRef = EServiceDescriptorRef.of(UUID.randomUUID());
        when(client.eservices()).thenReturn(api);
        when(api.rejectDelegatedEServiceDescriptor()).thenReturn(operation);
        when(operation.eServiceIdPath(serviceRef.id())).thenReturn(operation);
        when(operation.descriptorIdPath(descriptorRef.id())).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder().setStatusCode(status).build());
        });
        var entities = mock(EntityStore.class);
        var restClient = new BffEServiceDescriptorApprovalRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), entities), client);
        var descriptors = spy(new BffEServiceDescriptorGateway(mock(BffEServiceRestClient.class), entities,
                mock(BffEServiceDescriptorMapper.class), mock(BffEServiceDescriptorInterfaceGateway.class),
                mock(BffEServiceDescriptorArchiveGateway.class), mock(BffEServiceDescriptorUpdateGateway.class)));
        var gateway = new BffEServiceDescriptorApprovalGateway(restClient, descriptors);
        var pending = EServiceDescriptor.builder().id(descriptorRef.id()).state(WAITING_FOR_APPROVAL).build();
        var draft = pending.toBuilder().state(DRAFT).build();
        if (status == 204) {
            doReturn(pending, pending, draft).when(descriptors).getEServiceDescriptor(serviceRef, descriptorRef);
        } else {
            doReturn(alreadyDraft ? draft : pending).when(descriptors)
                    .getEServiceDescriptor(serviceRef, descriptorRef);
        }

        if (status == 204) {
            assertThat(gateway.rejectDescriptor(serviceRef, descriptorRef, "Cleanup dello scenario")).isSameAs(draft);
            verify(descriptors, times(3)).getEServiceDescriptor(serviceRef, descriptorRef);
        } else if (alreadyDraft && status != 403) {
            assertThat(gateway.rejectDescriptor(serviceRef, descriptorRef, "Cleanup dello scenario")).isSameAs(draft);
            verify(descriptors).getEServiceDescriptor(serviceRef, descriptorRef);
        } else {
            assertThatThrownBy(() -> gateway.rejectDescriptor(serviceRef, descriptorRef, "Cleanup dello scenario"))
                    .isInstanceOf(IllegalStateException.class).hasMessageContaining("HTTP " + status);
            if (status == 403) {
                verifyNoInteractions(descriptors);
            } else {
                verify(descriptors).getEServiceDescriptor(serviceRef, descriptorRef);
            }
        }

        var payload = ArgumentCaptor.forClass(RejectDelegatedEServiceDescriptorSeed.class);
        verify(operation).body(payload.capture());
        assertThat(payload.getValue().getRejectionReason()).isEqualTo("Cleanup dello scenario");
        verify(operation, times(1)).execute(any());
        verify(api, never()).scheduleArchiveEservice();
    }
}
