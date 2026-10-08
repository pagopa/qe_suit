package it.pagopa.interop.web.agreement.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.interop.web.agreement.infrastructure.page.component.AgreementProducerFilter;
import it.pagopa.interop.web.agreement.infrastructure.page.component.AgreementProducerTable;

/**
 * Pagina "Richieste di fruizione ricevute" (/erogazione/richieste).
 * <p>
 * {@link #assertLoaded()} conferma solo il caricamento (h1). Le aspettative sul contenuto
 * stanno nel contract test. Selettori da validare sul DOM QA.
 */
@Url("${interop.web.agreement-producer}")
public interface AgreementProducerPage extends Page {

    String TITLE = "Richieste di fruizione ricevute";
    long TABLE_LOAD_TIMEOUT_MILLIS = 30_000;
    long TABLE_LOAD_POLL_MILLIS = 250;

    @XPath(".//h1")
    Label pageTitle();

    @XPath(".//h1/following::p[1]")
    Label description();

    @XPath(".//div[contains(@class, 'MuiAutocomplete-root')][.//label[normalize-space()='Cerca per e-service']]")
    AgreementProducerFilter eServiceFilter();

    @XPath(".//div[contains(@class, 'MuiAutocomplete-root')][.//label[normalize-space()='Cerca per fruitore']]")
    AgreementProducerFilter consumerFilter();

    @XPath(".//div[contains(@class, 'MuiAutocomplete-root')][.//label[normalize-space()='Stato della richiesta']]")
    AgreementProducerFilter statusFilter();

    /**
     * Filtro che NON deve esistere (solo lato fruitore).
     */
    @XPath(".//div[contains(@class, 'MuiAutocomplete-root')][.//label[normalize-space()='Cerca per erogatore']]")
    AgreementProducerFilter producerFilter();

    AgreementProducerTable table();

    Pagination pagination();

    @XPath(".//nav[contains(@class, 'MuiPagination-root')]//button[contains(@class, 'Mui-selected')]")
    Readable<String> selectedPageButton();

    @XPath(".//*[contains(@class, 'MuiSkeleton-root')]")
    Component tableSkeleton();

    /**
     * Colonna "Erogatore" (non attesa).
     */
    @XPath(".//table//th[normalize-space()='Erogatore']")
    Component producerColumnHeader();

    /**
     * Pulsante "Modifica" (stato DRAFT, non atteso).
     */
    @XPath(".//*[(self::button or self::a) and normalize-space()='Modifica']")
    Component editButton();

    /**
     * Pulsante di creazione (non atteso).
     */
    @XPath(".//*[(self::button or self::a) and (normalize-space()='Crea' or starts-with(normalize-space(), 'Crea '))]")
    Component createButton();

    /**
     * Attende la scomparsa dello skeleton della tabella (dati asincroni).
     */
    default void waitUntilTableLoaded() {
        long deadline = System.currentTimeMillis() + TABLE_LOAD_TIMEOUT_MILLIS;
        while (tableSkeleton().get(FindPolicy.PRESENT).isPresent()) {
            if (System.currentTimeMillis() > deadline) {
                throw new IllegalStateException("Skeleton della tabella ancora presente dopo "
                        + TABLE_LOAD_TIMEOUT_MILLIS + " ms");
            }
            try {
                Thread.sleep(TABLE_LOAD_POLL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrotto in attesa del caricamento della tabella", e);
            }
        }
    }

    /**
     * Solo conferma di caricamento: nessuna asserzione su copy, filtri o tabella.
     */
    @Override
    default void assertLoaded() {
        pageTitle().readAndAssert(TITLE);
    }
}

