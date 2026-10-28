package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase;
import it.pagopa.interop.common.eservice.application.EServiceCatalogGateway;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogExpectationMapper;
import it.pagopa.interop.common.infrastructure.cucumber.DataTableMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceCatalogAssertionStepsTest {
    private final EServiceCatalogGateway gateway = mock(EServiceCatalogGateway.class);
    private final BffEServiceRequestFactory factory = new BffEServiceRequestFactory();
    private final EServiceCatalogUseCase catalog = new EServiceCatalogUseCase(gateway);
    private final EServiceCatalogPaginationUseCase pagination =
            new EServiceCatalogPaginationUseCase(catalog, gateway, factory);
    private final EntityStore store = mock(EntityStore.class, CALLS_REAL_METHODS);
    private final EServiceCatalogSearchSteps searches = mock(EServiceCatalogSearchSteps.class);
    private final EServiceCatalogAssertionSteps steps = new EServiceCatalogAssertionSteps(
            searches, catalog, pagination, store, Mappers.getMapper(EServiceCatalogExpectationMapper.class));
    private final EService included = EService.builder().id(UUID.randomUUID()).name("same-name").build();
    private final EService excluded = EService.builder().id(UUID.randomUUID()).name("same-name").build();

    @Test
    void verifiesSinglePresenceAndMultipleAbsencesUsingIdsWithoutCallingGateway() {
        EService third = EService.builder().id(UUID.randomUUID()).build();
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(store.getByAlias("ES-3", EService.class)).thenReturn(Optional.of(third));
        when(searches.getSearchCommand()).thenReturn(new BffSearchEServiceCatalogCommand().offset(0).limit(20));
        when(searches.getSearchResult()).thenReturn(result(included));

        steps.shouldContainEService("ES-1");
        steps.shouldNotContainEServices(List.of(expectation("ES-2"), expectation("ES-3")));
        steps.shouldContainEServices(List.of(expectation("ES-1")));
        steps.shouldNotContainEService("ES-2");

        verifyNoInteractions(gateway);
    }

    @Test
    void failsIfAnyExcludedEServiceIsPresent() {
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(searches.getSearchCommand()).thenReturn(new BffSearchEServiceCatalogCommand().offset(0).limit(20));
        when(searches.getSearchResult()).thenReturn(result(included));

        assertThatThrownBy(() -> steps.shouldNotContainEServices(
                List.of(expectation("ES-2"), expectation("ES-1"))))
                .isInstanceOf(AssertionError.class).hasMessageContaining(included.getId().toString());
    }

    @Test
    void verifiesExactUuidOrderAndSingleOccurrenceAcrossFetchedPages() {
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(searches.getSearchResult()).thenReturn(result(included, excluded));
        when(searches.getSearchPages()).thenReturn(List.of(
                result(included),
                result(excluded)));

        steps.shouldContainEServicesInOrder(List.of(expectation("ES-1"), expectation("ES-2")));
        steps.shouldAppearExactlyOnce("ES-1");

        assertThatThrownBy(() -> steps.shouldContainEServicesInOrder(
                List.of(expectation("ES-2"), expectation("ES-1"))))
                .isInstanceOf(AssertionError.class);
        verifyNoInteractions(gateway);
    }

    @Test
    void relativeOrderUsesOriginalCommandAndFirstPageAndOnlyFetchesNeededPages() {
        EService other = EService.builder().id(UUID.randomUUID()).build();
        var command = new BffSearchEServiceCatalogCommand().offset(0).limit(2);
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(searches.getSearchCommand()).thenReturn(command);
        when(searches.getSearchResult()).thenReturn(new EServiceCatalog(
                result(other, included).getResults(), 0, 2, 100));
        when(gateway.search(any())).thenReturn(new EServiceCatalog(result(excluded,
                EService.builder().id(UUID.randomUUID()).build()).getResults(), 2, 2, 100));

        steps.shouldContainEServicesInRelativeOrder(List.of(expectation("ES-1"), expectation("ES-2")));
        assertThatThrownBy(() -> steps.shouldContainEServicesInRelativeOrder(
                List.of(expectation("ES-2"), expectation("ES-1"))))
                .isInstanceOf(AssertionError.class);
        verify(gateway, times(2)).search(any());
        verify(searches, never()).getSearchPages();
    }

    @Test
    void relativeOrderRejectsMissingDuplicateAndEmptyExpectations() {
        var command = new BffSearchEServiceCatalogCommand().offset(0).limit(20);
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(searches.getSearchCommand()).thenReturn(command);
        when(searches.getSearchResult()).thenReturn(result(included));
        when(gateway.search(command)).thenReturn(result(included));
        assertThatThrownBy(() -> steps.shouldContainEServicesInRelativeOrder(
                List.of(expectation("ES-1"), expectation("ES-2"))))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Inconclusive");
        assertThatThrownBy(() -> steps.shouldContainEServicesInRelativeOrder(List.of()))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> steps.shouldContainEServicesInRelativeOrder(
                List.of(expectation("ES-1"), expectation("ES-1")))).isInstanceOf(AssertionError.class);
        when(searches.getSearchResult()).thenReturn(result(included, included));
        when(gateway.search(command)).thenReturn(result(included, included));
        assertThatThrownBy(() -> steps.shouldContainEServicesInRelativeOrder(List.of(expectation("ES-1"))))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Repeated e-service");
    }

    @Test
    void failsWhenAnEserviceAppearsMoreThanOnceAcrossLoadedPages() {
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(searches.getSearchPages()).thenReturn(List.of(result(included), result(included)));

        assertThatThrownBy(() -> steps.shouldAppearExactlyOnce("ES-1"))
                .isInstanceOf(AssertionError.class);
        verifyNoInteractions(gateway);
    }

    @Test
    void positivePresenceDelegatesToTheSearchSnapshotOwner() {
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(searches.getSearchResult()).thenReturn(result(included));

        steps.shouldContainEService("ES-2");

        verify(searches).awaitPresence(List.of(excluded));
        verify(searches, never()).awaitPresence(List.of(included));
    }

    @Test
    void unknownAliasDoesNotPassAsAnAbsentEService() {
        when(searches.getSearchResult()).thenReturn(result());

        assertThatThrownBy(() -> steps.shouldNotContainEServices(List.of(expectation("UNKNOWN"))))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("UNKNOWN");
    }

    @Test
    void requiresASuccessfulSearchAndNonEmptyExpectations() {
        when(searches.getSearchResult()).thenThrow(new IllegalStateException("No catalog search"));
        assertThatThrownBy(() -> steps.shouldContainEService("ES-1"))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("No catalog search");
        assertThatThrownBy(() -> catalog.shouldContain(result(), List.of())).isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> catalog.shouldNotContain(result(), List.of())).isInstanceOf(AssertionError.class);
    }

    @Test
    void verifiesSearchSuccessAndEmptyPageWithDeclaredTotal() {
        when(searches.getSearchResult()).thenReturn(result());

        steps.searchTerminatesWithoutErrors();
        steps.shouldHaveEmptyPageWithTotalCount(0);

        assertThatThrownBy(() -> steps.shouldHaveEmptyPageWithTotalCount(1))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void verifiesVersionOwnershipAndUnreadNotificationPropertiesByAlias() {
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(searches.getObservedPages()).thenReturn(List.of(new EServiceCatalog(List.of(
                EServiceCatalog.Entry.builder().id(included.getId()).version("3")
                        .isMine(false).hasUnreadNotifications(true).build()), 0, 20, 1)));

        steps.shouldReportProperties("ES-1", List.of(
                property("versione presentata", "3"),
                property("è mio", "no"),
                property("notifiche non lette", "sì")));

        assertThatThrownBy(() -> steps.shouldReportProperties("ES-1", List.of(property("versione presentata", "2"))))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> steps.shouldReportProperties("ES-1", List.of(property("proprietà ignota", "no"))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void absenceScansEveryPageWithOriginalFiltersAndCatchesAnExcludedIdOnALaterPage() {
        var command = filteredCommand(0, 2);
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(searches.getSearchCommand()).thenReturn(command);
        when(searches.getSearchResult()).thenReturn(page(0, 6, UUID.randomUUID(), UUID.randomUUID()));
        List<BffSearchEServiceCatalogCommand> requests = new java.util.ArrayList<>();
        when(gateway.search(any())).thenAnswer(invocation -> {
            BffSearchEServiceCatalogCommand request = invocation.getArgument(0);
            requests.add(request);
            return request.getOffset() == 2 ? page(2, 6, UUID.randomUUID(), UUID.randomUUID())
                    : page(4, 6, UUID.randomUUID(), excluded.getId());
        });

        assertThatThrownBy(() -> steps.shouldNotContainEService("ES-2"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(excluded.getId().toString())
                .hasMessageContaining("offset 4");
        assertThat(requests).extracting(BffSearchEServiceCatalogCommand::getOffset).containsExactly(2, 4);
        assertThat(requests).allSatisfy(request -> {
            assertThat(request).isNotSameAs(command);
            assertThat(request.getBffPayload().getKeyword()).isEqualTo("keyword");
            assertThat(request.getBffPayload().getProducersIds()).isEqualTo(command.getBffPayload().getProducersIds());
            assertThat(request.getBffPayload().getLimit()).isEqualTo(2);
        });
        assertThat(command.getOffset()).isZero();
        verify(searches, never()).getSearchPages();
    }

    @Test
    void absencePassesOnlyAfterAllPagesAndRetriesAnAnomalousScanOnce() {
        var command = filteredCommand(0, 2);
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(searches.getSearchCommand()).thenReturn(command);
        when(searches.getSearchResult()).thenReturn(page(0, 4, UUID.randomUUID(), UUID.randomUUID()));
        when(gateway.search(any())).thenAnswer(invocation -> page(2, 4, UUID.randomUUID(), UUID.randomUUID()));

        steps.shouldNotContainEServices(List.of(expectation("ES-2")));
        verify(gateway, times(1)).search(any());

        when(gateway.search(any())).thenAnswer(invocation -> {
            BffSearchEServiceCatalogCommand request = invocation.getArgument(0);
            return request.getOffset() == 0 ? page(0, 4, UUID.randomUUID(), UUID.randomUUID())
                    : page(2, 6, UUID.randomUUID(), UUID.randomUUID());
        });
        assertThatThrownBy(() -> steps.shouldNotContainEService("ES-2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Inconclusive catalog absence scan after 2 attempts")
                .hasMessageContaining("total changed from 4 to 6");
    }

    @Test
    void propertiesWaitForTheTargetWithoutFetchingTheCompletePageSet() {
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(searches.getObservedPages()).thenReturn(List.of(page(0, 4, UUID.randomUUID(), UUID.randomUUID())));
        doAnswer(invocation -> {
            when(searches.getObservedPages()).thenReturn(List.of(
                    page(0, 3, UUID.randomUUID(), UUID.randomUUID()),
                    new EServiceCatalog(List.of(EServiceCatalog.Entry.builder().id(included.getId())
                            .version("2").isMine(false).build()), 2, 2, 3)));
            return null;
        }).when(searches).awaitPresence(List.of(included));

        steps.shouldReportProperties("ES-1",
                List.of(property("versione presentata", "2"), property("è mio", "no")));

        assertThatThrownBy(() -> steps.shouldReportProperties("ES-1", List.of(property("è mio", "sì"))))
                .isInstanceOf(AssertionError.class);
        verify(searches, times(1)).awaitPresence(List.of(included));
        verify(searches, never()).getSearchPages();
        verifyNoInteractions(gateway);
    }

    @Test
    void propertyPresenceTimeoutIsPropagatedWithoutFallingBackToCompletePagination() {
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(searches.getObservedPages()).thenReturn(List.of(result()));
        var failure = new IllegalStateException("Timed out waiting for catalog presence");
        doThrow(failure).when(searches).awaitPresence(List.of(included));

        assertThatThrownBy(() -> steps.shouldReportProperties("ES-1", List.of(property("versione presentata", "2"))))
                .isSameAs(failure);
        verify(searches, never()).getSearchPages();
        verifyNoInteractions(gateway);
    }

    private BffSearchEServiceCatalogCommand filteredCommand(int offset, int limit) {
        var command = new BffSearchEServiceCatalogCommand();
        command.offset(offset).limit(limit).keyword("keyword").producersIds(List.of(UUID.randomUUID()));
        return command;
    }

    private EServiceCatalog page(int offset, int total, UUID... ids) {
        return new EServiceCatalog(java.util.Arrays.stream(ids)
                .map(id -> EServiceCatalog.Entry.builder().id(id).build()).toList(), offset, 2, total);
    }

    @Test
    void defaultTransformerValidatesAliasDto() {
        try (var validators = Validation.buildDefaultValidatorFactory()) {
            var transformer = new DataTableMapper(validators.getValidator());
            var dto = (EServiceCatalogExpectation) transformer.transform(
                    Map.of("alias", "ES-2"), EServiceCatalogExpectation.class);
            assertThat(dto.getAlias()).isEqualTo("ES-2");
            assertThatThrownBy(() -> transformer.transform(Map.of("alias", " "), EServiceCatalogExpectation.class))
                    .isInstanceOf(ConstraintViolationException.class);
            assertThatThrownBy(() -> transformer.transform(Map.of(), EServiceCatalogExpectation.class))
                    .isInstanceOf(ConstraintViolationException.class);
        } finally {
            DataTableMapper.clearGherkinKeys();
        }
    }

    private EServiceCatalogExpectation expectation(String alias) {
        var dto = new EServiceCatalogExpectation();
        dto.setAlias(alias);
        return dto;
    }

    private it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogPropertyExpectation
    property(String property, String value) {
        var expectation =
                new it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogPropertyExpectation();
        expectation.setProperty(property);
        expectation.setValue(value);
        return expectation;
    }

    private EServiceCatalog result(EService... services) {
        return new EServiceCatalog(java.util.Arrays.stream(services)
                .map(service -> EServiceCatalog.Entry.builder().id(service.getId()).name(service.getName()).build())
                .toList(), 0, 20, services.length);
    }
}
