package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import org.springframework.plugin.core.Plugin;

/**
 * Predispone una nuova notifica legale sul proprio canale: campi e destinatari del command, il resto resta ai valori
 * di default.
 */
public interface PrepareNotificationGateway extends Plugin<Channel> {
    void prepareNotification(LegalNotificationCreationCommand command);
}
