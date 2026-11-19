package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.Assertions;

/**
 * Cellulare attivo: valore, "Modifica" e "Disattiva". "Modifica" apre un campo con "Conferma": un valore valido avvia
 * l'invio del codice di verifica, quindi nei test si usano solo valori non validi.
 */
public interface SmsContact extends Component {
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
