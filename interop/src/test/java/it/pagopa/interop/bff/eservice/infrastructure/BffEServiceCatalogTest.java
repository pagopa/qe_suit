package it.pagopa.interop.bff.eservice.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.infrastructure.template.action.TestChainFactory;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

        var result = new EServiceCatalogUseCase(gateway, new BffEServiceRequestFactory()).search(command);

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
    void fetchesEveryPageThroughTestChainWithCopiedFiltersAndCorrectOffsets() {
        ApiClient apiClient = mock(ApiClient.class);
        EservicesApi api = mock(EservicesApi.class);
        EservicesApi.QueryEServicesCatalogOper operation = mock(EservicesApi.QueryEServicesCatalogOper.class);
        when(apiClient.eservices()).thenReturn(api);
        when(api.queryEServicesCatalog()).thenReturn(operation);
        when(operation.body(any())).thenReturn(operation);
        AtomicInteger pageIndex = new AtomicInteger();
        when(operation.execute(any())).thenAnswer(invocation -> {
            int index = pageIndex.getAndIncrement();
            int resultCount = index == 2 ? 1 : 2;
            StringBuilder results = new StringBuilder("[");
            for (int resultIndex = 0; resultIndex < resultCount; resultIndex++) {
                if (resultIndex > 0) {
                    results.append(",");
                }
                String id = UUID.nameUUIDFromBytes(
                        ("catalog-page-" + index + "-result-" + resultIndex).getBytes(StandardCharsets.UTF_8))
                        .toString();
                results.append("{\"id\":\"").append(id).append("\",\"name\":\"Page ")
                        .append(index).append(" result ").append(resultIndex).append("\"}");
            }
            results.append("]");
            String body = "{\"results\":" + results + ",\"pagination\":{\"offset\":" + index * 2
                    + ",\"limit\":2,\"totalCount\":5}}";
            Function<Response, Response> handler = invocation.getArgument(0);
            return handler.apply(new ResponseBuilder().setStatusCode(200).setContentType(ContentType.JSON)
                    .setBody(body).build());
        });
        var client = new BffEServiceCatalogRestClient(
                new TestChainFactory(mock(LastApiResponseStore.class), mock(EntityStore.class)), apiClient);
        var gateway = new BffEServiceCatalogGateway(client, mapper);
        var command = (BffSearchEServiceCatalogCommand)
                new BffEServiceRequestFactory().defaultSearchEServiceCatalogCommand();
        command.keyword("ordered").limit(2)
                .sortBy(it.pagopa.interop.common.eservice.domain.EServiceCatalogSortBy.NAME_ASC)
                .onlyActiveEservices(true).producersIds(List.of(UUID.randomUUID()));

        var pages = new EServiceCatalogUseCase(gateway, new BffEServiceRequestFactory()).searchPages(command);

        assertThat(pages).extracting(EServiceCatalog::getOffset).containsExactly(0, 2, 4);
        assertThat(pages).extracting(page -> page.getResults().size()).containsExactly(2, 2, 1);
        org.mockito.ArgumentCaptor<CatalogFilterPayload> payloads =
                org.mockito.ArgumentCaptor.forClass(CatalogFilterPayload.class);
        verify(operation, times(3)).body(payloads.capture());
        assertThat(payloads.getAllValues()).extracting(CatalogFilterPayload::getOffset).containsExactly(0, 2, 4);
        assertThat(payloads.getAllValues()).allSatisfy(payload -> {
            assertThat(payload.getLimit()).isEqualTo(2);
            assertThat(payload.getKeyword()).isEqualTo("ordered");
            assertThat(payload.getSortBy()).isEqualTo(EServiceSortBy.NAME_ASC);
            assertThat(payload.getOnlyActiveEservices()).isTrue();
            assertThat(payload.getProducersIds()).hasSize(1);
        });
        verify(operation, times(3)).execute(any());
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

    @Test
    void factoryCopiesEveryPayloadFilterWithoutMutatingTheOriginal() {
        var command = new BffSearchEServiceCatalogCommand();
        var payload = command.getBffPayload();
        UUID templateId = UUID.randomUUID();
        UUID producerId = UUID.randomUUID();
        payload.setOffset(4);
        payload.setLimit(5);
        payload.setKeyword("keyword");
        payload.setSortBy(EServiceSortBy.CREATED_AT_DESC);
        payload.setOnlyActiveEservices(true);
        payload.setAvailableForRequester(false);
        payload.setSubscribedByRequester(true);
        payload.setOnlyTemplateInstances(false);
        payload.setHasLinkedPurposeTemplates(true);
        payload.setProducerCategories(new ArrayList<>(List.of(EServiceProducerCategory.COMUNI)));
        payload.setAsyncExchange(false);
        payload.setMode(EServiceMode.DELIVER);
        payload.setOnlySignalHubEnabled(true);
        payload.setRequesterDelegationRoles(new ArrayList<>(List.of(RequesterDelegationRole.values()[0])));
        payload.setTemplateId(templateId);
        payload.setProducersIds(new ArrayList<>(List.of(producerId)));

        var copy = (BffSearchEServiceCatalogCommand)
                new BffEServiceRequestFactory().searchEServiceCatalogCommandForOffset(command, 9);
        var copiedPayload = copy.getBffPayload();

        assertThat(copiedPayload).isNotSameAs(payload);
        assertThat(copiedPayload.getOffset()).isEqualTo(9);
        assertThat(copiedPayload.getLimit()).isEqualTo(5);
        assertThat(copiedPayload.getKeyword()).isEqualTo("keyword");
        assertThat(copiedPayload.getSortBy()).isEqualTo(EServiceSortBy.CREATED_AT_DESC);
        assertThat(copiedPayload.getOnlyActiveEservices()).isTrue();
        assertThat(copiedPayload.getAvailableForRequester()).isFalse();
        assertThat(copiedPayload.getSubscribedByRequester()).isTrue();
        assertThat(copiedPayload.getOnlyTemplateInstances()).isFalse();
        assertThat(copiedPayload.getHasLinkedPurposeTemplates()).isTrue();
        assertThat(copiedPayload.getProducerCategories()).containsExactly(EServiceProducerCategory.COMUNI);
        assertThat(copiedPayload.getAsyncExchange()).isFalse();
        assertThat(copiedPayload.getMode()).isEqualTo(EServiceMode.DELIVER);
        assertThat(copiedPayload.getOnlySignalHubEnabled()).isTrue();
        assertThat(copiedPayload.getRequesterDelegationRoles())
                .containsExactly(RequesterDelegationRole.values()[0]);
        assertThat(copiedPayload.getTemplateId()).isEqualTo(templateId);
        assertThat(copiedPayload.getProducersIds()).containsExactly(producerId);
        assertThat(payload.getOffset()).isEqualTo(4);
        assertThat(copiedPayload.getProducerCategories()).isNotSameAs(payload.getProducerCategories());
        assertThat(copiedPayload.getRequesterDelegationRoles()).isNotSameAs(payload.getRequesterDelegationRoles());
        assertThat(copiedPayload.getProducersIds()).isNotSameAs(payload.getProducersIds());
    }

    @ParameterizedTest
    @ValueSource(strings = {"NAME_ASC", "NAME_DESC", "CREATED_AT_ASC", "CREATED_AT_DESC"})
    void mapsCommonSortModesToGeneratedBffValues(String mode) {
        var command = new BffSearchEServiceCatalogCommand();
        command.sortBy(it.pagopa.interop.common.eservice.domain.EServiceCatalogSortBy.valueOf(mode));

        assertThat(command.getBffPayload().getSortBy().name()).isEqualTo(mode);
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
