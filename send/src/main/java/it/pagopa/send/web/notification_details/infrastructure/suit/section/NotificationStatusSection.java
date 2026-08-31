package it.pagopa.send.web.notification_details.infrastructure.suit.section;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Clickable;
import it.frontend.e2e.framework.web.capability.core.Readable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.web.infrastructure.suit.component.Chip;

/**
 * Contratto condiviso tra le varianti attore per la sezione di stato della notifica.
 * Gli @XPath qui sono placeholder: questa interfaccia non viene mai bindata direttamente,
 * ogni attore li ridefinisce con quelli reali nella propria interfaccia annidata, così il
 * chiamante può leggere lo stato senza conoscere l'attore corrente.
 */
public interface NotificationStatusSection extends Component {

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> header();


    Chip statusChip();

    @XPath("//*[@id=\"title-of-page\"]")
    Readable<String> detailsMessage();

    @XPath("//*[@id=\"title-of-page\"]")
    Clickable detailsButton();

}
