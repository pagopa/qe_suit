package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogSearchConfigurationMapper;
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
    private final EServiceCatalogSearchSteps steps = new EServiceCatalogSearchSteps(
            catalog, pagination, factory, session, Mappers.getMapper(EServiceCatalogSearchConfigurationMapper.class),
            new it.pagopa.infrastructure.context.InMemoryEntityStore());

    @BeforeEach
    void stubDefaultSearchResult() {
        when(catalog.search(any())).thenReturn(new EServiceCatalog(List.of(), 0, 20, 0));
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
        assertThat(payload.getLimit()).isEqualTo(20);
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

    private EServiceCatalogSearchConfiguration filter(String filter, String value) {
        var configuration = new EServiceCatalogSearchConfiguration();
        configuration.setFilter(filter);
        configuration.setValue(value);
        return configuration;
    }
}
