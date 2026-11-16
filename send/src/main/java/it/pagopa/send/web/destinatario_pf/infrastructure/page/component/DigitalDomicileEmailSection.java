package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.Assertions;

/**
 * Secondo passo del wizard "Attiva domicilio digitale su SEND": email e cellulare per gli avvisi. Se l'utente li ha già,
 * il passo mostra i recapiti con "Modifica", altrimenti il campo per aggiungere l'email e il pulsante per aggiungere il
 * cellulare; senza email "Continua" non porta al riepilogo. "Modifica", "Aggiungi email" e "Aggiungi numero" con un
 * valore valido avviano l'invio del codice di verifica, quindi nei test si usano solo valori non validi.
 */
public interface DigitalDomicileEmailSection extends Component {
    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]")
    Readable<String> content();

    // recapiti da aggiungere

    @XPath("//*[@id=\"default_email-label\"]")
    Readable<String> emailInputLabel();

    @XPath("//*[@id=\"default_email\"]")
    TextField emailInput();

    @XPath("//*[@id=\"default_email-button\"]")
    Button addEmailButton();

    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]//button[normalize-space()=\"Aggiungi numero di cellulare\"]")
    Button addSmsButton();

    @XPath("//*[@id=\"default_sms\"]")
    TextField smsInput();

    @XPath("//*[@id=\"default_sms-button\"]")
    Button addSmsSaveButton();

    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]//button[normalize-space()=\"Aggiungi numero di cellulare\"]/preceding-sibling::p[1]")
    Readable<String> smsQuestion();

    // recapiti già attivi

    @XPath("//*[@id=\"default_email-custom-label\"]")
    Readable<String> emailLabel();

    @XPath("//*[@id=\"default_email-typography\"]")
    Readable<String> emailValue();

    @XPath("//*[@id=\"modifyContact-default_email\"]")
    Button modifyEmailButton();

    @XPath("//*[@data-testid=\"default_emailContact\"]//input")
    TextField editEmailInput();

    @XPath("//*[@id=\"saveContact-default_email\"]")
    Button saveEmailButton();

    @XPath("//*[@id=\"default_email-helper-text\"]")
    Readable<String> emailErrorMessage();

    @XPath("//*[@id=\"default_sms-custom-label\"]")
    Readable<String> smsLabel();

    @XPath("//*[@id=\"default_sms-typography\"]")
    Readable<String> smsValue();

    @XPath("//*[@id=\"modifyContact-default_sms\"]")
    Button modifySmsButton();

    @XPath("//*[@data-testid=\"default_smsContact\"]//input")
    TextField editSmsInput();

    @XPath("//*[@id=\"saveContact-default_sms\"]")
    Button saveSmsButton();

    @XPath("//*[@id=\"default_sms-helper-text\"]")
    Readable<String> smsErrorMessage();

    @XPath("//*[@data-testid=\"prev-button\"]")
    Button backButton();

    @XPath("//*[@data-testid=\"prev-button\"]/following-sibling::button")
    Button continueButton();

    // labels

    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]/p[1]")
    Readable<String> title();

    @XPath("//*[@data-testid=\"emailSmsContactWizard\"]/p[2]")
    Readable<String> description();

    @Override
    default void assertLoaded() {
        title().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
    }
}
