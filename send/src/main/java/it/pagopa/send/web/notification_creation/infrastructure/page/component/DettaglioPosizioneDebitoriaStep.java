package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.send.common.legal_notification.domain.ResolvedF24Payment;
import it.pagopa.send.common.legal_notification.domain.ResolvedPagoPaPayment;
import it.pagopa.send.common.legal_notification.domain.ResolvedPayment;
import it.pagopa.send.common.legal_notification.domain.ResolvedRecipient;

import java.util.List;

/**
 * Passo "Posizione debitoria" di "Invia una nuova notifica" ({@code CreateNotificationPage}): il tipo di pagamento di
 * ciascun destinatario.
 */
public interface DettaglioPosizioneDebitoriaStep extends Component {

    // un gruppo di scelte per ogni destinatario, nell'ordine dei destinatari
    @XPath(".//label[@data-testid='paymentModel'][.//input[@value='PAGO_PA']]")
    List<RadioButton> pagoPaOptions();

    @XPath(".//label[@data-testid='paymentModel'][.//input[@value='F24']]")
    List<RadioButton> f24Options();

    @XPath(".//label[@data-testid='paymentModel'][.//input[@value='PAGO_PA_F24']]")
    List<RadioButton> pagoPaAndF24Options();

    @XPath(".//label[@data-testid='paymentModel'][.//input[@value='NOTHING']]")
    List<RadioButton> noPaymentOptions();

    /** Per ogni destinatario il tipo di pagamento dei suoi pagamenti: avviso pagoPA, modello F24, entrambi o nessuno. */
    default void selectPaymentModels(List<ResolvedRecipient> recipients) {
        for (int i = 0; i < recipients.size(); i++) {
            List<ResolvedPayment> payments = recipients.get(i).payments();
            boolean pagoPa = payments.stream().anyMatch(ResolvedPagoPaPayment.class::isInstance);
            boolean f24 = payments.stream().anyMatch(ResolvedF24Payment.class::isInstance);
            List<RadioButton> options = pagoPa && f24 ? pagoPaAndF24Options()
                    : pagoPa ? pagoPaOptions()
                    : f24 ? f24Options()
                    : noPaymentOptions();
            options.get(i).select();
        }
    }
}
