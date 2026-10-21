package it.pagopa.interop.common.eservice.infrastructure.cucumber;

import io.cucumber.java.en.When;
import it.pagopa.interop.common.eservice.application.EServiceCatalogUseCase;
import it.pagopa.interop.common.eservice.application.EServiceRequestFactory;
import it.pagopa.interop.common.eservice.application.command.SearchEServiceCatalogCommand;
import it.pagopa.interop.common.eservice.domain.EServiceCatalog;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.EServiceCatalogSearchConfiguration;
import it.pagopa.interop.common.eservice.infrastructure.cucumber.dto.mapper.EServiceCatalogSearchConfigurationMapper;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EServiceCatalogSearchSteps {
    private final EServiceCatalogUseCase catalog;
    private final EServiceRequestFactory requestFactory;
    private final CurrentUserSession session;
    private final EServiceCatalogSearchConfigurationMapper configurationMapper;
    private SearchEServiceCatalogCommand searchCommand;
    private EServiceCatalog searchResult;
    private List<EServiceCatalog> searchPages;
    private boolean searchSucceeded;

    @When("(il ){tenant} cerca nel catalogo {string} con i seguenti filtri:")
    public void searchWithFilters(
            Tenant requester, String keyword, List<EServiceCatalogSearchConfiguration> filters
    ) {
        search(requester, keyword, filters);
    }

    @When("(il ){tenant} cerca nel catalogo {string}")
    public void search(Tenant requester, String keyword) {
        search(requester, keyword, List.of());
    }

    @When("(il ){tenant} cerca nel catalogo senza indicare alcuna parola")
    public void searchWithoutKeyword(Tenant requester) {
        search(requester, null, List.of());
    }

    @When("(il ){tenant} cerca nel catalogo senza indicare alcuna parola con i seguenti filtri:")
    public void searchWithoutKeywordWithFilters(
            Tenant requester, List<EServiceCatalogSearchConfiguration> filters
    ) {
        search(requester, null, filters);
    }

    @When("(il ){tenant} cerca nel catalogo una parola composta soltanto da spazi")
    public void searchWithWhitespace(Tenant requester) {
        search(requester, "   ", List.of());
    }

    public EServiceCatalog getSearchResult() {
        if (!searchSucceeded || searchResult == null) {
            throw new IllegalStateException("No catalog search has been performed in this scenario");
        }
        return searchResult;
    }

    public List<EServiceCatalog> getSearchPages() {
        getSearchResult();
        if (searchPages == null) {
            searchPages = catalog.searchPages(searchCommand, searchResult);
        }
        return searchPages;
    }

    public List<EServiceCatalog> getSearchPages(int pageCount) {
        getSearchResult();
        if (searchPages == null) {
            searchPages = catalog.searchPages(searchCommand, searchResult, pageCount);
        }
        return searchPages;
    }

    public SearchEServiceCatalogCommand getSearchCommand() {
        if (!searchSucceeded || searchCommand == null) {
            throw new IllegalStateException("No successful catalog search has been performed in this scenario");
        }
        return searchCommand;
    }

    public void assertSearchSucceeded() {
        getSearchResult();
    }

    private void search(Tenant requester, String keyword, List<EServiceCatalogSearchConfiguration> filters) {
        searchCommand = null;
        searchResult = null;
        searchPages = null;
        searchSucceeded = false;
        SearchEServiceCatalogCommand command = requestFactory.defaultSearchEServiceCatalogCommand();
        command.keyword(keyword);
        configurationMapper.apply(filters, command);
        session.set(User.getTenantAdmin(requester), requester);
        EServiceCatalog result = catalog.search(command);
        if (result == null) {
            throw new IllegalStateException("Catalog search returned no result");
        }
        searchCommand = command;
        searchResult = result;
        searchSucceeded = true;
    }

}
