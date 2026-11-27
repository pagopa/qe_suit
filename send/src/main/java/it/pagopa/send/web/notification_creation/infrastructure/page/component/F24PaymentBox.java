package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.common.legal_notification.domain.ResolvedF24Payment;

/** Un modello F24 di un destinatario nel passo "Dettaglio posizione debitoria": titolo e metadati in formato JSON. */
public interface F24PaymentBox extends Component {

    @XPath(".//input[@type='file']")
    Uploadable metadata();

    @XPath(".//*[@data-testid='f24-name']//input")
    TextField title();

    default void fillFields(ResolvedF24Payment payment, String metadataPath) {
        metadata().upload(metadataPath);
        title().write(payment.title());
    }
}
