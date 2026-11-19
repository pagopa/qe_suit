package it.pagopa.send.common.legal_notification.infrastructure.factory;

import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.RecipientCardinality;
import it.pagopa.send.common.legal_notification.domain.RecipientSpec;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Costruisce il {@link LegalNotificationCreationCommand} con destinatari indicati dallo scenario oppure generati per tipo
 * e cardinalità; ogni destinatario si costruisce con {@link RecipientSpecFactory}, come negli step che lo aggiungono.
 */
@Component
@RequiredArgsConstructor
public class LegalNotificationCreationCommandFactory {

    private final RecipientSpecFactory recipientSpecFactory;
    private final DefaultRecipientSelector defaultRecipientSelector;

    /** Destinatari indicati dallo scenario, ciascuno con i suoi override, nell'ordine della mappa. */
    public LegalNotificationCreationCommand forExplicitRecipients(User sender, Map<String, String> data,
                                                                  Map<User, Map<String, String>> recipientOverrides) {
        List<RecipientSpec> recipients = recipientOverrides.entrySet().stream()
                .map(entry -> recipientSpecFactory.build(entry.getKey(), entry.getValue()))
                .toList();
        return new LegalNotificationCreationCommand(sender, data, recipients);
    }

    /** Destinatari generati per tipo e cardinalità, con i valori di default di {@link RecipientSpecFactory}. */
    public LegalNotificationCreationCommand forGeneratedRecipients(User sender, Map<String, String> data,
                                                                   UserType type, RecipientCardinality cardinality) {
        List<RecipientSpec> recipients = defaultRecipientSelector.select(type, cardinality).stream()
                .map(recipient -> recipientSpecFactory.build(recipient, Map.of()))
                .toList();
        return new LegalNotificationCreationCommand(sender, data, recipients);
    }
}
