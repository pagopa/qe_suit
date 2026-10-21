package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogExpectationMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EServiceCatalogAssertionSteps {
    private final EServiceCatalogSearchSteps searchSteps;
    private final EServiceCatalogUseCase catalog;
    private final EntityStore entityStore;
    private final EServiceCatalogExpectationMapper expectationMapper;

    @Then("l'e-service {string} è presente tra i risultati della ricerca")
    public void shouldContainEService(String alias) {
        catalog.shouldContain(searchSteps.getSearchResult(),
                List.of(entityStore.getByAliasOrThrow(alias, EService.class)));
    }

    @Then("l'e-service {string} non è presente tra i risultati della ricerca")
    public void shouldNotContainEService(String alias) {
        catalog.shouldNotContain(searchSteps.getSearchResult(),
                List.of(entityStore.getByAliasOrThrow(alias, EService.class)));
    }

    @Then("tra i risultati della ricerca sono presenti i seguenti e-service:")
    public void shouldContainEServices(List<EServiceCatalogExpectation> expectations) {
        catalog.shouldContain(searchSteps.getSearchResult(),
                expectationMapper.toEServices(expectations, entityStore));
    }

    @Then("tra i risultati della ricerca non sono presenti i seguenti e-service:")
    public void shouldNotContainEServices(List<EServiceCatalogExpectation> expectations) {
        catalog.shouldNotContain(searchSteps.getSearchResult(),
                expectationMapper.toEServices(expectations, entityStore));
    }
}
