package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationDomain;
import org.springframework.plugin.core.Plugin;

import java.util.List;
import java.util.Map;

/** Cerca tra le notifiche legali con i filtri del canale. */
public interface SearchNotificationGateway extends Plugin<Channel> {
    List<LegalNotificationDomain> searchNotification(Map<String, String> filters);
}
