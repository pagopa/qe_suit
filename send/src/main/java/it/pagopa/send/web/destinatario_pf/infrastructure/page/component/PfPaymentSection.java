package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.PaymentSection;

/**
 * Pagamenti della notifica nella pagina di dettaglio del cittadino ({@code NotificationDetailsPFPage}).
 */
public interface PfPaymentSection extends PaymentSection {
    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[2]/div[2]/div/h2")
    Readable<String> header();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[2]/div[2]/div/div[1]/div[2]/div/div")
    Readable<String> costMessage();

    @XPath("//*[@id=\"paymentPagoPa-302040124464100004\"]/div[1]/div[1]/span[2]")
    Readable<String> noticeCode();

    @XPath("//*[@id=\"paymentPagoPa-302040124464100004\"]/div[1]/div[2]/span[2]")
    Readable<String> expiredDate();

    @XPath("//*[@id=\"paymentPagoPa-302040124464100004\"]/div[2]/div/p")
    Readable<String> amount();

    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/div/div[2]/div[2]/div/button")
    Clickable payButton();

    @XPath("//*[@id=\"item\"]")
    Clickable downloadButton();
}
