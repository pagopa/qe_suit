package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.RadioButton;
import it.pagopa.send.common.legal_notification.domain.ResolvedF24Payment;
import it.pagopa.send.common.legal_notification.domain.ResolvedPagoPaPayment;
import it.pagopa.send.common.legal_notification.domain.ResolvedPayment;
import it.pagopa.send.common.legal_notification.domain.ResolvedRecipient;
import it.pagopa.send.web.notification_creation.application.NotificationData;

import java.util.List;

/**
 * Passo del wizard "Invia una nuova notifica" usato dagli step Cucumber ({@code CreateNotificationPage}).
 */
public interface DettaglioPosizioneDebitoriaStep extends Component {

    @XPath(".//label[@data-testid='paymentModel'][1]")
    Clickable selectPagoPa();

    @XPath(".//label[@data-testid='paymentModel'][2]")
    Clickable selectF24();

    @XPath(".//label[@data-testid='paymentModel'][3]")
    Clickable selectPagoPaAndF24();

    @XPath(".//label[@data-testid='paymentModel'][4]")
    Clickable selectNoPayment();

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

    default void selectPaymentTypeAndSubmit(String paymentType) {
        switch (paymentType) {
            case "PAGO_PA"       -> selectPagoPa().click();
            case "F24"           -> selectF24().click();
            case "PAGO_PA_F24"   -> selectPagoPaAndF24().click();
            case "NOTHING"       -> selectNoPayment().click();
            default -> throw new IllegalArgumentException(
                    "Unknown payment type: " + paymentType
            );
        }
    }

    default void fillFields(NotificationData data) {
        selectPaymentTypeAndSubmit(data.getPaymentType());
    }
}
