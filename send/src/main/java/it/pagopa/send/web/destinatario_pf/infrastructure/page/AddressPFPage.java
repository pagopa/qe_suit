package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.infrastructure.page.AddressPage;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/recapiti}
 * Pagina "I tuoi recapiti" del cittadino.
 * Si apre dalla voce "I tuoi recapiti" del menu laterale.
 * Contiene le card del domicilio digitale, di SEND sull'app IO, dell'email e del cellulare.
 * L'assertLoaded verifica solo gli elementi presenti per qualunque utente (titoli della pagina e delle card).
 * I componenti {@link PecContact}, {@link SpecialContacts}, {@link EmailContact} e {@link SmsContact} mappano
 * le sezioni che compaiono solo quando l'utente ha configurato il relativo recapito.
 */
@Url("${url.notifiche.cittadino.recapiti}")
public interface AddressPFPage extends AddressPage, Page {

    Optional<OneTrustBanner> oneTrustBanner();

    @XPath("//*[@data-testid=\"titleBox\"]")
    Readable<String> title();

    @XPath("//*[@id=\"subtitle-page\"]")
    Readable<String> subtitle();

    @XPath("//*[@data-testid=\"legalContactsTitle\"]")
    Readable<String> legalContactsTitle();

    @XPath("//*[@data-testid=\"ioContactTitle\"]")
    Readable<String> ioContactTitle();

    @XPath("//*[@data-testid=\"ioContactDescription\"]")
    Readable<String> ioContactDescription();

    @XPath("//*[@id=\"ioContactSection\"]//button")
    Button downloadIoAppButton();

    @XPath("//*[@data-testid=\"emailContactTitle\"]")
    Readable<String> emailContactTitle();

    @XPath("//*[@data-testid=\"legalContacts\"]")
    PecContact pecContact();

    @XPath("//*[@data-testid=\"specialContacts\"]")
    SpecialContacts specialContacts();

    @XPath("//*[@id=\"emailContactSection\"]")
    EmailContact emailContact();

    @XPath("//*[@data-testid=\"smsContactTitle\"]/ancestor::*[.//*[@data-testid=\"default_smsContact\"]][1]")
    SmsContact smsContact();

    @XPath("//main")
    DigitalDomicileManagementPFPage digitalDomicileManagement();

    // labels

    @XPath("//*[@data-testid=\"ioContactTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
    Readable<String> ioStatus();

    interface PecContact extends Component {
        @XPath("//*[@id=\"default_pec-typography\"]")
        Readable<String> pecValue();

        @XPath("//*[@id=\"modifyContact-default_pec\"]")
        Button modifyPecButton();

        @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardHeader\"]//button[normalize-space()=\"Gestisci\"]")
        Button manageButton();

        @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardHeader\"]//button[normalize-space()=\"Disattiva\"]")
        Button disableButton();

        // labels

        @XPath("//*[@data-testid=\"legalContactsTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
        Readable<String> status();

        @XPath("//*[@data-testid=\"default_pecContact\"]/following-sibling::p[1]")
        Readable<String> description();

        @Override
        default void assertLoaded() {
            pecValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
            modifyPecButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Modifica"));
            manageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Gestisci"));
            disableButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Disattiva"));

            // labels
            status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attivo"));
            description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Quando un ente ti invia una notifica SEND, viene recapitata in modo sicuro e con valore legale sulla PEC che hai scelto."));
        }
    }

    interface SpecialContacts extends Component {
        @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//span[contains(@id,\"_pec-typography\")]")
        Readable<String> values();

        @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"modifyContact-\")]")
        Readable<String> modifyButtons();

        @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"cancelContact-\")]")
        Readable<String> deleteButtons();

        // labels

        @XPath("//*[@data-testid=\"specialContacts\"]/p[1]")
        Readable<String> title();

        @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]/preceding-sibling::*[1]")
        Readable<String> senders();

        @Override
        default void assertLoaded() {
            values().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
            modifyButtons().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isEqualTo("Modifica")));
            deleteButtons().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isEqualTo("Elimina")));

            // labels
            title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("PERSONALIZZATI PER ENTE"));
            senders().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
        }
    }

    interface EmailContact extends Component {
        @XPath("//*[@id=\"default_email-typography\"]")
        Readable<String> emailValue();

        @XPath("//*[@id=\"modifyContact-default_email\"]")
        Button modifyEmailButton();

        @XPath("//*[@data-testid=\"disable-email\"]")
        Button disableEmailButton();

        // labels

        @XPath("//*[@data-testid=\"emailContactTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
        Readable<String> status();

        @XPath("//*[@data-testid=\"default_emailContact\"]/following-sibling::p[1]")
        Readable<String> description();

        @Override
        default void assertLoaded() {
            emailValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
            modifyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Modifica"));
            disableEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Disattiva"));

            // labels
            status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attivo"));
            description().readAndAssert(h -> Assertions.assertThat(h).contains("ti avvisiamo con una email"));
        }
    }

    interface SmsContact extends Component {
        @XPath("//*[@data-testid=\"smsContactTitle\"]")
        Readable<String> smsContactTitle();

        @XPath("//*[@id=\"default_sms-typography\"]")
        Readable<String> smsValue();

        @XPath("//*[@id=\"modifyContact-default_sms\"]")
        Button modifySmsButton();

        @XPath("//*[@data-testid=\"disable-sms\"]")
        Button disableSmsButton();

        @XPath("//*[@data-testid=\"smsContactDescription\"]")
        Readable<String> smsContactDescription();

        // labels

        @XPath("//*[@data-testid=\"smsContactTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
        Readable<String> status();

        @Override
        default void assertLoaded() {
            smsContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Il tuo cellulare"));
            smsValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
            modifySmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Modifica"));
            disableSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Disattiva"));
            smsContactDescription().readAndAssert(h -> Assertions.assertThat(h).endsWith("ti avvisiamo con un SMS."));

            // labels
            status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Attivo"));
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isIn("Addresses", "I tuoi recapiti"));
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi recapiti"));
        subtitle().readAndAssert(h -> Assertions.assertThat(h).startsWith("Gestisci i recapiti digitali su cui ricevere le comunicazioni a valore legale di SEND"));
        legalContactsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Il tuo domicilio digitale"));
        ioContactTitle().readAndAssert(h -> Assertions.assertThat(h).startsWith("SEND sull").endsWith("app IO"));
        emailContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Il tuo indirizzo email"));
    }
}
