package it.pagopa.send.common.legal_notification.application;

import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import org.springframework.plugin.core.Plugin;

/** Aggiunge un destinatario alla notifica legale in preparazione sul proprio canale. */
public interface AddRecipientGateway extends Plugin<Channel> {
    void addRecipient(RecipientSpec recipient);
}
