package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.Assertions;

/**
 * Email da aggiungere: il campo con "Aggiungi email" e, sotto, il pulsante per aggiungere anche il cellulare, che apre
 * un campo con "Aggiungi numero" e "Annulla". Un valore valido avvia l'invio del codice di verifica, quindi nei test si
 * usano solo valori non validi.
 */
public interface EmailToAdd extends Component {
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
