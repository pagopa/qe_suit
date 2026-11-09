package it.pagopa.send.web.notification_details.infrastructure.page.section;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.PaymentSection;
import org.assertj.core.api.Assertions;
import java.util.List;

/**
 * Sezione "Pagamenti" del dettaglio della PA, mostrata solo per le notifiche con pagamenti: codice avviso e un elemento
 * per ogni pagamento.
 */
public interface MittentePaymentSection extends PaymentSection {
    //        @XPath("header notice code")
    @XPath("//h2[normalize-space(text())=\"Pagamenti\"]")
    Readable<String> noticeCodeHeader();

    //        @XPath("notice code value")
    @XPath("//div[@data-testid=\"paymentInfoBox\"]/p")
    Readable<String> noticeCodeValue();

    @XPath("//div[@data-testid=\"payment-item\"]")
    List<Button> paymentListButtons();

    @Override
    default void assertLoaded() {
        noticeCodeHeader().readAndAssert((h) -> {
            Assertions.assertThat(h).isNotNull();
        });
        noticeCodeValue().readAndAssert((v) -> {
            Assertions.assertThat(v).isNotNull();
        });

    }
}
