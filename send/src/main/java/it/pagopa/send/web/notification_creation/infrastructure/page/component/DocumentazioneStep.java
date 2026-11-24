package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.send.web.notification_creation.application.NotificationData;

/**
 * Passo del wizard "Invia una nuova notifica" usato dagli step Cucumber ({@code CreateNotificationPage}).
 */
public interface DocumentazioneStep extends Component {

    @XPath(".//input[@id='documents.0.name']")
    Writable<String> documentTitle();

    @XPath(".//*[@id=\"file-input\"]")
    Uploadable attachment();

    default void fillFields(NotificationData data) {
        documentTitle().write(data.getDocumentTitle());
        attachment().upload(data.getDocumentFilePath());
    }
}
