package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import org.springframework.plugin.core.Plugin;

import java.util.Map;

/**
 * Cerca tra le notifiche della PA con i filtri indicati finché compare quella con lo IUN dato: una notifica appena
 * accettata entra tra le notifiche della PA con qualche secondo di ritardo. Fallisce se non compare entro un tempo
 * massimo.
 */
public interface FindNotificationGateway extends Plugin<Channel> {
    LegalNotificationDomain findNotification(String iun, Map<String, String> filters);
}
