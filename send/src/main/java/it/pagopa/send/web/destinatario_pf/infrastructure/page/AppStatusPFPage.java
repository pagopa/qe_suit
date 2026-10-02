package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import org.assertj.core.api.Assertions;

import java.util.List;

/**
 * Questa pagina rappresenta lo stato della piattaforma per il cittadino, con lo stato attuale dei servizi SEND
 * e lo storico dei disservizi con le relative attestazioni scaricabili.
 * La pagina richiede che nello storico sia presente almeno un disservizio.
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
        downtimeTableHeaders().readAllAndAssert(List.of("Data di inizio", "Data di fine", "Servizio coinvolto", "Attestazioni opponibili a terzi", "Stato"));
        downtimeServices().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
        downloadLegalFactButtons().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isEqualTo("Scarica l'attestazione")));
        downtimeStatuses().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
        rowsPerPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("10"));
        Assertions.assertThat(previousPageButton().get(FindPolicy.PRESENT)).isPresent();
        firstPageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("1"));
        Assertions.assertThat(nextPageButton().get(FindPolicy.PRESENT)).isPresent();

        // labels
        downtimeHistoryTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Storico dei disservizi"));
    }
}
