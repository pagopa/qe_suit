package it.pagopa.send.web.destinatario_pf.infrastructure.page;

import it.frontend.e2e.framework.annotation.location.web.Url;
import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.web.login.infrastructure.page.component.OneTrustBanner;
import org.assertj.core.api.Assertions;

import java.util.Optional;

/**
 * {@code {baseUrl}/recapiti/domicilio-digitale/gestione}
 * Pagina "Gestisci il tuo domicilio digitale" del cittadino.
 * Si apre dal pulsante "Gestisci" della card domicilio digitale in "I tuoi recapiti".
 * Mostra il domicilio digitale attivo e le opzioni per modificarlo; la pagina è disponibile solo a un utente con un domicilio digitale attivo.
 * "Personalizza per ente" apre nella stessa pagina il form {@link CustomizeBySenderForm}, il cui "Conferma" salva la
 * PEC per ente; "Trasferisci su SEND", presente solo con il domicilio su PEC, apre nella stessa pagina il wizard di
 * attivazione di SEND, il cui "Conferma" sostituisce la PEC. Nei test si usano solo valori non validi e non si
 * conferma il wizard.
 * L'assertLoaded verifica che la pagina sia caricata, cioè il titolo, "Personalizza per ente" e "Indietro"; stato,
 * indirizzo, testi e form sono verificati da {@code WebDigitalDomicileManagementPFContractTest}.
 */
@Url("${url.notifiche.cittadino.recapiti-domicilio-digitale-gestione}")
public interface DigitalDomicileManagementPFPage extends Page {

    Optional<OneTrustBanner> oneTrustBanner();

    // titolo della pagina; dopo "Trasferisci su SEND" è il titolo del wizard di trasferimento
    @XPath("//*[@data-testid=\"wizard-title\"]")
    Readable<String> title();

    @XPath("//*[@data-testid=\"legalContactsTitle\"]")
    Readable<String> optionsTitle();

    @XPath("//*[@data-testid=\"legalContactManager\"]//button[normalize-space()=\"Trasferisci su SEND\"]")
    Button transferToSendButton();

    @XPath("//*[@data-testid=\"legalContactManager\"]//button[normalize-space()=\"Personalizza per ente\"]")
    Button customizeBySenderButton();

    // torna alla pagina precedente nella cronologia del browser
    @XPath("//main//button[normalize-space()=\"Indietro\"][not(@data-testid)]")
    Button backButton();

    // contenuto delle opzioni, per sapere se c'è "Trasferisci su SEND" senza attenderlo
    @XPath("//*[@data-testid=\"legalContactManager\"]")
    Readable<String> content();

    @XPath("//*[@data-testid=\"addSpecialContact\"]")
    CustomizeBySenderForm customizeBySenderForm();
    // opzioni della tendina "Tipologia" del form, aperte fuori dal form in fondo alla pagina
    @XPath("//*[@role=\"option\"][@data-value]")
    Readable<String> channelTypeOptions();

    @XPath("//*[@role=\"option\"][@data-value=\"PEC\"]")
    Button pecOption();

    // passi del wizard aperto da "Trasferisci su SEND"
    @XPath("//*[@data-testid=\"desktopWizardStepper\"]//*[starts-with(@data-testid,\"step-\")]")
    Readable<String> transferSteps();

    // labels

    @XPath("//*[@data-testid=\"legalContactManager\"]/div[1]//span")
    Readable<String> status();

    @XPath("//*[@data-testid=\"legalContactManager\"]/div[1]/p")
    Readable<String> pecValue();

    @XPath("//*[@data-testid=\"legalContactManager\"]//button/preceding-sibling::p[2]")
    Readable<String> optionTitles();

    @XPath("//*[@data-testid=\"legalContactManager\"]//button/preceding-sibling::p[1]")
    Readable<String> optionDescriptions();

    /**
     * Form "Personalizza il tuo domicilio digitale per ente mittente": ente, tipologia e, per la tipologia PEC, indirizzo e
     * accettazione. "Conferma" con dati validi salva la PEC per ente.
     */
    interface CustomizeBySenderForm extends Component {
        @XPath("//*[@data-testid=\"specialContactsTitle\"]")
        Readable<String> title();

        @XPath("//form/p[1]")
        Readable<String> description();

        @XPath("//form/p[2]")
        Readable<String> requiredFieldsLabel();

        @XPath("//*[@id=\"sender-label\"]")
        Readable<String> senderLabel();

        @XPath("//*[@id=\"sender\"]")
        TextField senderInput();

        @XPath("//*[@id=\"sender-helper-text\"]")
        Readable<String> senderErrorMessage();

        @XPath("//*[@id=\"channelType-label\"]")
        Readable<String> channelTypeLabel();

        @XPath("//*[@id=\"channelType\"]")
        Button channelTypeSelect();

        @XPath("//*[@id=\"channelType-helper-text\"]")
        Readable<String> channelTypeErrorMessage();

        @XPath("//*[@id=\"s_value-label\"]")
        Readable<String> pecLabel();

        @XPath("//*[@id=\"s_value\"]")
        TextField pecInput();

        @XPath("//*[@id=\"s_value-helper-text\"]")
        Readable<String> pecErrorMessage();

        @XPath("//label[.//*[@id=\"s_disclaimer\"]]")
        Readable<String> disclaimerLabel();

        @XPath("//*[@id=\"s_disclaimer-helper-text\"]")
        Readable<String> disclaimerErrorMessage();

        @XPath("//*[@data-testid=\"prev-button\"]")
        Button backButton();

        @XPath("//*[@data-testid=\"next-button\"]")
        Button confirmButton();

        @Override
        default void assertLoaded() {
            title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
        }
    }

    @Override
    default void assertLoaded() {
        oneTrustBanner().ifPresent(OneTrustBanner::accept);
        title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Gestisci il tuo domicilio digitale"));
        customizeBySenderButton().assertLoaded();
        backButton().assertLoaded();
    }
}
