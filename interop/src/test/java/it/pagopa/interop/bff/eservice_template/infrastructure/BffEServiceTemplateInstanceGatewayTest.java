package it.pagopa.interop.bff.eservice_template.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceDescriptorGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceGateway;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRestClient;
import it.pagopa.interop.bff.eservice_template.application.BffEServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInstance;
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
    private final ApiClient client = mock(ApiClient.class);
    private final EservicesApi api = mock(EservicesApi.class);
    private final EservicesApi.CreateEServiceInstanceFromTemplateOper operation =
            mock(EservicesApi.CreateEServiceInstanceFromTemplateOper.class);
    private final BffEServiceDescriptorGateway descriptorGateway = mock(BffEServiceDescriptorGateway.class);
    private final BffEServiceGateway serviceGateway = mock(BffEServiceGateway.class);
    private final InMemoryEntityStore entityStore = new InMemoryEntityStore();
    private final UUID descriptorId = UUID.randomUUID();

    private BffEServiceTemplateInstanceGateway gatewayForCreation(UUID serviceId) {
        when(client.eservices()).thenReturn(api);
        when(api.createEServiceInstanceFromTemplate()).thenReturn(operation);
        when(operation.templateIdPath(any())).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON)
                    .setBody("{\"id\":\"" + serviceId + "\",\"descriptorId\":\"" + descriptorId + "\"}").build());
        });
        var restClient = new BffEServiceRestClient(new TestChainFactory(
                mock(LastApiResponseStore.class), entityStore), client);
        return new BffEServiceTemplateInstanceGateway(restClient, serviceGateway, descriptorGateway, entityStore);
    }

    @Test
    void typedCommandReachesTransportAndRegistersActualLabelOnFreshAliasedTemplate() {
        var service = EService.builder().id(UUID.randomUUID()).build();
        var gateway = gatewayForCreation(service.getId());
        when(serviceGateway.getEService(service.getRef())).thenReturn(service);
        var template = EServiceTemplate.builder().id(UUID.randomUUID()).name("stale").build();
        var other = new EServiceTemplateInstance("others-label", EServiceRef.of(UUID.randomUUID()));
        var fresh = template.toBuilder().name("fresh").description("updated description").instance(other).build();
        entityStore.upsert("template", fresh);
        var command = new BffEServiceTemplateInstanceCreationCommand();
        command.instanceLabel("eservice-1");

        var instance = gateway.createEServiceInstance(template, command);
        var association = new EServiceTemplateInstance("eservice-1", service.getRef());
        assertThat(instance).isSameAs(service);
        assertThat(entityStore.getByAliasOrThrow("template", EServiceTemplate.class))
                .isEqualTo(fresh.toBuilder().instance(association).build());
        assertThat(fresh.getInstances()).containsExactly(other);

        verify(operation).templateIdPath(template.getId());
        verify(operation).body(same(command.getBffPayload()));
        assertThat(command.getBffPayload().getInstanceLabel()).isEqualTo("eservice-1");
        verify(descriptorGateway).getEServiceDescriptor(service.getRef(), EServiceDescriptorRef.of(descriptorId));
        verifyNoMoreInteractions(descriptorGateway);
        verify(api).createEServiceInstanceFromTemplate();
        verifyNoMoreInteractions(api);
    }

    @Test
    void registrationSurvivesFailedDescriptorRefreshAndRepeatedIdDoesNotDuplicate() {
        var serviceRef = EServiceRef.of(UUID.randomUUID());
        var gateway = gatewayForCreation(serviceRef.id());
        var template = EServiceTemplate.builder().id(UUID.randomUUID()).name("template").build();
        var command = new BffEServiceTemplateInstanceCreationCommand();
        command.instanceLabel("first");
        var refreshFailure = new IllegalStateException("descriptor refresh failed");
        when(descriptorGateway.getEServiceDescriptor(serviceRef, EServiceDescriptorRef.of(descriptorId)))
                .thenThrow(refreshFailure);

        assertThatThrownBy(() -> gateway.createEServiceInstance(template, command)).isSameAs(refreshFailure);
        assertThat(entityStore.getById(template.getId(), EServiceTemplate.class).orElseThrow().getInstances())
                .containsExactly(new EServiceTemplateInstance("first", serviceRef));
        verifyNoInteractions(serviceGateway);

        command.instanceLabel("second");
        assertThatThrownBy(() -> gateway.createEServiceInstance(template, command)).isSameAs(refreshFailure);
        assertThat(entityStore.getById(template.getId(), EServiceTemplate.class).orElseThrow().getInstances())
                .containsExactly(new EServiceTemplateInstance("second", serviceRef));
    }
}
