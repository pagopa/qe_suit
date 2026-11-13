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
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.infrastructure.response.ApiResponse;
import it.pagopa.interop.bff.infrastructure.cucumber.BffErrorMessageSteps;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateInstanceUseCase;
import it.pagopa.interop.common.eservice_template.application.command.EServiceTemplateInstanceCreationCommand;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateInstanceCreationFailureReason;
import it.pagopa.interop.common.eservice_template.infrastructure.cucumber.EServiceTemplateInstanceSteps;
import it.pagopa.interop.common.infrastructure.copy.ExpectedErrorMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffEServiceTemplateInstanceGatewayTest {
    @ParameterizedTest
    @ValueSource(ints = {400, 200, 500})
    void linkInLabelIsSentOnceAndOnlyExpectedRejectionReachesErrorMessageAssertion(int status) throws Exception {
        String messageId = "CATALOG_HYPERLINK_DETECTION_ERROR__POST_TEMPLATES_TEMPLATE_ID_ESERVICES";
        String body = new ObjectMapper().writeValueAsString(Map.of("userMessage",
                ExpectedErrorMessages.load().text(messageId, "it")));
        var client = mock(ApiClient.class);
        var api = mock(EservicesApi.class);
        var operation = mock(EservicesApi.CreateEServiceInstanceFromTemplateOper.class);
        when(client.eservices()).thenReturn(api);
        when(api.createEServiceInstanceFromTemplate()).thenReturn(operation);
        when(operation.templateIdPath(any())).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder().setStatusCode(status).setContentType(ContentType.JSON)
                    .setBody(body).build());
        });
        var responses = mock(LastApiResponseStore.class);
        doAnswer(invocation -> {
            when(responses.getLastResponse()).thenReturn(invocation.getArgument(0));
            return null;
        }).when(responses).setLastResponse(any(ApiResponse.class));
        var entities = mock(EntityStore.class);
        var restClient = new BffEServiceRestClient(new TestChainFactory(responses, entities), client);
        var serviceGateway = mock(BffEServiceGateway.class);
        var descriptorGateway = mock(BffEServiceDescriptorGateway.class);
        var gateway = new BffEServiceTemplateInstanceGateway(restClient, serviceGateway, descriptorGateway, entities);
        var instances = new EServiceTemplateInstanceUseCase(
                gateway, new BffEServiceTemplateRequestFactory(), null, null);
        var steps = new EServiceTemplateInstanceSteps(instances, null, null, entities);
        var template = EServiceTemplate.builder().id(UUID.randomUUID()).build();

        if (status == 400) {
            steps.attemptToCreateTemplateInstanceWithLinkInLabel(template);
            new BffErrorMessageSteps(responses).assertErrorMessage(messageId, "it");
        } else {
            assertThatThrownBy(() -> steps.attemptToCreateTemplateInstanceWithLinkInLabel(template))
                    .isInstanceOf(AssertionError.class);
        }

        verify(operation).templateIdPath(template.getId());
        verify(operation).body(argThat(seed -> seed.getInstanceLabel().equals("https://example.com")));
        verify(operation, times(1)).execute(any());
        verify(responses).setLastResponse(any(ApiResponse.class));
        verifyNoInteractions(serviceGateway, descriptorGateway, entities);
    }

    @Test
    void negativeCreationRejectsCommandsFromOtherChannelsBeforeTransport() {
        var restClient = mock(BffEServiceRestClient.class);
        var gateway = new BffEServiceTemplateInstanceGateway(restClient, null, null, mock(EntityStore.class));

        assertThatThrownBy(() -> gateway.shouldFailToCreateEServiceInstance(
                EServiceTemplate.builder().id(UUID.randomUUID()).build(),
                mock(EServiceTemplateInstanceCreationCommand.class),
                EServiceTemplateInstanceCreationFailureReason.HYPERLINK_DETECTED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expected BffEServiceTemplateInstanceCreationCommand");
        verifyNoInteractions(restClient);
    }

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
        var entities = mock(EntityStore.class);
        var gateway = new BffEServiceTemplateInstanceGateway(restClient, serviceGateway, descriptorGateway, entities);
        var template = EServiceTemplate.builder().id(UUID.randomUUID()).build();
        var command = new BffEServiceTemplateInstanceCreationCommand();
        command.instanceLabel("eservice-1");

        var created = gateway.createEServiceInstance(template, command);
        assertThat(created.getId()).isEqualTo(service.getId());
        assertThat(created.getInstanceLabel()).isEqualTo("eservice-1");
        verify(entities).upsert(created);

        verify(operation).templateIdPath(template.getId());
        verify(operation).body(same(command.getBffPayload()));
        assertThat(command.getBffPayload().getInstanceLabel()).isEqualTo("eservice-1");
        verify(descriptorGateway).getEServiceDescriptor(EServiceRef.of(serviceId), EServiceDescriptorRef.of(descriptorId));
        verifyNoMoreInteractions(descriptorGateway);
        verify(api).createEServiceInstanceFromTemplate();
        verifyNoMoreInteractions(api);
    }
}
