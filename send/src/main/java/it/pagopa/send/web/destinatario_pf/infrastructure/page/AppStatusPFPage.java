package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/app-status}
 * Pagina "Stato della piattaforma" del cittadino.
 * Si apre dalla voce "Stato della piattaforma" del menu laterale.
 * Contiene lo stato attuale dei servizi SEND, con l'ora dell'ultimo aggiornamento, e lo storico dei disservizi con le
 * attestazioni scaricabili. Stato e storico cambiano nel tempo: tabella e paginazione dello storico compaiono solo se la
 * piattaforma ha registrato almeno un disservizio.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, lo stato attuale e l'ultimo aggiornamento; testi,
 * formato dello stato e dello storico sono verificati da {@code WebAppStatusPFContractTest}.
 */
@Url("${url.notifiche.cittadino.app-status}")
public interface AppStatusPFPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"app-status-bar\"]")
    Readable<String> statusBar();

    // icona dello stato: verde con la spunta quando tutti i servizi sono operativi
    @XPath("//*[@data-testid=\"app-status-bar\"]//*[@data-testid=\"CheckCircleRoundedIcon\"]")
    Readable<String> allServicesWorkingIcon();

    @XPath("//*[@data-testid=\"appStatus-lastCheck\"]")
    Readable<String> lastCheck();

    // storico dei disservizi: ogni elemento legge la colonna di tutte le righe

    @XPath("//*[@data-testid=\"tableDowntimeLog.header.cell\"]")
    Readable<String> downtimeTableHeaders();

    @XPath("//*[@data-testid=\"tableDowntimeLog.row\"]/td[1]")
    Readable<String> downtimeStartDates();

    @XPath("//*[@data-testid=\"tableDowntimeLog.row\"]/td[2]")
    Readable<String> downtimeEndDates();

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

    // contenuto della pagina dopo l'intestazione, per sapere se c'è lo storico senza attendere la tabella
    @XPath("//*[@id=\"appStatusLastCheck\"]/..")
    Readable<String> content();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Stato della piattaforma"));
        statusBar().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        lastCheck().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
