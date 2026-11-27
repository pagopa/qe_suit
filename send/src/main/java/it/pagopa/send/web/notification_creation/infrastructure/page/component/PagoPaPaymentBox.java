package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.suit.component.TextField;
import it.pagopa.send.common.legal_notification.domain.ResolvedPagoPaPayment;

/** Un avviso pagoPA di un destinatario nel passo "Dettaglio posizione debitoria". */
public interface PagoPaPaymentBox extends Component {

    @XPath(".//input[@type='file']")
    Uploadable attachment();

    @XPath(".//*[@data-testid='pagopa-notice-code']//input")
    TextField noticeCode();

    // il portale lo propone con il codice fiscale della PA
    @XPath(".//*[@data-testid='pagopa-creditor-tax-id']//input")
    TextField creditorTaxId();

    default void fillFields(ResolvedPagoPaPayment payment, String attachmentPath) {
        attachment().upload(attachmentPath);
        noticeCode().write(payment.noticeCode());
        creditorTaxId().cleanAndWrite(payment.creditorTaxId());
    }
}
