package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceCatalogGateway;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogPageExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogExpectationMapper;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogPaginationExpectationMapper;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogSearchConfigurationMapper;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EServiceCatalogPaginationStepsTest {
    private final EServiceCatalogGateway gateway = mock(EServiceCatalogGateway.class);
    private final BffEServiceRequestFactory factory = new BffEServiceRequestFactory();
    private final EServiceCatalogUseCase catalog = new EServiceCatalogUseCase(gateway, factory);
    private final EntityStore store = mock(EntityStore.class, CALLS_REAL_METHODS);
    private final EServiceCatalogExpectationMapper expectations = Mappers.getMapper(EServiceCatalogExpectationMapper.class);
    private final EServiceCatalogSearchSteps searches = new EServiceCatalogSearchSteps(catalog, factory,
            mock(CurrentUserSession.class), Mappers.getMapper(EServiceCatalogSearchConfigurationMapper.class));
    private final EServiceCatalogPaginationSteps pagination = new EServiceCatalogPaginationSteps(
            searches, catalog, store, expectations, Mappers.getMapper(EServiceCatalogPaginationExpectationMapper.class));
    private final EServiceCatalogAssertionSteps assertions = new EServiceCatalogAssertionSteps(
            searches, catalog, store, expectations);
    private final UUID first = UUID.randomUUID();
    private final UUID second = UUID.randomUUID();
    private final UUID third = UUID.randomUUID();

    @Test
    void explicitAssertionsFetchOnlyRemainingPagesAndReuseOneCompleteScenarioCache() {
        stubPages(third, 3);
        when(store.getByAlias("first", EService.class)).thenReturn(Optional.of(service(first)));
        when(store.getByAlias("second", EService.class)).thenReturn(Optional.of(service(second)));
        when(store.getByAlias("third", EService.class)).thenReturn(Optional.of(service(third)));
        search();
        verify(gateway, times(1)).search(any());

        pagination.pagesContainExpectedResultsWithTotal(List.of(pageExpectation(1, 2), pageExpectation(2, 1)));
        pagination.pagesContainNoDuplicateEServices();
        pagination.pagesContainExactlyEServices(List.of(expectation("third"), expectation("first"), expectation("second")));
        assertions.shouldAppearExactlyOnce("third");

        verify(gateway).search(argThat(command -> command.getOffset() == 0));
        verify(gateway).search(argThat(command -> command.getOffset() == 2));
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void exactlyOnceChecksTheFullResultEvenWhenThereWasNoPreviousPaginationAssertion() {
        stubPages(first, 3);
        when(store.getByAlias("first", EService.class)).thenReturn(Optional.of(service(first)));
        search();

        assertThatThrownBy(() -> assertions.shouldAppearExactlyOnce("first"))
                .isInstanceOf(AssertionError.class).hasMessageContaining("Occurrences");

        verify(gateway, times(2)).search(any());
    }

    @Test
    void explicitPaginationStillRejectsChangingTotalsWithoutRelativeScanRetries() {
        stubPages(third, 4);
        search();

        assertThatThrownBy(() -> pagination.pagesContainExpectedResultsWithTotal(
                List.of(pageExpectation(1, 2), pageExpectation(2, 1))))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("total count changed");

        verify(gateway, times(2)).search(any());
    }

    @Test
    void explicitPaginationStillRejectsIncorrectExpectedCountSum() {
        stubPages(third, 3);
        search();

        assertThatThrownBy(() -> pagination.pagesContainExpectedResultsWithTotal(
                List.of(pageExpectation(1, 2), pageExpectation(2, 0))))
                .isInstanceOf(AssertionError.class).hasMessageContaining("Declared catalog total count");
    }

    @Test
    void explicitPaginationStillDetectsDuplicateIdsAcrossPages() {
        stubPages(first, 3);
        search();
        pagination.pagesContainExpectedResultsWithTotal(List.of(pageExpectation(1, 2), pageExpectation(2, 1)));

        assertThatThrownBy(pagination::pagesContainNoDuplicateEServices)
                .isInstanceOf(AssertionError.class).hasMessageContaining("E-service IDs across catalog pages");
        verify(gateway, times(2)).search(any());
    }

    private void stubPages(UUID lastId, int lastTotal) {
        when(gateway.search(any())).thenAnswer(invocation -> {
            var command = (it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand)
                    invocation.getArgument(0);
            return command.getOffset() == 0
                    ? page(List.of(first, second), 0, 3)
                    : page(lastTotal == 3 ? List.of(lastId) : List.of(lastId, UUID.randomUUID()), 2, lastTotal);
        });
    }

    private void search() {
        var filter = new EServiceCatalogSearchConfiguration();
        filter.setFilter("risultati per pagina");
        filter.setValue("2");
        searches.searchWithFilters(Tenant.PAGO_PA, "controlled-keyword", List.of(filter));
    }

    private EServiceCatalog page(List<UUID> ids, int offset, int total) {
        return new EServiceCatalog(ids.stream().map(id -> EServiceCatalog.Entry.builder().id(id).build()).toList(),
                offset, 2, total);
    }

    private EService service(UUID id) {
        return EService.builder().id(id).build();
    }

    private EServiceCatalogPageExpectation pageExpectation(int page, int count) {
        var dto = new EServiceCatalogPageExpectation();
        dto.setPage(page);
        dto.setResults(count);
        return dto;
    }

    private EServiceCatalogExpectation expectation(String alias) {
        var dto = new EServiceCatalogExpectation();
        dto.setAlias(alias);
        return dto;
    }
}
