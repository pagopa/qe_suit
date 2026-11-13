package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.Assertions;

/**
 * Email attiva: valore, "Modifica" e "Disattiva". "Modifica" apre un campo con "Conferma": un valore valido avvia
 * l'invio del codice di verifica, quindi nei test si usano solo valori non validi.
 */
public interface EmailContact extends Component {
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
