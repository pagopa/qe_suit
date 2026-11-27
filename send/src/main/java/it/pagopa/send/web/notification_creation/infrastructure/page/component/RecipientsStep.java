package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.web.notification_creation.application.NotificationData;

import java.util.List;

/**
 * Passo del wizard "Invia una nuova notifica" usato dagli step Cucumber ({@code CreateNotificationPage}).
 */
public interface RecipientsStep extends Component {

    // un blocco per destinatario: il primo è già presente, gli altri si aggiungono con "Aggiungi destinatario"
    @XPath(".//*[@data-testid='RecipientFormBox']")
    List<RecipientFormBox> recipientBoxes();

    @XPath(".//*[@data-testid='add-recipient']")
    Clickable addRecipientButton();

    /** L'ultimo blocco se è ancora vuoto, altrimenti un nuovo blocco aggiunto con "Aggiungi destinatario". */
    default RecipientFormBox nextEmptyRecipientBox() {
        List<RecipientFormBox> boxes = recipientBoxes();
        RecipientFormBox last = boxes.get(boxes.size() - 1);
        if (last.isEmpty()) {
            return last;
        }
        addRecipientButton().click();
        List<RecipientFormBox> updated = recipientBoxes();
        return updated.get(updated.size() - 1);
    }

    // Recipient type
    @XPath(".//*[@id='recipient-pf']")
    Clickable selectNaturalPerson();

    @XPath(".//*[@id='recipient-pg']")
    Clickable selectLegalPerson();

    // Main fields
    @XPath(".//input[@id='recipients[0].taxId']")
    Writable<String> taxId();

    @XPath(".//input[@id='recipients[0].firstName']")
    Writable<String> firstName();

    @XPath(".//input[@id='recipients[0].lastName']")
    Writable<String> lastName();

    // Address lookup
    @XPath(".//label[@data-testid='physicalAddressLookupRadio.0'][1]")
    Clickable selectNationalRegistry();

    @XPath(".//label[@data-testid='physicalAddressLookupRadio.0'][2]")
    Clickable selectManualAddress();

    // PEC — optional
    @XPath(".//input[@id='recipients[0].digitalDomicile']")
    Writable<String> pecAddress();

    default void fillFields(NotificationData data) {
        selectNaturalPerson().click();
        taxId().write(data.getTaxId());
        firstName().write(data.getFirstName());
        lastName().write(data.getLastName());
        selectNationalRegistry().click();
        if (data.getPec() != null && !data.getPec().isEmpty()) {
            pecAddress().write(data.getPec());
        }
    }
}
