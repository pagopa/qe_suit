package it.pagopa.interop.web.eservice.infrastructure.suit.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.catalog.EServiceCatalogCard;

import java.util.List;

@Url("${interop.web.catalog}")
public interface EServiceCatalogPage extends Page {

    String PAGE_TITLE = "Catalogo degli e-service";
    String SEARCH_BY_KEY_PLACEHOLDER = "Cerca per parola chiave";
    String SEARCH_BY_PROVIDER_PLACEHOLDER = "Cerca per erogatore";

    //*[@id="interop-sidenav-main"]/div[3]/main/div/div[2]/div/div/h1
    @XPath(".//h1")
    Label pageTitle();

    // Cerca il primo paragrafo (o elemento di testo) che si trova subito dopo l'h1
    @XPath("//h1/following-sibling::*[contains(@class, 'MuiTypography-root')] | //h1/parent::*//p")
    Label pageDescription();

    // Campi di ricerca
    @XPath(".//label[@for='keyword']")
    TextField searchByName();

    @XPath(".//label[normalize-space()='Cerca per erogatore']")
    TextField searchByProvider();

    //Bottone di ricerca
    @XPath(".//button[contains(@class, 'MuiButtonBase-root') and not(contains(@class, 'MuiPaginationItem-root'))]")
    Button searchButton();

    //Bottone filtri (apre un padding laterale con i filtri)
    @XPath(".//button[contains(@class, 'MuiButtonBase-root') and not(contains(@class, 'MuiPaginationItem-root')) and contains(text(), 'Filtri')]")
    Button filtersButton();

    @XPath(".//div[contains(@class, 'MuiStack-root')]")
    EServiceCatalogFiltersDrawer filtersDrawer();

    //Label che mostra il numero di risultati della ricerca (es. "4061 risultati")
    @XPath(".//*[contains(text(), 'risultati')]")
    Label resultsCounterLabel();

    // Bottone di ordinamento (es. "Dal più recente")
    @XPath(".//button[contains(@class, 'MuiButtonBase-root') and contains(text(), 'Dal più recente') or contains(text(), 'Dal meno recente') or contains(text(), 'Nome e-service A-Z') or contains(text(), 'Nome e-service Z-A')]")
    Button sortButton();

    //----------GRIGLIA---------
    List<EServiceCatalogCard> eServiceCards();

    // Paginazione e Stato vuoto
    @XPath("//nav[contains(@class, 'MuiPagination-root')]")
    Pagination pagination();

    @XPath(".//*[@role='status']//p[contains(text(), 'Non ci sono risultati')]")
    Label emptyStateMessage();


    default void search(String name) {
        searchByName().cleanAndWriteAndAssert(name);
        searchButton().click();
    }

    /**
     * Criterio di pagina caricata:
     * Verifica la comparsa di titolo h1 e dei due campi di ricerca.
     */
    @Override
    default void assertLoaded() {
        searchByName().readAndAssert(SEARCH_BY_KEY_PLACEHOLDER);
        searchByProvider().readAndAssert(SEARCH_BY_PROVIDER_PLACEHOLDER);
        pageTitle().readAndAssert(PAGE_TITLE);
    }
}
