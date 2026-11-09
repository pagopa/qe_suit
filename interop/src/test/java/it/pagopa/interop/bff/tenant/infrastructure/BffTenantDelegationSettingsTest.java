package it.pagopa.interop.bff.tenant.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.TenantsApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.TenantDelegatedFeaturesFlagsUpdateSeed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffTenantDelegationSettingsTest {
    private static final UUID TENANT_ID = UUID.fromString("80bc8c2a-0f36-47b5-9b7f-7b74c73d2b20");
    private final TenantsApi api = mock(TenantsApi.class);
    private final TenantsApi.GetTenantOper read = mock(TenantsApi.GetTenantOper.class);
    private final TenantsApi.UpdateTenantDelegatedFeaturesOper update =
            mock(TenantsApi.UpdateTenantDelegatedFeaturesOper.class);
    private final EntityStore store = mock(EntityStore.class);
    private final BffTenantGateway gateway;

    BffTenantDelegationSettingsTest() {
        var client = mock(ApiClient.class);
        when(client.tenants()).thenReturn(api);
        when(api.getTenant()).thenReturn(read);
        when(read.tenantIdPath(TENANT_ID)).thenReturn(read);
        when(api.updateTenantDelegatedFeatures()).thenReturn(update);
        when(update.body(any())).thenReturn(update);
        gateway = new BffTenantGateway(new BffTenantRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), store), client),
                mock(BffTenantRequestFactory.class));
    }

    @Test
    void alreadyEnabledIsReadOnlyOnEveryInvocation() {
        reads(200, tenant(true, true));
        gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID));
        gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID));

        verify(read, times(2)).tenantIdPath(TENANT_ID);
        verify(read, times(2)).execute(any());
        verify(api, never()).updateTenantDelegatedFeatures();
        verifyNoInteractions(store);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void enablesAbsentProducerPreservingConsumerAndLeavingOtherSettingsOutOfThePayload(boolean consumer)
            throws Exception {
        AtomicInteger reads = new AtomicInteger();
        when(read.execute(any())).thenAnswer(call -> handle(call.getArgument(0), 200,
                tenant(reads.getAndIncrement() > 0, consumer)));
        writes(204);

        gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID));
        gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID));

        var payload = ArgumentCaptor.forClass(TenantDelegatedFeaturesFlagsUpdateSeed.class);
        verify(update).body(payload.capture());
        assertThat(new ObjectMapper().valueToTree(payload.getValue()).toString())
                .isEqualTo("{\"isDelegatedConsumerFeatureEnabled\":" + consumer
                        + ",\"isDelegatedProducerFeatureEnabled\":true}");
        verify(read, times(3)).execute(any());
        assertThat(TenantsApi.UpdateTenantDelegatedFeaturesOper.REQ_URI)
                .isEqualTo("/tenants/delegatedFeatures/update");
        assertThat(TenantsApi.UpdateTenantDelegatedFeaturesOper.REQ_METHOD.name()).isEqualTo("POST");
        verifyNoInteractions(store);
    }

    @Test
    void waitsForSuccessfulReadbackRatherThanAssumingWriteMadeTheFeatureVisible() {
        AtomicInteger reads = new AtomicInteger();
        when(read.execute(any())).thenAnswer(call -> handle(call.getArgument(0), 200,
                tenant(reads.getAndIncrement() > 1, false)));
        writes(204);

        gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID));

        verify(read, times(3)).execute(any());
        verify(update).execute(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {403, 500})
    void readFailureIsNotAbsenceAndDoesNotWrite(int status) {
        reads(status, "{}");
        assertThatThrownBy(() -> gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID)))
                .isInstanceOf(AssertionError.class).hasMessageContaining(Integer.toString(status));
        verify(api, never()).updateTenantDelegatedFeatures();
        verify(read).execute(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {403, 500})
    void failedWriteIsNotSuccessAndIsNotRetried(int status) {
        reads(200, tenant(false, true));
        writes(status);
        assertThatThrownBy(() -> gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining(TENANT_ID.toString());
        verify(read).execute(any());
        verify(update).execute(any());
    }

    @Test
    void readbackAuthorizationFailureFailsImmediately() {
        AtomicInteger attempts = new AtomicInteger();
        when(read.execute(any())).thenAnswer(call -> {
            boolean initial = attempts.getAndIncrement() == 0;
            return handle(call.getArgument(0), initial ? 200 : 403, initial ? tenant(false, false) : "{}");
        });
        writes(204);

        assertThatThrownBy(() -> gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID)))
                .isInstanceOf(IllegalStateException.class);
        verify(read, times(2)).execute(any());
        verify(update).execute(any());
    }

    @Test
    void rejectsWrongTenantOrNullConfigurationBeforeWriting() {
        reads(200, "{\"id\":\"00000000-0000-0000-0000-000000000001\",\"features\":[]}");
        assertThatThrownBy(() -> gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID)))
                .isInstanceOf(IllegalStateException.class);
        reads(200, "{\"id\":\"" + TENANT_ID + "\",\"features\":null}");
        assertThatThrownBy(() -> gateway.ensureProducerDelegationEnabled(TenantRef.of(TENANT_ID)))
                .isInstanceOf(IllegalStateException.class);
        verify(api, never()).updateTenantDelegatedFeatures();
    }

    private void reads(int status, String body) {
        doAnswer(call -> handle(call.getArgument(0), status, body)).when(read).execute(any());
    }

    private void writes(int status) {
        when(update.execute(any())).thenAnswer(call -> handle(call.getArgument(0), status, "{}"));
    }

    private Response handle(Function<Response, Response> handler, int status, String body) {
        return handler.apply(new ResponseBuilder().setStatusCode(status).setContentType(ContentType.JSON)
                .setBody(body).build());
    }

    private String tenant(boolean producer, boolean consumer) {
        return "{\"id\":\"" + TENANT_ID + "\",\"features\":[{\"certifier\":{\"certifierId\":\"unchanged\"}}"
                + (producer ? ",{\"delegatedProducer\":{\"availabilityTimestamp\":\"2026-10-20T10:00:00Z\"}}" : "")
                + (consumer ? ",{\"delegatedConsumer\":{\"availabilityTimestamp\":\"2026-10-20T10:00:00Z\"}}" : "")
                + "]}";
    }
}
