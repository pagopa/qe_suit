package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.NotificationDocumentType;
import org.springframework.plugin.core.Plugin;

/**
 * Scarica un documento della notifica indicata (per l'avviso di avvenuta ricezione, quello del primo destinatario) e
 * restituisce il nome del file ottenuto. Fallisce se il documento non si ottiene entro un tempo massimo.
 */
public interface DownloadNotificationDocumentGateway extends Plugin<Channel> {
    String downloadDocument(String iun, NotificationDocumentType type);
}
