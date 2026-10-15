package it.pagopa.interop.web.eservice.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;

@Url("${interop.web.catalog}")
public interface EServiceCatalogPage extends Page {

    String URL = "/catalogo-e-service";

    //*[@id="interop-sidenav-main"]/div[3]/main/div/div[2]/div/div/h1
    @XPath(".//h1")
    Readable<String> pageTitle();

    // Cerca il primo paragrafo (o elemento di testo) che si trova subito dopo l'h1
    @XPath("//h1/following-sibling::*[contains(@class, 'MuiTypography-root')] | //h1/parent::*//p")
    Readable<String> pageDescription();

    // Campi di ricerca
    @XPath("//input[@placeholder='Cerca per nome']")
    Readable<String> searchByName();

    @XPath("//*[contains(@placeholder, 'Cerca per erogatore') or contains(text(), 'Cerca per erogatore')]")
    Readable<String> searchByProvider();

    //----------GRIGLIA---------

    // Nome Erogatore (<span> con classe MuiTypography-caption)
    @XPath("(//div[contains(@class, 'MuiGrid-item')])[1]//span[contains(@class, 'MuiTypography-caption')]")
    Readable<String> cardProviderName();

    // Nome dell'E-Service (<h6> con classe MuiTypography-h6)
    @XPath("(//div[contains(@class, 'MuiGrid-item')])[1]//h6[contains(@class, 'MuiTypography-h6')]")
    Readable<String> cardEServiceName();

    // Descrizione E-Service (semplice tag <p>)
    @XPath("(//div[contains(@class, 'MuiGrid-item')])[1]//p")
    Readable<String> cardDescription();

    // Bottone "Visualizza" (Classe MuiButtonBase)
    @XPath("(//div[contains(@class, 'MuiGrid-item')])[1]//button[contains(@class, 'MuiButtonBase') and contains(., 'Visualizza')]")
    Button cardActionButton();

    // Contenitore della griglia
    @XPath("//div[contains(@class, 'MuiGrid-container')]")
    Gettable<?> grid();

    // Paginazione e Stato vuoto
    @XPath("//nav[contains(@class, 'MuiPagination-root')]")
    Gettable<?> pagination();

    @XPath("//*[contains(@class, 'MuiAlert-root') or contains(text(), 'Nessun risultato')]")
    Gettable<?> emptyStateMessage();


    /**
     * Criterio di pagina caricata:
     * Verifica la comparsa di titolo h1 e dei due campi di ricerca.
     */
    @Override
    default void assertLoaded() {
        searchByName().readAndAssert("Cerca per nome");
        searchByProvider().readAndAssert("Cerca per erogatore");
        pageTitle().readAndAssert("Catalogo degli e-service");
    }


}
