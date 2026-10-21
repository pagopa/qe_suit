package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogPageExpectation;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogExpectationMapper;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogPaginationExpectationMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EServiceCatalogPaginationSteps {
    private final EServiceCatalogSearchSteps searchSteps;
    private final EServiceCatalogUseCase catalog;
    private final EntityStore entityStore;
    private final EServiceCatalogExpectationMapper expectationMapper;
    private final EServiceCatalogPaginationExpectationMapper paginationExpectationMapper;

    @Then("le pagine contengono i seguenti risultati, con totale dichiarato pari alla loro somma:")
    public void pagesContainExpectedResultsWithTotal(
            List<EServiceCatalogPageExpectation> expectations
    ) {
        var resultCounts = paginationExpectationMapper.toResultCounts(expectations);
        catalog.shouldHavePagination(searchSteps.getSearchPages(resultCounts.size()), resultCounts);
    }

    @Then("nessun e-service compare in più di una pagina")
    public void pagesContainNoDuplicateEServices() {
        catalog.shouldHaveNoDuplicateEServices(searchSteps.getSearchPages());
    }

    @Then("le pagine contengono complessivamente i seguenti e-service:")
    public void pagesContainExactlyEServices(List<EServiceCatalogExpectation> expectations) {
        catalog.shouldContainExactly(searchSteps.getSearchPages(),
                expectationMapper.toEServices(expectations, entityStore));
    }
}
