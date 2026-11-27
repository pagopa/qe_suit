package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.send.common.legal_notification.domain.FeePolicy;
import it.pagopa.send.common.legal_notification.domain.ResolvedRecipient;

import java.util.List;

/**
 * Passo "Dettaglio posizione debitoria" di "Invia una nuova notifica", presente se almeno un destinatario ha dei
 * pagamenti: costo di notifica, tecnologia del pagamento e una "Posizione debitoria di ..." per ciascuno di quei
 * destinatari, nel loro ordine.
 */
public interface PaymentDetailStep extends Component {

    @XPath(".//label[.//input[@name='notificationFeePolicy' and @value='FLAT_RATE']]")
    RadioButton flatRate();

    // asincrona: la posizione debitoria dei pagamenti è caricata sul GPD di pagoPA
    @XPath(".//label[.//input[@name='pagoPaIntMode' and @value='ASYNC']]")
    RadioButton asyncIntegration();

    @XPath(".//*[contains(@data-testid, '-payments')]")
    List<RecipientPaymentsBox> recipientPayments();

    /** Il costo di notifica puntuale (DELIVERY_MODE) chiede altri dati, non ancora gestiti. */
    default void fillFields(FeePolicy feePolicy, List<ResolvedRecipient> recipientsWithPayments,
                            String pagoPaAttachmentPath, String f24MetadataPath) {
        if (feePolicy != FeePolicy.FLAT_RATE) {
            throw new UnsupportedOperationException("Costo di notifica non ancora supportato sul canale Web: " + feePolicy);
        }
        flatRate().select();
        asyncIntegration().select();
        List<RecipientPaymentsBox> boxes = recipientPayments();
        for (int i = 0; i < recipientsWithPayments.size(); i++) {
            boxes.get(i).fillFields(recipientsWithPayments.get(i).payments(), pagoPaAttachmentPath, f24MetadataPath);
        }
    }
}
