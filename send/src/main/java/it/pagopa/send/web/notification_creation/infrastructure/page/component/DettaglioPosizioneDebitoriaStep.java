package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.web.notification_creation.application.NotificationData;

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
