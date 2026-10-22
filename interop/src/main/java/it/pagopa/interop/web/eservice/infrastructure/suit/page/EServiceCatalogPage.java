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

@Url("${interop.web.catalog}")
public interface EServiceCatalogPage extends Page {

    String URL = "/catalogo-e-service";
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
    @XPath("//input[@placeholder='" + SEARCH_BY_KEY_PLACEHOLDER + "']")
    TextField searchByName();

    @XPath("//*[contains(@placeholder, '" + SEARCH_BY_PROVIDER_PLACEHOLDER + "') or contains(text(), '" + SEARCH_BY_PROVIDER_PLACEHOLDER + "')]")
    Readable<String> searchByProvider();

    //Bottone di ricerca
    @XPath("//button[contains(@class, 'MuiButtonBase-root') and not(contains(@class, 'MuiPaginationItem-root'))]")
    Button searchButton();

    //Bottone filtri (apre un padding laterale con i filtri)
    @XPath("//button[contains(@class, 'MuiButtonBase-root') and not(contains(@class, 'MuiPaginationItem-root')) and contains(text(), 'Filtri')]")
    Button filtersButton();

    @XPath("//div[contains(@class, 'MuiStack-root'))]")
    EServiceCatalogFiltersDrawer filtersDrawer();

    //Label che mostra il numero di risultati della ricerca (es. "4061 risultati")
    @XPath("//*[contains(text(), 'risultati')]")
    Label resultsCounterLabel();

    // Bottone di ordinamento (es. "Dal più recente")
    @XPath("//*[@role='button' and contains(., 'Dal più recente')]")
    Button sortButton();

    //----------GRIGLIA---------

    // Nome Erogatore (<span> con classe MuiTypography-caption)
    @XPath("(//div[contains(@class, 'MuiGrid-item')])[1]//span[contains(@class, 'MuiTypography-caption')]")
    Label cardProviderName();

    // Nome dell'E-Service (<h6> con classe MuiTypography-h6)
    @XPath("(//div[contains(@class, 'MuiGrid-item')])[1]//h6[contains(@class, 'MuiTypography-h6')]")
    Label cardEServiceName();

    // Descrizione E-Service (semplice tag <p>)
    @XPath("(//div[contains(@class, 'MuiGrid-item')])[1]//p")
    Label cardDescription();

    // Bottone "Vai all'e-service" (Classe MuiButtonBase)
    @XPath("(//div[contains(@class, 'MuiGrid-item')])[1]//button[contains(@class, 'MuiButtonBase')]")
    Button cardActionButton();

    // Contenitore della griglia
    @XPath("//div[contains(@class, 'MuiGrid-container')]")
    Gettable<?> grid();

    // Paginazione e Stato vuoto
    @XPath("//nav[contains(@class, 'MuiPagination-root')]")
    Pagination pagination();

    @XPath("//*[contains(@class, 'MuiAlert-root') or contains(text(), 'Nessun risultato')]")
    Label emptyStateMessage();


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
