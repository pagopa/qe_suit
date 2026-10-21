package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import it.pagopa.interop.bff.eservice.application.BffSearchEServiceCatalogCommand;
import it.pagopa.interop.bff.eservice.infrastructure.BffEServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogSearchConfigurationMapper;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceCatalogSearchStepsTest {
    private final EServiceCatalogUseCase catalog = mock(EServiceCatalogUseCase.class);
    private final CurrentUserSession session = mock(CurrentUserSession.class);
    private final EServiceRequestFactory factory = spy(new BffEServiceRequestFactory());
    private final EServiceCatalogSearchSteps steps = new EServiceCatalogSearchSteps(
            catalog, factory, session, Mappers.getMapper(EServiceCatalogSearchConfigurationMapper.class));

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
        var order = inOrder(session, catalog);
        order.verify(session).set(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA);
        order.verify(catalog).search(command);
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
    void failedSearchDoesNotExposePreviousResult() {
        when(catalog.search(any())).thenReturn(new EServiceCatalog(List.of(), 0, 20, 0));
        steps.search(Tenant.PAGO_PA, "first");
        when(catalog.search(any())).thenThrow(new IllegalStateException("backend failed"));

        assertThatThrownBy(() -> steps.search(Tenant.PAGO_PA, "second")).hasMessage("backend failed");
        assertThatThrownBy(steps::getSearchResult).isInstanceOf(IllegalStateException.class);
    }

    private EServiceCatalogSearchConfiguration filter(String filter, String value) {
        var configuration = new EServiceCatalogSearchConfiguration();
        configuration.setFilter(filter);
        configuration.setValue(value);
        return configuration;
    }
}
