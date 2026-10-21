package it.pagopa.interop.bff.eservice_template.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRestClient;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.kernel.domain.EServiceDescriptorRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffEServiceTemplateInstanceGatewayTest {
    @Test
    void typedCommandReachesRealTestChainTransportAndGatewayOnlyRefreshesCreatedDraft() {
        var client = mock(ApiClient.class);
        var api = mock(EservicesApi.class);
        var operation = mock(EservicesApi.CreateEServiceInstanceFromTemplateOper.class);
        var descriptorGateway = mock(BffEServiceDescriptorGateway.class);
        var serviceGateway = mock(BffEServiceGateway.class);
        when(client.eservices()).thenReturn(api);
        when(api.createEServiceInstanceFromTemplate()).thenReturn(operation);
        when(operation.templateIdPath(any())).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        UUID serviceId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON)
                    .setBody("{\"id\":\"" + serviceId + "\",\"descriptorId\":\"" + descriptorId + "\"}").build());
        });
        var service = EService.builder().id(serviceId).build();
        when(serviceGateway.getEService(EServiceRef.of(serviceId))).thenReturn(service);
        var restClient = new BffEServiceRestClient(new TestChainFactory(
                mock(LastApiResponseStore.class), mock(EntityStore.class)), client);
        var gateway = new BffEServiceTemplateInstanceGateway(restClient, serviceGateway, descriptorGateway);
        var template = EServiceTemplate.builder().id(UUID.randomUUID()).build();
        var command = new BffEServiceTemplateInstanceCreationCommand();
        command.instanceLabel("eservice-1");

        assertThat(gateway.createEServiceInstance(template, command)).isSameAs(service);

        verify(operation).templateIdPath(template.getId());
        verify(operation).body(same(command.getBffPayload()));
        assertThat(command.getBffPayload().getInstanceLabel()).isEqualTo("eservice-1");
        verify(descriptorGateway).getEServiceDescriptor(EServiceRef.of(serviceId), EServiceDescriptorRef.of(descriptorId));
        verifyNoMoreInteractions(descriptorGateway);
        verify(api).createEServiceInstanceFromTemplate();
        verifyNoMoreInteractions(api);
    }
}
