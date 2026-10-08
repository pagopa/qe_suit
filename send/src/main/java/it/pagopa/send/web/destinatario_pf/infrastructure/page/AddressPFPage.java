package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.infrastructure.page.AddressPage;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
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
 * dell'email; testi, recapiti e validazioni sono verificati da {@code WebAddressPFContractTest}.
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

    /**
     * Domicilio digitale attivo su PEC: valore, "Modifica", "Gestisci" e "Disattiva", con l'avviso sull'indirizzo
     * principale. "Modifica" apre un campo con "Conferma": un valore valido avvia la verifica della PEC, quindi nei test si
     * usano solo valori non validi.
     */
    interface PecContact extends Component {
        @XPath("//*[@id=\"default_pec-typography\"]")
        Readable<String> pecValue();

        @XPath("//*[@id=\"modifyContact-default_pec\"]")
        Button modifyPecButton();

        @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardHeader\"]//button[normalize-space()=\"Gestisci\"]")
        Button manageButton();

        @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardHeader\"]//button[normalize-space()=\"Disattiva\"]")
        Button disableButton();

        @XPath("//*[@data-testid=\"default_pecContact\"]//input")
        TextField editPecInput();

        @XPath("//*[@id=\"saveContact-default_pec\"]")
        Button savePecButton();

        @XPath("//*[@id=\"default_pec-helper-text\"]")
        Readable<String> editPecErrorMessage();

        // labels

        @XPath("//*[@data-testid=\"default_pecContact\"]/following-sibling::p[1]")
        Readable<String> description();

        @XPath("//*[@data-testid=\"legalContacts\"]//*[contains(@class,'MuiAlert-message')]")
        Readable<String> mainAddressAlert();

        @Override
        default void assertLoaded() {
            pecValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    /**
     * PEC personalizzate per ente: per ogni ente il nome, la PEC, "Modifica" ed "Elimina". Ogni elemento legge la colonna
     * di tutti gli enti; i campi di modifica si individuano dall'id del recapito dell'ente.
     */
    interface SpecialContacts extends Component {
        @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//span[contains(@id,\"_pec-typography\")]")
        Readable<String> values();

        @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"modifyContact-\")]")
        Button modifyButtons();

        @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"cancelContact-\")]")
        Readable<String> deleteButtons();

        @XPath("(//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"modifyContact-\")])[1]")
        Button firstModifyButton();

        @XPath("(//*[contains(@data-testid,\"_pecSpecialContact\")]//input)[1]")
        TextField firstEditInput();

        @XPath("(//*[contains(@data-testid,\"_pecSpecialContact\")]//button[starts-with(@id,\"saveContact-\")])[1]")
        Button firstSaveButton();

        @XPath("(//*[contains(@data-testid,\"_pecSpecialContact\")]//p[contains(@id,\"-helper-text\")])[1]")
        Readable<String> firstEditErrorMessage();

        // labels

        @XPath("//*[@data-testid=\"specialContacts\"]/p[1]")
        Readable<String> title();

        @XPath("//*[contains(@data-testid,\"_pecSpecialContact\")]/preceding-sibling::*[1]")
        Readable<String> senders();

        @Override
        default void assertLoaded() {
            values().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty());
        }
    }

    /**
     * Domicilio digitale da attivare: i tre vantaggi e il pulsante "Inizia", che apre il wizard di attivazione.
     */
    interface DigitalDomicileToActivate extends Component {
        @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardBody\"]/button")
        Button startButton();

        // labels

        @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardBody\"]/p[1]")
        Readable<String> whyUseful();

        @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardBody\"]/div//p[1]")
        Readable<String> benefitTitles();

        @XPath("//*[@data-testid=\"legalContacts\"]//*[@data-testid=\"PnInfoCardBody\"]/div//p[2]")
        Readable<String> benefitDescriptions();

        @Override
        default void assertLoaded() {
            whyUseful().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    /**
     * Email attiva: valore, "Modifica" e "Disattiva". "Modifica" apre un campo con "Conferma": un valore valido avvia
     * l'invio del codice di verifica, quindi nei test si usano solo valori non validi.
     */
    interface EmailContact extends Component {
        @XPath("//*[@id=\"default_email-typography\"]")
        Readable<String> emailValue();

        @XPath("//*[@id=\"modifyContact-default_email\"]")
        Button modifyEmailButton();

        @XPath("//*[@data-testid=\"disable-email\"]")
        Button disableEmailButton();

        @XPath("//*[@data-testid=\"default_emailContact\"]//input")
        TextField editEmailInput();

        @XPath("//*[@id=\"saveContact-default_email\"]")
        Button saveEmailButton();

        @XPath("//*[@id=\"default_email-helper-text\"]")
        Readable<String> editEmailErrorMessage();

        // labels

        @XPath("//*[@data-testid=\"emailContactTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
        Readable<String> status();

        @XPath("//*[@data-testid=\"default_emailContact\"]/following-sibling::p[1]")
        Readable<String> description();

        @Override
        default void assertLoaded() {
            emailValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    /**
     * Email da aggiungere: il campo con "Aggiungi email" e, sotto, il pulsante per aggiungere anche il cellulare, che apre
     * un campo con "Aggiungi numero" e "Annulla". Un valore valido avvia l'invio del codice di verifica, quindi nei test si
     * usano solo valori non validi.
     */
    interface EmailToAdd extends Component {
        @XPath("//*[@id=\"default_email-label\"]")
        Readable<String> emailInputLabel();

        @XPath("//*[@id=\"default_email\"]")
        TextField emailInput();

        @XPath("//*[@id=\"default_email-button\"]")
        Button addEmailButton();

        @XPath("//*[@id=\"default_email-helper-text\"]")
        Readable<String> emailErrorMessage();

        @XPath("//*[@id=\"emailContactSection\"]//button[normalize-space()=\"Aggiungi numero di cellulare\"]")
        Button addSmsButton();

        @XPath("//*[@id=\"default_sms-label\"]")
        Readable<String> smsInputLabel();

        @XPath("//*[@id=\"default_sms\"]")
        TextField smsInput();

        @XPath("//*[@id=\"default_sms-button\"]")
        Button saveSmsButton();

        @XPath("//*[@id=\"default_sms-helper-text\"]")
        Readable<String> smsErrorMessage();

        @XPath("//*[@data-testid=\"default_smsContact\"]/following-sibling::button[normalize-space()=\"Annulla\"] | //*[@data-testid=\"default_smsContact\"]//button[normalize-space()=\"Annulla\"]")
        Button cancelSmsButton();

        // labels

        @XPath("//*[@id=\"emailContactSection\"]//*[@data-testid=\"PnInfoCardBody\"]/p[1]")
        Readable<String> description();

        @XPath("//*[@id=\"emailContactSection\"]//button[normalize-space()=\"Aggiungi numero di cellulare\"]/preceding-sibling::p[1]")
        Readable<String> smsQuestion();

        @Override
        default void assertLoaded() {
            description().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    /**
     * Cellulare attivo: valore, "Modifica" e "Disattiva". "Modifica" apre un campo con "Conferma": un valore valido avvia
     * l'invio del codice di verifica, quindi nei test si usano solo valori non validi.
     */
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

        @XPath("//*[@data-testid=\"default_smsContact\"]//input")
        TextField editSmsInput();

        @XPath("//*[@id=\"saveContact-default_sms\"]")
        Button saveSmsButton();

        @XPath("//*[@id=\"default_sms-helper-text\"]")
        Readable<String> editSmsErrorMessage();

        // labels

        @XPath("//*[@data-testid=\"smsContactTitle\"]/ancestor::*[@data-testid=\"PnInfoCardHeader\"]//*[contains(@class,'MuiChip-label')]")
        Readable<String> status();

        @Override
        default void assertLoaded() {
            smsValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::acceptIfShown);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("I tuoi recapiti"));
        legalContactsTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        ioContactTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        emailContactTitle().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
