package it.pagopa.interop.bff.eservice_template.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.response.Response;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.infrastructure.template.action.strategy.PollingStrategy;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EserviceTemplatesApi;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BffEServiceTemplateLifecycleRestClientTest {
    @Test
    void suspendVersionSetsJsonContentTypeOnTheTransportRequest() throws Exception {
        ApiClient apiClient = mock(ApiClient.class);
        EserviceTemplatesApi api = mock(EserviceTemplatesApi.class);
        EserviceTemplatesApi.SuspendEServiceTemplateVersionOper operation =
                mock(EserviceTemplatesApi.SuspendEServiceTemplateVersionOper.class);
        when(apiClient.eserviceTemplates()).thenReturn(api);
        when(api.suspendEServiceTemplateVersion()).thenReturn(operation);
        when(operation.eServiceTemplateIdPath(any())).thenReturn(operation);
        when(operation.eServiceTemplateVersionIdPath(any())).thenReturn(operation);
        when(operation.reqSpec(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> responseHandler = invocation.getArgument(0);
            return responseHandler.apply(new ResponseBuilder().setStatusCode(200).build());
        });

        var client = new BffEServiceTemplateLifecycleRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), new InMemoryEntityStore()), apiClient);
        client.suspendVersion(UUID.randomUUID(), UUID.randomUUID())
                .withPolling(PollingStrategy.UNTIL_SUCCESS).get();

        ArgumentCaptor<Consumer> requestConfigurer = ArgumentCaptor.forClass(Consumer.class);
        verify(operation).reqSpec(requestConfigurer.capture());
        Type callbackType = EserviceTemplatesApi.SuspendEServiceTemplateVersionOper.class
                .getMethod("reqSpec", Consumer.class).getGenericParameterTypes()[0];
        Class<?> requestSpecType = (Class<?>) ((ParameterizedType) callbackType).getActualTypeArguments()[0];
        Object requestSpec = mock(requestSpecType);
        @SuppressWarnings("unchecked")
        Consumer<Object> configure = requestConfigurer.getValue();
        configure.accept(requestSpec);

        boolean configuresJson = mockingDetails(requestSpec).getInvocations().stream()
                .anyMatch(invocation -> invocation.getMethod().getName().equals("setContentType")
                        && invocation.getArgument(0).equals("application/json"));
        assertThat(configuresJson).isTrue();
    }
}
