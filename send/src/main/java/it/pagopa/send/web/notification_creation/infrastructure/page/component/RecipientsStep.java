package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;

import java.util.List;

/**
 * Passo "Destinatari" di "Invia una nuova notifica" ({@code CreateNotificationPage}): un blocco per destinatario.
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
}
