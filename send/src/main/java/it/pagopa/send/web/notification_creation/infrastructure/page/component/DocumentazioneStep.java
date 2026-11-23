package it.pagopa.send.web.notification_creation.infrastructure.page.component;

import it.frontend.e2e.framework.annotation.selector.XPath;
import it.frontend.e2e.framework.core.capability.core.Uploadable;
import it.frontend.e2e.framework.web.capability.core.Writable;
import it.frontend.e2e.framework.web.domain.Component;

/**
 * Passo "Documenti allegati" di "Invia una nuova notifica" ({@code CreateNotificationPage}): titolo e file del documento.
 */
public interface DocumentazioneStep extends Component {

    @XPath(".//input[@id='documents.0.name']")
    Writable<String> documentTitle();

    @XPath(".//*[@id=\"file-input\"]")
    Uploadable attachment();

    default void fillFields(String title, String absoluteFilePath) {
        documentTitle().write(title);
        attachment().upload(absoluteFilePath);
    }
}
