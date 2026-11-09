package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogSearchConfigurationMapper;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogPropertyExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogExpectationMapper;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.EServiceCatalogAssertionSteps;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceCatalogSearchStepsTest {
    private final EServiceCatalogUseCase catalog = mock(EServiceCatalogUseCase.class);
    private final EServiceCatalogPaginationUseCase pagination = mock(EServiceCatalogPaginationUseCase.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final EServiceRequestFactory factory = spy(new BffEServiceRequestFactory());
    private final it.pagopa.infrastructure.context.InMemoryEntityStore entityStore =
            new it.pagopa.infrastructure.context.InMemoryEntityStore();
    private final EServiceCatalogSearchSteps steps = new EServiceCatalogSearchSteps(
            catalog, pagination, factory, session, Mappers.getMapper(EServiceCatalogSearchConfigurationMapper.class),
            entityStore);

    @BeforeEach
    void stubDefaultSearchResult() {
        when(catalog.search(any())).thenReturn(new EServiceCatalog(List.of(), 0, 20, 0));
        when(catalog.findEntry(any(), any())).thenCallRealMethod();
    }

    @Test
    void searchesNebulaWithOnlyActiveFilterAsRequesterAdmin() {
        var command = new BffSearchEServiceCatalogCommand();
        command.offset(0).limit(20);
        doReturn(command).when(factory).defaultSearchEServiceCatalogCommand();
        var page = new EServiceCatalog(List.of(), 0, 20, 0);
        when(catalog.search(command)).thenReturn(page);

        steps.searchWithFilters(Tenant.PAGO_PA, "Nebula", List.of(filter("solo e-service attivi", "sì")));

        assertThat(command.getBffPayload().getKeyword()).isEqualTo("Nebula");
        assertThat(command.getBffPayload().getOnlyActiveEservices()).isTrue();
        assertThat(command.getBffPayload().getOffset()).isZero();
        assertThat(command.getBffPayload().getLimit()).isEqualTo(20);
        assertThat(steps.getSearchResult()).isSameAs(page);
        assertThat(steps.getSearchCommand()).isSameAs(command);
        var order = inOrder(session, catalog);
        order.verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
        order.verify(catalog).search(command);
        verifyNoMoreInteractions(catalog);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "SANITÀ", "tributi.", "!!!", "😀"})
    void sendsKeywordUnchangedAndLeavesOmittedBooleanUnset(String keyword) {
        steps.search(Tenant.PAGO_PA, keyword);

        var captor = org.mockito.ArgumentCaptor.forClass(
                it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand.class);
        verify(catalog).search(captor.capture());
        var payload = ((BffSearchEServiceCatalogCommand) captor.getValue()).getBffPayload();
        assertThat(payload.getKeyword()).isEqualTo(keyword);
        assertThat(payload.getOnlyActiveEservices()).isNull();
    }

    @Test
    void supportsExplicitFalseAndPaginationOverrides() {
        steps.searchWithFilters(Tenant.PAGO_PA, "Nebula", List.of(
                filter("solo e-service attivi", "no"),
                filter("a partire dal risultato", "10"),
                filter("risultati per pagina", "2")));

        var captor = org.mockito.ArgumentCaptor.forClass(
                it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand.class);
        verify(catalog).search(captor.capture());
        var payload = ((BffSearchEServiceCatalogCommand) captor.getValue()).getBffPayload();
        assertThat(payload.getOnlyActiveEservices()).isFalse();
        assertThat(payload.getOffset()).isEqualTo(10);
        assertThat(payload.getLimit()).isEqualTo(2);
    }

    @Test
    void rejectsUnsupportedFiltersBeforeExecutingSearch() {
        assertThatThrownBy(() -> steps.searchWithFilters(Tenant.PAGO_PA, "Nebula", List.of(filter("sconosciuto", "sì"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Unsupported catalog filter");
        verifyNoInteractions(catalog, session);
    }

    @Test
    void rejectsDuplicateFilters() {
        assertThatThrownBy(() -> steps.searchWithFilters(Tenant.PAGO_PA, "Nebula", List.of(
                filter("solo e-service attivi", "sì"),
                filter("solo e-service attivi", "no"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Duplicate");
        verifyNoInteractions(catalog, session);
    }

    @Test
    void rejectsInvalidBooleanBeforeExecutingSearch() {
        assertThatThrownBy(() -> steps.searchWithFilters(Tenant.PAGO_PA, "Nebula",
                List.of(filter("solo e-service attivi", "forse"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid catalog boolean");
        verifyNoInteractions(catalog, session);
    }

    @Test
    void searchesWithoutKeywordWithProducerAndExplicitSort() {
        steps.searchWithoutKeywordWithFilters(Tenant.PAGO_PA, List.of(
                filter("erogatore", "Comune di Milano"),
                filter("ordinamento", "nome crescente")));

        var payload = ((BffSearchEServiceCatalogCommand) steps.getSearchCommand()).getBffPayload();
        assertThat(payload.getKeyword()).isNull();
        assertThat(payload.getProducersIds()).containsExactly(Tenant.COMUNE_DI_MILANO.getOrganizationId());
        assertThat(payload.getSortBy().name()).isEqualTo("NAME_ASC");
        assertThat(payload.getOffset()).isZero();
        assertThat(payload.getLimit()).isEqualTo(200);
    }

    @Test
    void searchesWithoutKeywordAndLeavesDefaultSortToBackend() {
        steps.searchWithoutKeywordWithFilters(Tenant.PAGO_PA,
                List.of(filter("erogatore", "Comune di Milano")));

        var payload = ((BffSearchEServiceCatalogCommand) steps.getSearchCommand()).getBffPayload();
        assertThat(payload.getKeyword()).isNull();
        assertThat(payload.getSortBy()).isNull();
    }

    @Test
    void failedSearchDoesNotExposePreviousResult() {
        when(catalog.search(any())).thenReturn(new EServiceCatalog(List.of(), 0, 20, 0));
        steps.search(Tenant.PAGO_PA, "first");
        when(catalog.search(any())).thenThrow(new IllegalStateException("backend failed"));

        assertThatThrownBy(() -> steps.search(Tenant.PAGO_PA, "second")).hasMessage("backend failed");
        assertThatThrownBy(steps::getSearchResult).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(steps::getSearchPages).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(steps::getSearchCommand).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void loadsStrictPagesOnDemandReusingFirstPageAndCachesAcrossAssertions() {
        steps.search(Tenant.PAGO_PA, "first");
        var command = steps.getSearchCommand();
        var firstPage = steps.getSearchResult();
        var pages = List.of(firstPage);
        when(pagination.searchPages(command, firstPage, 1)).thenReturn(pages);

        assertThat(steps.getSearchPages(1)).isSameAs(pages);
        assertThat(steps.getSearchPages()).isSameAs(pages);
        assertThat(steps.getSearchPages(1)).isSameAs(pages);
        verify(pagination).searchPages(command, firstPage, 1);
        verify(pagination, never()).searchPages(any(), any());
    }

    @Test
    void fullResultAssertionsLoadAllPagesAndANewSearchClearsTheCache() {
        steps.search(Tenant.PAGO_PA, "first");
        var firstCommand = steps.getSearchCommand();
        var firstPage = steps.getSearchResult();
        var firstPages = List.of(firstPage);
        when(pagination.searchPages(firstCommand, firstPage)).thenReturn(firstPages);
        assertThat(steps.getSearchPages()).isSameAs(firstPages);
        assertThat(steps.getSearchPages()).isSameAs(firstPages);

        var nextPage = new EServiceCatalog(List.of(), 0, 20, 0);
        when(catalog.search(any())).thenReturn(nextPage);
        steps.search(Tenant.PAGO_PA, "second");
        var nextCommand = steps.getSearchCommand();
        var nextPages = List.of(nextPage);
        when(pagination.searchPages(nextCommand, nextPage)).thenReturn(nextPages);

        assertThat(steps.getSearchPages()).isSameAs(nextPages);
        verify(pagination).searchPages(firstCommand, firstPage);
        verify(pagination).searchPages(nextCommand, nextPage);
    }

    @Test
    void invalidNewSearchClearsSuccessfulSearchStateAndCachedPages() {
        steps.search(Tenant.PAGO_PA, "first");
        when(pagination.searchPages(any(), any())).thenReturn(List.of(steps.getSearchResult()));
        steps.getSearchPages();

        assertThatThrownBy(() -> steps.searchWithFilters(Tenant.PAGO_PA, "second",
                List.of(filter("sconosciuto", "sì")))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(steps::getSearchResult).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(steps::getSearchPages).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> steps.getSearchPages(1)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(steps::getSearchCommand).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void failedPageLoadingDoesNotCachePartialResults() {
        steps.search(Tenant.PAGO_PA, "first");
        when(pagination.searchPages(any(), any())).thenThrow(new IllegalStateException("page failed"));
        assertThatThrownBy(steps::getSearchPages).hasMessage("page failed");
        assertThatThrownBy(steps::getSearchPages).hasMessage("page failed");
        verify(pagination, times(2)).searchPages(steps.getSearchCommand(), steps.getSearchResult());
    }

    @Test
    void successfulPresenceRefreshReplacesTheSnapshotAndInvalidatesCachedPages() {
        var expected = it.pagopa.interop.common.eservice.domain.EService.builder()
                .id(java.util.UUID.randomUUID()).build();
        var initial = new EServiceCatalog(List.of(), 0, 20, 0);
        var refreshed = new EServiceCatalog(List.of(
                EServiceCatalog.Entry.builder().id(expected.getId()).build()), 0, 20, 1);
        when(catalog.search(any())).thenReturn(initial);
        steps.search(Tenant.PAGO_PA, "first");
        var command = steps.getSearchCommand();
        var oldPages = List.of(initial);
        var freshPages = List.of(refreshed);
        when(pagination.searchPages(command, initial)).thenReturn(oldPages);
        when(catalog.awaitPresence(command, initial, List.of(expected))).thenReturn(List.of(refreshed));

        assertThat(steps.getSearchPages()).isSameAs(oldPages);
        steps.awaitPresence(List.of(expected));

        assertThat(steps.getSearchResult()).isSameAs(refreshed);
        assertThat(steps.getSearchResult().getResults())
                .extracting(EServiceCatalog.Entry::getId)
                .containsExactly(expected.getId());
        when(pagination.searchPages(command, refreshed)).thenReturn(freshPages);
        assertThat(steps.getSearchPages()).isSameAs(freshPages);
        verify(catalog).awaitPresence(command, initial, List.of(expected));
        verify(pagination).searchPages(command, refreshed);
    }

    @Test
    void downstreamPresenceAndPropertyAssertionsReadTheRefreshedPage() {
        var expected = it.pagopa.interop.common.eservice.domain.EService.builder()
                .id(java.util.UUID.randomUUID()).build();
        entityStore.upsert("ES-1", expected);
        var initial = new EServiceCatalog(List.of(), 0, 20, 0);
        var refreshed = new EServiceCatalog(List.of(
                EServiceCatalog.Entry.builder().id(expected.getId()).version("3").build()), 0, 20, 1);
        when(catalog.search(any())).thenReturn(initial);
        steps.search(Tenant.PAGO_PA, "first");
        var command = steps.getSearchCommand();
        when(catalog.awaitPresence(command, initial, List.of(expected))).thenReturn(List.of(refreshed));
        var assertions = new EServiceCatalogAssertionSteps(
                steps, catalog, pagination, entityStore, Mappers.getMapper(EServiceCatalogExpectationMapper.class));

        assertions.shouldContainEService("ES-1");
        assertions.shouldReportProperties("ES-1", List.of(property("versione presentata", "3")));

        assertThat(steps.getSearchResult()).isSameAs(refreshed);
        verify(catalog).awaitPresence(command, initial, List.of(expected));
        verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
    }

    @Test
    void presenceTraversalPagesFeedPropertyAssertionsWithoutReplacingTheCompletePageCache() {
        var expected = it.pagopa.interop.common.eservice.domain.EService.builder()
                .id(java.util.UUID.randomUUID()).build();
        entityStore.upsert("ES-1", expected);
        var initial = new EServiceCatalog(List.of(), 0, 20, 0);
        var first = new EServiceCatalog(List.of(EServiceCatalog.Entry.builder()
                .id(java.util.UUID.randomUUID()).build()), 0, 1, 3);
        var later = new EServiceCatalog(List.of(EServiceCatalog.Entry.builder()
                .id(expected.getId()).version("7").build()), 1, 1, 3);
        when(catalog.search(any())).thenReturn(initial);
        steps.search(Tenant.PAGO_PA, "first");
        var command = steps.getSearchCommand();
        when(catalog.awaitPresence(command, initial, List.of(expected))).thenReturn(List.of(first, later));
        var assertions = new EServiceCatalogAssertionSteps(
                steps, catalog, pagination, entityStore, Mappers.getMapper(EServiceCatalogExpectationMapper.class));

        assertions.shouldContainEService("ES-1");
        assertions.shouldReportProperties("ES-1", List.of(property("versione presentata", "7")));

        assertThat(steps.getSearchResult()).isSameAs(first);
        assertThat(steps.getObservedPages()).containsExactly(first, later);
        verify(pagination, never()).searchPages(any(), any());
        var completePages = List.of(first, later, new EServiceCatalog(List.of(
                EServiceCatalog.Entry.builder().id(java.util.UUID.randomUUID()).build()), 2, 1, 3));
        when(pagination.searchPages(command, first)).thenReturn(completePages);
        assertThat(steps.getSearchPages()).isSameAs(completePages);
        steps.search(Tenant.PAGO_PA, "second");
        assertThat(steps.getObservedPages()).containsExactly(initial);
    }

    private EServiceCatalogPropertyExpectation property(String name, String value) {
        var expectation = new EServiceCatalogPropertyExpectation();
        expectation.setProperty(name);
        expectation.setValue(value);
        return expectation;
    }

    private EServiceCatalogSearchConfiguration filter(String filter, String value) {
        var configuration = new EServiceCatalogSearchConfiguration();
        configuration.setFilter(filter);
        configuration.setValue(value);
        return configuration;
    }
}
