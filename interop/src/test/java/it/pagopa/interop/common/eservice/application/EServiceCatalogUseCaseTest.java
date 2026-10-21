package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.domain.EServiceCatalogSortBy;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.tenant.domain.TenantCategory;
import it.pagopa.interop.generated.openapi.clients.bff.model.RequesterDelegationRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EServiceCatalogUseCaseTest {
    private final EServiceCatalogGateway gateway = mock(EServiceCatalogGateway.class);
    private final EServiceCatalogUseCase catalog = new EServiceCatalogUseCase(gateway);
    private final EServiceCatalogPaginationUseCase pagination = new EServiceCatalogPaginationUseCase(
            catalog, gateway, new BffEServiceRequestFactory());

    @Test
    void ordinarySearchRequestsOnlyTheRequestedPageEvenForALargeCatalog() {
        var command = fullCommand();
        var result = page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 4000);
        when(gateway.search(command)).thenReturn(result);

        assertThat(catalog.search(command)).isSameAs(result);

        verify(gateway).search(command);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void relativeOrderStopsOnTheFirstPageWhenAllExpectedIdsAreFound() {
        var command = fullCommand();
        var first = service(UUID.randomUUID());
        var second = service(UUID.randomUUID());

        pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(first.getId()), entry(second.getId())), 0, 2, 4000),
                List.of(first, second));

        verifyNoInteractions(gateway);
    }

    @Test
    void relativeOrderAllowsChangingTotalsAndStopsAsSoonAsAllExpectedIdsAreFound() {
        var command = fullCommand();
        var first = service(UUID.randomUUID());
        var second = service(UUID.randomUUID());
        when(gateway.search(any())).thenReturn(
                page(List.of(entry(UUID.randomUUID()), entry(second.getId())), 2, 2, 4001));

        pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(UUID.randomUUID()), entry(first.getId())), 0, 2, 4000),
                List.of(first, second));

        verify(gateway).search(argThat(request -> request.getOffset() == 2));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void relativeOrderReachesDeepTargetsWithoutAnArbitraryPageCapAndCopiesFilters() {
        var command = fullCommand();
        command.limit(20);
        var first = service(UUID.randomUUID());
        var second = service(UUID.randomUUID());
        when(gateway.search(any())).thenAnswer(invocation -> {
            BffSearchEServiceCatalogCommand request = invocation.getArgument(0);
            assertSameFilters(request);
            int offset = request.getOffset();
            int total = offset < 260 ? 4001 : 4002;
            List<EServiceCatalog.Entry> entries = java.util.stream.IntStream
                    .range(offset, Math.min(offset + 20, total))
                    .mapToObj(index -> entry(index == 3980 ? first.getId()
                            : index == 4000 ? second.getId() : UUID.randomUUID()))
                    .toList();
            return page(entries, offset, 20, total);
        });
        EServiceCatalog firstPage = catalog.search(command);

        pagination.shouldContainInRelativeOrder(command, firstPage, List.of(first, second));

        verify(gateway, times(201)).search(any());
        assertThat(command.getOffset()).isZero();
        assertSameFilters(command);
    }

    @Test
    void relativeOrderDoesNotRetryProvenWrongOrder() {
        var command = fullCommand();
        var first = service(UUID.randomUUID());
        var second = service(UUID.randomUUID());
        when(gateway.search(any())).thenReturn(
                page(List.of(entry(first.getId()), entry(UUID.randomUUID())), 2, 2, 4000));

        assertThatThrownBy(() -> pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(second.getId()), entry(UUID.randomUUID())), 0, 2, 4000),
                List.of(first, second)))
                .isInstanceOf(AssertionError.class).hasMessageContaining("Relative e-service order");

        verify(gateway, times(1)).search(any());
    }

    @Test
    void wrongOrderWithObservedDriftIsInconclusiveRatherThanAProductOrderingFailure() {
        var command = fullCommand();
        var first = service(UUID.randomUUID());
        var second = service(UUID.randomUUID());
        when(gateway.search(any())).thenAnswer(invocation -> {
            var request = (BffSearchEServiceCatalogCommand) invocation.getArgument(0);
            return request.getOffset() == 0
                    ? page(List.of(entry(second.getId()), entry(UUID.randomUUID())), 0, 2, 4)
                    : page(List.of(entry(first.getId()), entry(UUID.randomUUID())), 2, 2, 5);
        });

        assertThatThrownBy(() -> pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(second.getId()), entry(UUID.randomUUID())), 0, 2, 4),
                List.of(first, second)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Inconclusive").hasMessageContaining("changing totals");

        verify(gateway, times(3)).search(any());
    }

    @Test
    void anomalousDuplicatesTriggerOneFreshTraversalWithoutDroppingBackendResults() {
        var command = fullCommand();
        var first = service(UUID.randomUUID());
        var second = service(UUID.randomUUID());
        UUID unrelated = UUID.randomUUID();
        when(gateway.search(any())).thenReturn(
                page(List.of(entry(unrelated), entry(second.getId())), 2, 2, 4),
                page(List.of(entry(first.getId()), entry(UUID.randomUUID())), 0, 2, 4),
                page(List.of(entry(UUID.randomUUID()), entry(second.getId())), 2, 2, 4));

        pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(first.getId()), entry(unrelated)), 0, 2, 4),
                List.of(first, second));

        var order = inOrder(gateway);
        order.verify(gateway).search(argThat(request -> request.getOffset() == 2));
        order.verify(gateway).search(same(command));
        order.verify(gateway).search(argThat(request -> request.getOffset() == 2));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void repeatedDuplicateAnomaliesAreExplicitlyInconclusive() {
        var command = fullCommand();
        var first = service(UUID.randomUUID());
        var second = service(UUID.randomUUID());
        var duplicatePage = page(List.of(entry(first.getId()), entry(first.getId())), 0, 2, 2);
        when(gateway.search(command)).thenReturn(duplicatePage);

        assertThatThrownBy(() -> pagination.shouldContainInRelativeOrder(command, duplicatePage, List.of(first, second)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Inconclusive")
                .hasMessageContaining("2 attempts").hasMessageContaining("Repeated e-service");

        verify(gateway).search(command);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void missingTargetsAtTheEndAreInconclusiveAndIdentifyMissingIdsAfterOneFreshAttempt() {
        var command = fullCommand();
        var expected = service(UUID.randomUUID());
        var firstPage = page(List.of(entry(UUID.randomUUID())), 0, 2, 1);
        when(gateway.search(command)).thenReturn(firstPage);

        assertThatThrownBy(() -> pagination.shouldContainInRelativeOrder(command, firstPage, List.of(expected)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Inconclusive")
                .hasMessageContaining(expected.getId().toString()).hasMessageContaining("after offset 0");

        verify(gateway).search(command);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void aMissingTargetCanBeRecoveredByTheFreshTraversal() {
        var command = fullCommand();
        var expected = service(UUID.randomUUID());
        var firstPage = page(List.of(entry(UUID.randomUUID())), 0, 2, 1);
        when(gateway.search(command)).thenReturn(page(List.of(entry(expected.getId())), 0, 2, 1));

        pagination.shouldContainInRelativeOrder(command, firstPage, List.of(expected));

        verify(gateway).search(command);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void aContinuouslyGrowingCatalogDoesNotCauseUnboundedTraversal() {
        var command = fullCommand();
        var expected = service(UUID.randomUUID());
        when(gateway.search(any())).thenAnswer(invocation -> {
            var request = (BffSearchEServiceCatalogCommand) invocation.getArgument(0);
            return page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())),
                    request.getOffset(), 2, request.getOffset() + 4);
        });

        assertThatThrownBy(() -> pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 4),
                List.of(expected)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Inconclusive")
                .hasMessageContaining("initial total=4, latest total=6");

        verify(gateway, times(3)).search(any());
        verify(gateway, never()).search(argThat(request -> request.getOffset() > 2));
    }

    @Test
    void aFreshAttemptUsesItsNewMetadataHorizonToFindTargetsBeyondTheOriginalEnd() {
        var command = fullCommand();
        var expected = service(UUID.randomUUID());
        when(gateway.search(any())).thenAnswer(invocation -> {
            var request = (BffSearchEServiceCatalogCommand) invocation.getArgument(0);
            return page(List.of(entry(UUID.randomUUID()),
                    entry(request.getOffset() == 6 ? expected.getId() : UUID.randomUUID())),
                    request.getOffset(), 2, 8);
        });

        pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 4),
                List.of(expected));

        var requests = org.mockito.ArgumentCaptor.forClass(
                it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand.class);
        verify(gateway, times(5)).search(requests.capture());
        assertThat(requests.getAllValues()).extracting(request -> request.getOffset())
                .containsExactly(2, 0, 2, 4, 6);
    }

    @Test
    void shrinkingTotalsTerminateAtTheFreshEndInsteadOfRequestingStalePages() {
        var command = fullCommand();
        var expected = service(UUID.randomUUID());
        when(gateway.search(any())).thenReturn(
                page(List.of(), 2, 2, 2),
                page(List.of(entry(expected.getId())), 0, 2, 1));

        pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 100),
                List.of(expected));

        verify(gateway, times(2)).search(any());
        verify(gateway, never()).search(argThat(request -> request.getOffset() > 2));
    }

    @Test
    void relativeScanRetriesInconsistentResultCountsButDoesNotMaskApiFailures() {
        var command = fullCommand();
        var expected = service(UUID.randomUUID());
        var firstPage = page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 4);
        when(gateway.search(any())).thenReturn(
                page(List.of(), 2, 2, 4),
                page(List.of(entry(expected.getId())), 0, 2, 1));
        pagination.shouldContainInRelativeOrder(command, firstPage, List.of(expected));
        verify(gateway, times(2)).search(any());

        reset(gateway);
        var apiFailure = new IllegalStateException("HTTP 500");
        when(gateway.search(any())).thenThrow(apiFailure);
        assertThatThrownBy(() -> pagination.shouldContainInRelativeOrder(command, firstPage, List.of(expected)))
                .isSameAs(apiFailure);
        verify(gateway, times(1)).search(any());
    }

    @Test
    void apiFailureDuringTheFreshAttemptIsNotHiddenAsAnInconclusiveScan() {
        var command = fullCommand();
        var expected = service(UUID.randomUUID());
        var apiFailure = new IllegalStateException("HTTP 403");
        when(gateway.search(command)).thenThrow(apiFailure);

        assertThatThrownBy(() -> pagination.shouldContainInRelativeOrder(command,
                page(List.of(entry(UUID.randomUUID())), 0, 2, 1), List.of(expected)))
                .isSameAs(apiFailure);

        verify(gateway).search(command);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void explicitPaginationReusesTheAlreadyFetchedFirstPage() {
        var command = fullCommand();
        var firstPage = page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 4);
        when(gateway.search(any())).thenReturn(
                page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 2, 2, 4));

        var pages = pagination.searchPages(command, firstPage, 2);

        assertThat(pages).hasSize(2).first().isSameAs(firstPage);
        catalog.shouldHavePagination(pages, List.of(2, 2));
        verify(gateway).search(argThat(request -> request.getOffset() == 2));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void explicitPaginationRejectsAnUnexpectedTotalBeforeFetchingThousandsOfUnrequestedPages() {
        var command = fullCommand();
        var firstPage = page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 4000);

        assertThatThrownBy(() -> pagination.searchPages(command, firstPage, 2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Expected 2 catalog pages").hasMessageContaining("total 4000");

        verifyNoInteractions(gateway);
    }

    @Test
    void loadsBoundedPagesWithAllOriginalFiltersAndPreservesTheInitialCommand() {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        UUID third = UUID.randomUUID();
        UUID fourth = UUID.randomUUID();
        UUID fifth = UUID.randomUUID();
        BffSearchEServiceCatalogCommand command = fullCommand();
        when(gateway.search(any())).thenAnswer(invocation -> {
            BffSearchEServiceCatalogCommand pageCommand = invocation.getArgument(0);
            assertSameFilters(pageCommand);
            int offset = pageCommand.getOffset();
            List<EServiceCatalog.Entry> entries = switch (offset) {
                case 0 -> List.of(entry(first), entry(second));
                case 2 -> List.of(entry(third), entry(fourth));
                case 4 -> List.of(entry(fifth));
                default -> throw new AssertionError("Unexpected page offset " + offset);
            };
            return page(entries, offset, 2, 5);
        });

        List<EServiceCatalog> pages = pagination.searchPages(command);

        assertThat(pages).extracting(EServiceCatalog::getOffset).containsExactly(0, 2, 4);
        assertThat(pages).extracting(page -> page.getResults().size()).containsExactly(2, 2, 1);
        assertThat(command.getOffset()).isZero();
        assertThat(command.getBffPayload().getKeyword()).isEqualTo("catalog-keyword");
        verify(gateway, times(3)).search(any());
        verify(gateway).search(same(command));
    }

    @Test
    void doesNotRequestPagesAfterAnEmptyOffsetBeyondTheEnd() {
        var command = (BffSearchEServiceCatalogCommand) new BffSearchEServiceCatalogCommand()
                .offset(10).limit(2);
        when(gateway.search(command)).thenReturn(page(List.of(), 10, 2, 2));

        assertThat(pagination.searchPages(command))
                .singleElement()
                .satisfies(result -> {
                    assertThat(result.getResults()).isEmpty();
                    assertThat(result.getTotalCount()).isEqualTo(2);
                });

        verify(gateway, times(1)).search(any());
    }

    @Test
    void rejectsMissingGatewayResultAndInvalidPageMetadataOrCounts() {
        var command = (BffSearchEServiceCatalogCommand) new BffSearchEServiceCatalogCommand()
                .offset(0).limit(2);

        when(gateway.search(command)).thenReturn(null);
        assertThatThrownBy(() -> pagination.searchPages(command))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("no search result");

        when(gateway.search(command)).thenReturn(page(List.of(), 1, 2, 0));
        assertThatThrownBy(() -> pagination.searchPages(command))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("offset");

        when(gateway.search(command)).thenReturn(page(List.of(), 0, 1, 0));
        assertThatThrownBy(() -> pagination.searchPages(command))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("limit");

        when(gateway.search(command)).thenReturn(page(List.of(entry(UUID.randomUUID())), 0, 2, 2));
        assertThatThrownBy(() -> pagination.searchPages(command))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("expected 2");
    }

    @Test
    void rejectsChangingTotalsAndIncorrectCountsOnSubsequentPages() {
        var command = (BffSearchEServiceCatalogCommand) new BffSearchEServiceCatalogCommand()
                .offset(0).limit(2);
        when(gateway.search(any())).thenAnswer(invocation -> {
            var pageCommand = (BffSearchEServiceCatalogCommand) invocation.getArgument(0);
            return pageCommand.getOffset() == 0
                    ? page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 3)
                    : page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 2, 2, 4);
        });

        assertThatThrownBy(() -> pagination.searchPages(command))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("total count changed");

        org.mockito.Mockito.doAnswer(invocation -> {
            var pageCommand = (BffSearchEServiceCatalogCommand) invocation.getArgument(0);
            return pageCommand.getOffset() == 0
                    ? page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 3)
                    : page(List.of(), 2, 2, 3);
        }).when(gateway).search(any());
        assertThatThrownBy(() -> pagination.searchPages(command))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("expected 1");
    }

    @Test
    void checksExactOrderPageTotalsAndDuplicateIdsIncludingWithinOnePage() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        EService first = service(firstId);
        EService second = service(secondId);
        EServiceCatalog firstPage = page(List.of(entry(firstId), entry(secondId)), 0, 2, 3);
        EServiceCatalog lastPage = page(List.of(entry(UUID.randomUUID())), 2, 2, 3);

        catalog.shouldContainInOrder(page(List.of(entry(firstId), entry(secondId)), 0, 2, 2),
                List.of(first, second));
        assertThatThrownBy(() -> catalog.shouldContainInOrder(
                page(List.of(entry(secondId), entry(firstId)), 0, 2, 2), List.of(first, second)))
                .isInstanceOf(AssertionError.class);
        catalog.shouldHavePagination(List.of(firstPage, lastPage), List.of(2, 1));
        assertThatThrownBy(() -> catalog.shouldHavePagination(
                List.of(firstPage, lastPage), List.of(1, 1)))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Declared catalog total count");
        assertThatThrownBy(() -> catalog.shouldContainExactly(List.of(firstPage, lastPage), List.of(first, second)))
                .isInstanceOf(AssertionError.class);

        EServiceCatalog duplicateWithinPage = page(List.of(entry(firstId), entry(firstId)), 0, 2, 2);
        assertThatThrownBy(() -> catalog.shouldHaveNoDuplicateEServices(List.of(duplicateWithinPage)))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> catalog.shouldHaveNoDuplicateEServices(List.of(
                page(List.of(entry(firstId)), 0, 1, 2),
                page(List.of(entry(firstId)), 1, 1, 2))))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void verifiesExactCollectiveIdsAndExactlyOneOccurrence() {
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        EService first = service(firstId);
        EService second = service(secondId);
        List<EServiceCatalog> pages = List.of(
                page(List.of(entry(firstId)), 0, 1, 2),
                page(List.of(entry(secondId)), 1, 1, 2));

        catalog.shouldContainExactly(pages, List.of(first, second));
        catalog.shouldAppearExactlyOnce(pages, first);
        assertThatThrownBy(() -> catalog.shouldContainExactly(pages, List.of(first)))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> catalog.shouldAppearExactlyOnce(pages, service(UUID.randomUUID())))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void rejectsPaginationExpectationsWithWrongPageCountsOrTotals() {
        List<EServiceCatalog> pages = List.of(
                page(List.of(entry(UUID.randomUUID()), entry(UUID.randomUUID())), 0, 2, 3),
                page(List.of(entry(UUID.randomUUID())), 2, 2, 3));
        catalog.shouldHavePagination(pages, List.of(2, 1));

        assertThatThrownBy(() -> catalog.shouldHavePagination(pages, List.of(1, 1)))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> catalog.shouldHavePagination(List.of(
                pages.get(0), page(List.of(entry(UUID.randomUUID())), 2, 2, 4)),
                List.of(2, 1)))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> catalog.shouldHavePagination(
                List.of(pages.get(0)), List.of(2, 1)))
                .isInstanceOf(AssertionError.class);
    }

    private BffSearchEServiceCatalogCommand fullCommand() {
        var command = new BffSearchEServiceCatalogCommand();
        command.offset(0).limit(2).keyword("catalog-keyword")
                .sortBy(EServiceCatalogSortBy.CREATED_AT_ASC)
                .onlyActiveEservices(true)
                .mode(EServiceMode.DELIVER)
                .asyncExchange(false)
                .onlySignalHubEnabled(true)
                .producerCategories(List.of(TenantCategory.COMUNI))
                .producersIds(List.of(UUID.randomUUID()));
        var payload = command.getBffPayload();
        payload.setAvailableForRequester(true);
        payload.setSubscribedByRequester(false);
        payload.setOnlyTemplateInstances(true);
        payload.setHasLinkedPurposeTemplates(false);
        payload.setRequesterDelegationRoles(List.of(RequesterDelegationRole.values()[0]));
        payload.setTemplateId(UUID.randomUUID());
        return command;
    }

    private void assertSameFilters(BffSearchEServiceCatalogCommand command) {
        var payload = command.getBffPayload();
        assertThat(payload.getKeyword()).isEqualTo("catalog-keyword");
        assertThat(payload.getSortBy().name()).isEqualTo("CREATED_AT_ASC");
        assertThat(payload.getOnlyActiveEservices()).isTrue();
        assertThat(payload.getMode().name()).isEqualTo("DELIVER");
        assertThat(payload.getAsyncExchange()).isFalse();
        assertThat(payload.getOnlySignalHubEnabled()).isTrue();
        assertThat(payload.getProducerCategories()).containsExactly(
                it.pagopa.interop.generated.openapi.clients.bff.model.EServiceProducerCategory.COMUNI);
        assertThat(payload.getProducersIds()).hasSize(1);
        assertThat(payload.getAvailableForRequester()).isTrue();
        assertThat(payload.getSubscribedByRequester()).isFalse();
        assertThat(payload.getOnlyTemplateInstances()).isTrue();
        assertThat(payload.getHasLinkedPurposeTemplates()).isFalse();
        assertThat(payload.getRequesterDelegationRoles()).containsExactly(RequesterDelegationRole.values()[0]);
        assertThat(payload.getTemplateId()).isNotNull();
    }

    private EServiceCatalog page(List<EServiceCatalog.Entry> entries, int offset, int limit, int total) {
        return new EServiceCatalog(entries, offset, limit, total);
    }

    private EServiceCatalog.Entry entry(UUID id) {
        return EServiceCatalog.Entry.builder().id(id).build();
    }

    private EService service(UUID id) {
        return EService.builder().id(id).build();
    }

}
