package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import org.assertj.core.api.Assertions;

/**
 * {@code {baseUrl}/profilo}
 * Pagina "I tuoi dati" del cittadino.
 * Si apre dal menu dell'area utente in alto (pulsante con il nome dell'utente).
 * Mostra nome, cognome e codice fiscale ricavati da SPID o CIE, non modificabili.
 * L'assertLoaded verifica le etichette e che i valori siano presenti, senza controllare quelli di un utente specifico.
 */
@Url("${url.notifiche.cittadino.profilo}")
public interface ProfilePFPage extends Page {

    @XPath("//*[@id=\"item\"]")
    Readable<String> breadcrumbs();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    // labels

    @XPath("//main//p[normalize-space()=\"Nome\"]")
    Readable<String> firstNameLabel();

    @XPath("//main//p[normalize-space()=\"Nome\"]/../following-sibling::div[1]/p")
    Readable<String> firstName();

    @XPath("//main//p[normalize-space()=\"Cognome\"]")
    Readable<String> lastNameLabel();

    @XPath("//main//p[normalize-space()=\"Cognome\"]/../following-sibling::div[1]/p")
    Readable<String> lastName();

    @XPath("//main//p[normalize-space()=\"Codice fiscale\"]")
    Readable<String> taxCodeLabel();

    @XPath("//main//p[normalize-space()=\"Codice fiscale\"]/../following-sibling::div[1]/p")
    Readable<String> taxCode();

    @Override
    default void assertLoaded() {
        breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi dati"));
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi dati"));
        subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Questi dati vengono ricavati dal tuo SPID o CIE e non sono modificabili."));

        // labels
        firstNameLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Nome"));
        firstName().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        lastNameLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Cognome"));
        lastName().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        taxCodeLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Codice fiscale"));
        taxCode().readAndAssert(h -> Assertions.assertThat(h).matches("[A-Z0-9]{16}"));
    }
}
