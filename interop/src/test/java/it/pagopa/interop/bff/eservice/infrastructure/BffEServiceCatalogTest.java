package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.generated.openapi.clients.bff.ApiClient;
import it.pagopa.interop.generated.openapi.clients.bff.api.EservicesApi;
import it.pagopa.interop.generated.openapi.clients.bff.model.*;
import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BffEServiceCatalogTest {
    private final BffEServiceCatalogMapper mapper = Mappers.getMapper(BffEServiceCatalogMapper.class);

    @Test
    void mapsOrderedEntriesMetadataAndDescriptorWithoutChangingSource() {
        UUID id = UUID.randomUUID();
        UUID producerId = UUID.randomUUID();
        UUID descriptorId = UUID.randomUUID();
        CatalogEServices response = new CatalogEServices().results(List.of(
                new CatalogEService().id(id).name("Nebula").description("description")
                        .producer(new CatalogTenant().id(producerId)).isMine(false).hasUnreadNotifications(true)
                        .activeDescriptor(new CompactDescriptor().id(descriptorId).version("2")
                                .state(it.pagopa.interop.generated.openapi.clients.bff.model.EServiceDescriptorState.PUBLISHED)),
                new CatalogEService().id(id).name("duplicate kept")))
                .pagination(new Pagination().offset(10).limit(20).totalCount(32));

        var result = mapper.toCatalog(response);

        assertThat(result.getOffset()).isEqualTo(10);
        assertThat(result.getLimit()).isEqualTo(20);
        assertThat(result.getTotalCount()).isEqualTo(32);
        assertThat(result.getResults()).hasSize(2);
        var first = result.getResults().get(0);
        assertThat(first.getId()).isEqualTo(id);
        assertThat(first.getProducerId()).isEqualTo(producerId);
        assertThat(first.getDescriptorId()).isEqualTo(descriptorId);
        assertThat(first.getVersion()).isEqualTo("2");
        assertThat(first.getState()).isEqualTo(EServiceDescriptorState.PUBLISHED);
        assertThat(first.getIsMine()).isFalse();
        assertThat(first.getHasUnreadNotifications()).isTrue();
        assertThat(result.getResults().get(1).getDescriptorId()).isNull();
    }

    @Test
    void executesPostCatalogWithExactPayloadThroughUseCaseAndGateway() {
        ApiClient apiClient = mock(ApiClient.class);
        EservicesApi api = mock(EservicesApi.class);
        EservicesApi.QueryEServicesCatalogOper operation = mock(EservicesApi.QueryEServicesCatalogOper.class);
        when(apiClient.eservices()).thenReturn(api);
        when(api.queryEServicesCatalog()).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON)
                    .setBody("{\"results\":[],\"pagination\":{\"offset\":0,\"limit\":20,\"totalCount\":0}}").build());
        });
        var client = new BffEServiceCatalogRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)), apiClient);
        var gateway = new BffEServiceCatalogGateway(client, mapper);
        var command = (BffSearchEServiceCatalogCommand) new BffEServiceRequestFactory().defaultSearchEServiceCatalogCommand();
        command.keyword("Nebula").onlyActiveEservices(true);
        command.producerCategories(List.of(it.pagopa.interop.common.tenant.domain.TenantCategory.COMUNI));

        var result = new EServiceCatalogUseCase(gateway).search(command);

        assertThat(result.getResults()).isEmpty();
        assertThat(result.getLimit()).isEqualTo(20);
        verify(operation).body(same(command.getBffPayload()));
        assertThat(command.getBffPayload().getProducerCategories()).containsExactly(EServiceProducerCategory.COMUNI);
        verify(operation, times(1)).execute(any());
        assertThat(EservicesApi.QueryEServicesCatalogOper.REQ_METHOD.name()).isEqualTo("POST");
        assertThat(EservicesApi.QueryEServicesCatalogOper.REQ_URI).isEqualTo("/catalog");
        assertThat(gateway.supports(Channel.BFF)).isTrue();
        assertThat(gateway.supports(Channel.WEB_BROWSER)).isFalse();
    }

    @Test
    void rejectsCommandsForOtherChannelsAndMissingPagination() {
        var client = mock(BffEServiceCatalogRestClient.class);
        var gateway = new BffEServiceCatalogGateway(client, mapper);

        assertThatThrownBy(() -> gateway.search(mock(SearchEServiceCatalogCommand.class)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("BffSearchEServiceCatalogCommand");
        assertThatThrownBy(() -> gateway.search(new BffSearchEServiceCatalogCommand()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("offset and limit");
        verifyNoInteractions(client);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 500})
    void pollsUntilSuccessBeforeDeserializingAndMappingTheCatalog(int status) {
        ApiClient apiClient = mock(ApiClient.class);
        EservicesApi api = mock(EservicesApi.class);
        EservicesApi.QueryEServicesCatalogOper operation = mock(EservicesApi.QueryEServicesCatalogOper.class);
        when(apiClient.eservices()).thenReturn(api);
        when(api.queryEServicesCatalog()).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        AtomicInteger attempts = new AtomicInteger();
        when(operation.execute(any())).thenAnswer(invocation -> {
            Function<Response, Response> handler = invocation.getArgument(0);
            boolean firstAttempt = attempts.getAndIncrement() == 0;
            return handler.apply(new ResponseBuilder().setStatusCode(firstAttempt ? status : 200)
                    .setContentType(ContentType.JSON)
                    .setBody(firstAttempt ? "{\"status\":" + status + "}"
                            : "{\"results\":[],\"pagination\":{\"offset\":0,\"limit\":20,\"totalCount\":0}}")
                    .build());
        });
        var client = new BffEServiceCatalogRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)), apiClient);
        var gateway = new BffEServiceCatalogGateway(client, mapper);

        var result = gateway.search(new BffEServiceRequestFactory().defaultSearchEServiceCatalogCommand());

        assertThat(result.getResults()).isEmpty();
        assertThat(result.getLimit()).isEqualTo(20);
        verify(operation, times(2)).execute(any());
    }

    @Test
    void factorySetsOnlyOffsetAndLimit() {
        var command = (BffSearchEServiceCatalogCommand)
                new BffEServiceRequestFactory().defaultSearchEServiceCatalogCommand();

        assertThat(command.getBffPayload()).isEqualTo(new CatalogFilterPayload().offset(0).limit(20));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 201})
    void rejectsInvalidPageSize(int limit) {
        assertThatThrownBy(() -> new BffSearchEServiceCatalogCommand().limit(limit))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validatesOffsetAndPreservesMaximumAllowedPageSize() {
        assertThatThrownBy(() -> new BffSearchEServiceCatalogCommand().offset(-1))
                .isInstanceOf(IllegalArgumentException.class);
        var command = new BffSearchEServiceCatalogCommand();
        command.limit(200).offset(0);
        assertThat(command.getBffPayload().getLimit()).isEqualTo(200);
    }
}
