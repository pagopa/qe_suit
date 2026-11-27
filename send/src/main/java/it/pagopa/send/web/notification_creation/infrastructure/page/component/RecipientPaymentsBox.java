package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.common.legal_notification.domain.ResolvedF24Payment;
import it.pagopa.send.common.legal_notification.domain.ResolvedPagoPaPayment;
import it.pagopa.send.common.legal_notification.domain.ResolvedPayment;

import java.util.List;

/**
 * "Posizione debitoria di ..." di un destinatario: un blocco per avviso pagoPA e uno per modello F24, il primo di
 * ciascun tipo già presente, gli altri aggiunti con "Aggiungi codice di avviso pagoPA" e "Aggiungi un altro Modello F24".
 */
public interface RecipientPaymentsBox extends Component {

    @XPath(".//*[contains(@data-testid, '.pagoPa.')]")
    List<PagoPaPaymentBox> pagoPaPayments();

    @XPath(".//*[@data-testid='add-new-pagopa']")
    Clickable addPagoPaPayment();

    @XPath(".//*[contains(@data-testid, '.f24.')]")
    List<F24PaymentBox> f24Payments();

    @XPath(".//*[@data-testid='add-new-f24']")
    Clickable addF24Payment();

    default void fillFields(List<ResolvedPayment> payments, String pagoPaAttachmentPath, String f24MetadataPath) {
        List<ResolvedPagoPaPayment> pagoPa = payments.stream()
                .filter(ResolvedPagoPaPayment.class::isInstance).map(ResolvedPagoPaPayment.class::cast).toList();
        for (int i = 0; i < pagoPa.size(); i++) {
            if (i > 0) {
                addPagoPaPayment().click();
            }
            pagoPaPayments().get(i).fillFields(pagoPa.get(i), pagoPaAttachmentPath);
        }
        List<ResolvedF24Payment> f24 = payments.stream()
                .filter(ResolvedF24Payment.class::isInstance).map(ResolvedF24Payment.class::cast).toList();
        for (int i = 0; i < f24.size(); i++) {
            if (i > 0) {
                addF24Payment().click();
            }
            f24Payments().get(i).fillFields(f24.get(i), f24MetadataPath);
        }
    }
}
