package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.TextField;
import org.assertj.core.api.Assertions;

/**
 * Domicilio digitale attivo su PEC: valore, "Modifica", "Gestisci" e "Disattiva", con l'avviso sull'indirizzo
 * principale. "Modifica" apre un campo con "Conferma": un valore valido avvia la verifica della PEC, quindi nei test si
 * usano solo valori non validi.
 */
public interface PecContact extends Component {
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
