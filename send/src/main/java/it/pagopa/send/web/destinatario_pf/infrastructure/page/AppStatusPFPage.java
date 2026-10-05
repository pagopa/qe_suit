package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/app-status}
 * Pagina "Stato della piattaforma" del cittadino.
 * Si apre dalla voce "Stato della piattaforma" del menu laterale.
 * Contiene lo stato attuale dei servizi SEND e lo storico dei disservizi con le attestazioni scaricabili.
 * L'assertLoaded verifica solo gli elementi sempre presenti; tabella e paginazione dello storico
 * compaiono solo se la piattaforma ha registrato almeno un disservizio e non vengono verificate.
 */
@Url("${url.notifiche.cittadino.app-status}")
public interface AppStatusPFPage extends Page {

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"app-status-bar\"]")
    Readable<String> statusBar();

    @XPath("//*[@data-testid=\"appStatus-lastCheck\"]")
    Readable<String> lastCheck();

    @XPath("//*[@data-testid=\"tableDowntimeLog.header.cell\"]")
    Readable<String> downtimeTableHeaders();

    @XPath("//*[@data-testid=\"tableDowntimeLog.row\"]/td[3]")
    Readable<String> downtimeServices();

    @XPath("//*[@data-testid=\"download-legal-fact\"]")
    Readable<String> downloadLegalFactButtons();

    @XPath("//*[@data-testid=\"downtime-status\"]")
    Readable<String> downtimeStatuses();

    @XPath("//*[@id=\"rows-per-page\"]")
    Button rowsPerPageButton();

    @XPath("//*[@id=\"previous\"]")
    Button previousPageButton();

    @XPath("//*[@id=\"page1\"]")
    Button firstPageButton();

    @XPath("//*[@id=\"next\"]")
    Button nextPageButton();

    // labels

    @XPath("//*[@id=\"appStatusLastCheck\"]/following-sibling::h2[1]")
    Readable<String> downtimeHistoryTitle();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isIn("Platform status", "Stato della piattaforma"));
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Stato della piattaforma"));
        subtitle().readAndAssert(h -> Assertions.assertThat(h).startsWith("Verifica il funzionamento di SEND"));
        statusBar().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        lastCheck().readAndAssert(h -> Assertions.assertThat(h).startsWith("Ultimo aggiornamento"));

        // labels
        downtimeHistoryTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Storico dei disservizi"));
    }
}
