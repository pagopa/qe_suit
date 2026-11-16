package it.pagopa.send.web.destinatario_pf.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.pagopa.infrastructure.suit.component.Chip;
import it.pagopa.send.web.notification_details.infrastructure.suit.section.NotificationStatusSection;

/**
 * Stato della notifica nella pagina di dettaglio del cittadino ({@code NotificationDetailsPFPage}).
 */
public interface PfNotificationStatusSection extends NotificationStatusSection {
    @Override
    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/aside/div[1]/div/h2")
    Readable<String> header();

    @Override
    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/aside/div[1]/div/div")
    Chip statusChip();

    @Override
    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/aside/div[1]/div/p")
    Readable<String> detailsMessage();

    @Override
    @XPath("//*[@id=\"root\"]/div[1]/div/main/div/div[2]/aside/div[1]/div/button")
    Clickable detailsButton();

}
