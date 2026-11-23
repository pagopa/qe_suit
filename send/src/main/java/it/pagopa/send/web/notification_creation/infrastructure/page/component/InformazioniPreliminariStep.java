package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationRequest;

import java.util.List;
import java.util.function.Predicate;

/**
 * Passo "Informazioni preliminari" di "Invia una nuova notifica" ({@code CreateNotificationPage}).
 */
public interface InformazioniPreliminariStep extends Component {
    @XPath(".//*[@id=\"subject\"]")
    Writable<String> subject();

    @XPath(".//input[@id='abstract']")
    Writable<String> description();

    @XPath(".//input[@id='paProtocolNumber']")
    Writable<String> protocolNumber();

    @XPath(".//input[@id='taxonomyCode']")
    Writable<String> taxonomyCode();

    // tipologia di comunicazione fisica: il valore del radio è il nome della tipologia
    @XPath(".//*[@data-testid='comunicationTypeRadio'][.//input[@value='REGISTERED_LETTER_890']]")
    RadioButton registeredLetter890();

    @XPath(".//*[@data-testid='comunicationTypeRadio'][.//input[@value='AR_REGISTERED_LETTER']]")
    RadioButton registeredLetter();

    // Group combobox — clicks to open the dropdown
    @XPath(".//*[@id='group']")
    Clickable openGroupDropdown();

    // "Gruppo *" quando l'ente ha dei gruppi e il gruppo è obbligatorio, altrimenti "Gruppo"
    @XPath(".//label[@id='group-label']")
    Readable<String> groupLabel();

    // voci della tendina aperta: il menu è fuori dal form
    @XPath("//*[@role='option']")
    List<Button> groupOptions();

    /**
     * Compila il passo con i dati preliminari della request di dominio, compresa la tipologia di comunicazione
     * fisica; il gruppo non è ancora noto (dipende dal mittente): se l'ente ha dei gruppi, e quindi è obbligatorio,
     * si sceglie il primo disponibile.
     */
    default void fillFields(LegalNotificationCreationRequest request) {
        subject().write(request.getSubject());
        description().write(request.getAbstractText());
        protocolNumber().write(request.getPaProtocolNumber());
        taxonomyCode().write(request.getTaxonomyCode());
        switch (request.getPhysicalCommunication()) {
            case REGISTERED_LETTER_890 -> registeredLetter890().select();
            case AR_REGISTERED_LETTER -> registeredLetter().select();
        }
        if (groupLabel().read().endsWith("*")) {
            selectGroup(option -> true);
        }
    }

    default void selectGroup(Predicate<Button> option) {
        openGroupDropdown().click();
        groupOptions().stream()
                .filter(option)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Gruppo non presente nella tendina"))
                .click();
    }
}
