package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/profilo}
 * Pagina "I tuoi dati" del cittadino.
 * Si apre dal menu dell'area utente in alto (pulsante con il nome dell'utente).
 * Mostra nome, cognome e codice fiscale ricavati da SPID o CIE, non modificabili, in un riquadro con una riga per dato:
 * l'etichetta a sinistra e il valore a destra.
 * L'assertLoaded verifica che la pagina sia caricata, cioè che il titolo sia "I tuoi dati" e che ci siano le tre righe;
 * testi e valori sono verificati da {@code WebProfilePFContractTest}.
 */
@Url("${url.notifiche.cittadino.profilo}")
public interface ProfilePFPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    // labels: righe del riquadro dei dati, con l'etichetta nella prima colonna e il valore nella seconda

    @XPath("//*[@id=\"page-header-container\"]/following-sibling::div//div[contains(@class,\"MuiPaper-root\")]/div/div[1]/p")
    Readable<String> labels();

    @XPath("//*[@id=\"page-header-container\"]/following-sibling::div//div[contains(@class,\"MuiPaper-root\")]/div/div[2]/p")
    Readable<String> values();

    @XPath("(//*[@id=\"page-header-container\"]/following-sibling::div//div[contains(@class,\"MuiPaper-root\")]/div/div[2]/p)[1]")
    Readable<String> firstName();

    @XPath("(//*[@id=\"page-header-container\"]/following-sibling::div//div[contains(@class,\"MuiPaper-root\")]/div/div[2]/p)[2]")
    Readable<String> lastName();

    @XPath("(//*[@id=\"page-header-container\"]/following-sibling::div//div[contains(@class,\"MuiPaper-root\")]/div/div[2]/p)[3]")
    Readable<String> taxCode();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi dati"));
        labels().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(3));
    }
}
