package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.DigitalDomicileToActivate;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.EmailContact;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.EmailToAdd;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.PecContact;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.SmsContact;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.component.SpecialContacts;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import it.pagopa.send.web.infrastructure.page.AddressPage;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/recapiti}
 * Pagina "I tuoi recapiti" del cittadino.
 * Si apre dalla voce "I tuoi recapiti" del menu laterale.
 * Contiene le card del domicilio digitale, di SEND sull'app IO e dell'email, più quella del cellulare se l'utente ne ha
 * uno. Il contenuto delle card dipende dai recapiti dell'utente: ogni recapito attivo mostra il valore, "Modifica" e
 * "Disattiva" o "Elimina"; un recapito da attivare mostra il modo per inserirlo.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo e le card del domicilio digitale, dell'app IO e
 * dell'email; testi, titoli e stati delle card, comuni a tutti gli utenti, sono verificati da
 * {@code WebAddressPFContractTest}.
 * L'assertLoaded è usato anche dagli step Cucumber dei recapiti.
 */
@Url("${url.notifiche.cittadino.recapiti}")
public interface AddressPFPage extends AddressPage, Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    // card del domicilio digitale

    @XPath("//*[@data-testid=\"legalContactsTitle\"]")
    Readable<String> legalContactsTitle();

    @XPath("//*[@data-testid=\"legalContactsTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
    Readable<String> legalContactsStatus();

    // contenuto della card, per sapere se il domicilio è attivo senza attendere i suoi elementi
    @XPath("//*[@data-testid=\"legalContacts\"]")
    Readable<String> legalContactsContent();

    @XPath("//*[@data-testid=\"legalContacts\"]")
    PecContact pecContact();

    @XPath("//*[@data-testid=\"specialContacts\"]")
    SpecialContacts specialContacts();

    @XPath("//*[@data-testid=\"legalContacts\"]")
    DigitalDomicileToActivate digitalDomicileToActivate();

    // card di SEND sull'app IO

    @XPath("//*[@data-testid=\"ioContactTitle\"]")
    Readable<String> ioContactTitle();

    @XPath("//*[@data-testid=\"ioContactTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
    Readable<String> ioStatus();

    @XPath("//*[@data-testid=\"ioContactDescription\"]")
    Readable<String> ioContactDescription();

    @XPath("//*[@id=\"ioContactSection\"]//button")
    Button downloadIoAppButton();

    // card dell'email (e del cellulare da aggiungere, se l'utente non ne ha uno)

    @XPath("//*[@data-testid=\"emailContactTitle\"]")
    Readable<String> emailContactTitle();

    @XPath("//*[@id=\"emailContactSection\"]")
    Readable<String> emailContactContent();

    @XPath("//*[@id=\"emailContactSection\"]")
    EmailContact emailContact();

    @XPath("//*[@id=\"emailContactSection\"]")
    EmailToAdd emailToAdd();

    // card del cellulare, presente solo se l'utente ne ha uno

    @XPath("//*[@data-testid=\"smsContactTitle\"]/ancestor::*[.//*[@data-testid=\"default_smsContact\"]][1]")
    SmsContact smsContact();

    // contenuto della pagina, per sapere quali card ci sono senza attenderle
    @XPath("//main")
    Readable<String> content();

    @XPath("//main")
    DigitalDomicileManagementPFPage digitalDomicileManagement();

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi recapiti"));
        legalContactsTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        ioContactTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        emailContactTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
