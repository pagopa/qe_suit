package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.application.context.EntityStore;
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
    private final EServiceCatalogUseCase catalog = new EServiceCatalogUseCase(gateway);
    private final EntityStore store = mock(EntityStore.class, CALLS_REAL_METHODS);
    private final EServiceCatalogSearchSteps searches = mock(EServiceCatalogSearchSteps.class);
    private final EServiceCatalogAssertionSteps steps = new EServiceCatalogAssertionSteps(
            searches, catalog, store, Mappers.getMapper(EServiceCatalogExpectationMapper.class));
    private final EService included = EService.builder().id(UUID.randomUUID()).name("same-name").build();
    private final EService excluded = EService.builder().id(UUID.randomUUID()).name("same-name").build();

    @Test
    void verifiesSinglePresenceAndMultipleAbsencesUsingIdsWithoutCallingGateway() {
        EService third = EService.builder().id(UUID.randomUUID()).build();
        when(store.getByAlias("ES-1", EService.class)).thenReturn(Optional.of(included));
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(store.getByAlias("ES-3", EService.class)).thenReturn(Optional.of(third));
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
        when(searches.getSearchResult()).thenReturn(result(included));

        assertThatThrownBy(() -> steps.shouldNotContainEServices(
                List.of(expectation("ES-2"), expectation("ES-1"))))
                .isInstanceOf(AssertionError.class).hasMessageContaining(included.getId().toString());
    }

    @Test
    void failsWhenRequestedIdIsMissingEvenIfNamesMatch() {
        when(store.getByAlias("ES-2", EService.class)).thenReturn(Optional.of(excluded));
        when(searches.getSearchResult()).thenReturn(result(included));

        assertThatThrownBy(() -> steps.shouldContainEService("ES-2"))
                .isInstanceOf(AssertionError.class).hasMessageContaining(excluded.getId().toString());
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

    private EServiceCatalog result(EService... services) {
        return new EServiceCatalog(java.util.Arrays.stream(services)
                .map(service -> EServiceCatalog.Entry.builder().id(service.getId()).name(service.getName()).build())
                .toList(), 0, 20, services.length);
    }
}
