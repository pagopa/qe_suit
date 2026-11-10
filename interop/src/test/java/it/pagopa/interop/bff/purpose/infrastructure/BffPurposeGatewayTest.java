package it.pagopa.interop.bff.purpose.infrastructure;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.PurposesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionSeed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffPurposeGatewayTest {
    private final InMemoryEntityStore store = new InMemoryEntityStore();
    private final ApiClient apiClient = mock(ApiClient.class);
    private final PurposesApi api = mock(PurposesApi.class);
    private final PurposesApi.CreatePurposeVersionOper create = mock(PurposesApi.CreatePurposeVersionOper.class);
    private final PurposesApi.GetPurposeOper read = mock(PurposesApi.GetPurposeOper.class);
    private final BffPurposeMapper mapper = mock(BffPurposeMapper.class);
    private final UUID newVersionId = UUID.randomUUID();
    private final PurposeVersion active = PurposeVersion.builder().id(UUID.randomUUID())
            .createdAt(Instant.EPOCH).purposeVersionState(PurposeVersionState.ACTIVE).dailyCalls(10).build();
    private final Purpose original = Purpose.builder().id(UUID.randomUUID()).version(active).build();
    private final Purpose updated = original.toBuilder().version(active.toBuilder().id(newVersionId)
            .purposeVersionState(PurposeVersionState.WAITING_FOR_APPROVAL).dailyCalls(51).build()).build();
    private BffPurposeGateway gateway;

    @BeforeEach
    void setUp() {
        when(apiClient.purposes()).thenReturn(api);
        when(api.createPurposeVersion()).thenReturn(create);
        when(create.purposeIdPath(original.getId())).thenReturn(create);
        when(create.body(any())).thenReturn(create);
        when(api.getPurpose()).thenReturn(read);
        when(read.purposeIdPath(original.getId())).thenReturn(read);
        gateway = new BffPurposeGateway(
                new BffPurposeRestClient(new TestChainFactory(mock(LastApiResponseStore.class), store), apiClient),
                mapper, mock(BffPurposeCommandFactory.class));
        store.upsert("purpose", original);
    }

    @Test
    void createsTheVersionOnceAndWaitsForItsExactIdBeforeRefreshingTheAliasedContext() {
        when(create.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{\"purposeId\":\"" + original.getId()
                    + "\",\"versionId\":\"" + newVersionId + "\"}"));
        });
        when(read.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(200, "{}"));
        });
        Purpose unrelatedWaiting = original.toBuilder().version(updated.getLastWaitingForApprovalVersion().toBuilder()
                .id(UUID.randomUUID()).build()).build();
        when(mapper.toPurpose(any())).thenReturn(unrelatedWaiting, updated);

        assertThat(gateway.updatePurposeDailyCalls(original.getRef(), 51)).isSameAs(updated);

        verify(create).purposeIdPath(original.getId());
        verify(create).body(new PurposeVersionSeed().dailyCalls(51));
        verify(create).execute(any());
        verify(read, times(2)).execute(any());
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(updated);
        assertThat(updated.getLastActiveVersion()).isSameAs(active);
    }

    @Test
    void failsWithoutRetryingTheWriteOrReadingThePurposeOnAuthorizationFailure() {
        when(create.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(response(403, "{}"));
        });

        assertThatThrownBy(() -> gateway.updatePurposeDailyCalls(original.getRef(), 51))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining(original.getId().toString());

        verify(create).execute(any());
        verifyNoInteractions(read, mapper);
        assertThat(store.getByAliasOrThrow("purpose", Purpose.class)).isSameAs(original);
    }

    private Response response(int status, String body) {
        return new ResponseBuilder().setStatusCode(status).setContentType(ContentType.JSON).setBody(body).build();
    }
}
