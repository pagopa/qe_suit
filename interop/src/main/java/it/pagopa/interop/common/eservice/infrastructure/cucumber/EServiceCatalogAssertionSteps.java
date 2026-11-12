package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceCatalogPaginationUseCase;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogPropertyExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogExpectationMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class EServiceCatalogAssertionSteps {
    private final EServiceCatalogSearchSteps searchSteps;
    private final EServiceCatalogUseCase catalog;
    private final EServiceCatalogPaginationUseCase pagination;
    private final EntityStore entityStore;
    private final EServiceCatalogExpectationMapper expectationMapper;

    @Then("l'e-service {string} è presente tra i risultati della ricerca")
    public void shouldContainEService(String alias) {
        searchSteps.getSearchResult();
        searchSteps.awaitPresence(List.of(entityStore.getByAliasOrThrow(alias, EService.class)));
    }

    @Then("l'e-service {string} non è presente tra i risultati della ricerca")
    public void shouldNotContainEService(String alias) {
        pagination.shouldNotContainAcrossPages(searchSteps.getSearchCommand(), searchSteps.getSearchResult(),
                List.of(entityStore.getByAliasOrThrow(alias, EService.class)));
    }

    @Then("l'e-service {string} è presente una sola volta tra i risultati della ricerca")
    public void shouldAppearExactlyOnce(String alias) {
        List<EService> expected = expectationMapper.toEServices(List.of(expectation(alias)), entityStore);
        catalog.shouldAppearExactlyOnce(searchSteps.getSearchPages(), expected.get(0));
    }

    @Then("la ricerca termina senza errori")
    public void searchTerminatesWithoutErrors() {
        searchSteps.assertSearchSucceeded();
    }

    @Then("la pagina è vuota e il totale dichiarato è {int}")
    public void shouldHaveEmptyPageWithTotalCount(int totalCount) {
        catalog.shouldBeEmptyWithTotalCount(searchSteps.getSearchResult(), totalCount);
    }

    @Then("tra i risultati della ricerca sono presenti, in quest'ordine, i seguenti e-service:")
    public void shouldContainEServicesInOrder(List<EServiceCatalogExpectation> expectations) {
        catalog.shouldContainInOrder(searchSteps.getSearchResult(),
                expectationMapper.toEServices(expectations, entityStore));
    }

    @Then("tra i risultati della ricerca, considerando tutte le pagine, i seguenti e-service compaiono in quest'ordine relativo:")
    public void shouldContainEServicesInRelativeOrder(List<EServiceCatalogExpectation> expectations) {
        pagination.shouldContainInRelativeOrder(searchSteps.getSearchCommand(), searchSteps.getSearchResult(),
                expectationMapper.toEServices(expectations, entityStore));
    }

    @Then("tra i risultati della ricerca sono presenti i seguenti e-service:")
    public void shouldContainEServices(List<EServiceCatalogExpectation> expectations) {
        searchSteps.getSearchResult();
        searchSteps.awaitPresence(expectationMapper.toEServices(expectations, entityStore));
    }

    @Then("tra i risultati della ricerca non sono presenti i seguenti e-service:")
    public void shouldNotContainEServices(List<EServiceCatalogExpectation> expectations) {
        List<EService> excluded = expectationMapper.toEServices(expectations, entityStore);
        pagination.shouldNotContainAcrossPages(searchSteps.getSearchCommand(), searchSteps.getSearchResult(),
                excluded);
    }

    @Then("per l'e-service {string} il risultato riporta:")
    public void shouldReportProperties(String alias, List<EServiceCatalogPropertyExpectation> properties) {
        assertThat(properties).as("Proprietà attese per l'e-service %s", alias).isNotEmpty();
        EService service = entityStore.getByAliasOrThrow(alias, EService.class);
        UUID id = service.getId();
        EServiceCatalog.Entry entry = catalog.findEntry(searchSteps.getObservedPages(), id)
                .or(() -> {
                    searchSteps.awaitPresence(List.of(service));
                    return catalog.findEntry(searchSteps.getObservedPages(), id);
                })
                .orElseThrow(() -> new AssertionError("E-service " + alias + " non presente nei risultati"));
        for (EServiceCatalogPropertyExpectation property : properties) {
            assertProperty(entry, property);
        }
    }

    private static void assertProperty(EServiceCatalog.Entry entry, EServiceCatalogPropertyExpectation property) {
        switch (property.getProperty()) {
            case "versione presentata" -> assertThat(entry.getVersion()).isEqualTo(property.getValue());
            case "è mio" -> assertThat(entry.getIsMine()).isEqualTo(parseBoolean(property.getValue()));
            case "notifiche non lette" ->
                    assertThat(entry.getHasUnreadNotifications()).isEqualTo(parseBoolean(property.getValue()));
            default -> throw new IllegalArgumentException("Proprietà catalogo non supportata: " + property.getProperty());
        }
    }

    private static boolean parseBoolean(String value) {
        return switch (value) {
            case "sì", "si" -> true;
            case "no" -> false;
            default -> throw new IllegalArgumentException("Valore booleano non valido: " + value);
        };
    }

    private EServiceCatalogExpectation expectation(String alias) {
        EServiceCatalogExpectation expectation = new EServiceCatalogExpectation();
        expectation.setAlias(alias);
        return expectation;
    }
}
