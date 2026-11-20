package it.pagopa.send.common.legal_notification.infrastructure.factory;

import it.pagopa.send.common.legal_notification.domain.FeePolicy;
import it.pagopa.send.common.legal_notification.domain.LegalNotificationCreationCommand;
import it.pagopa.send.common.legal_notification.domain.PhysicalCommunicationType;
import it.pagopa.send.common.legal_notification.domain.RecipientCardinality;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Costruisce il {@link LegalNotificationCreationCommand} con destinatari indicati dallo scenario oppure generati per tipo
 * e cardinalità; ogni destinatario si costruisce con {@link RecipientSpecFactory}, come negli step che lo aggiungono.
 * Gli altri campi della notifica si aggiungono sul command restituito.
 */
@Component
@RequiredArgsConstructor
public class LegalNotificationCreationCommandFactory {

    private final RecipientSpecFactory recipientSpecFactory;
    private final DefaultRecipientSelector defaultRecipientSelector;

    /** Destinatari indicati dallo scenario, ciascuno con i suoi override, nell'ordine della mappa. */
    public LegalNotificationCreationCommand forExplicitRecipients(User sender, Map<User, Map<String, String>> recipientOverrides) {
        LegalNotificationCreationCommand command = new LegalNotificationCreationCommand().sender(sender);
        recipientOverrides.forEach((recipient, overrides) -> command.recipient(recipientSpecFactory.build(recipient, overrides)));
        return command;
    }

    /** Destinatari generati per tipo e cardinalità, con i valori di default di {@link RecipientSpecFactory}. */
    public LegalNotificationCreationCommand forGeneratedRecipients(User sender, UserType type, RecipientCardinality cardinality) {
        LegalNotificationCreationCommand command = new LegalNotificationCreationCommand().sender(sender);
        defaultRecipientSelector.select(type, cardinality)
                .forEach(recipient -> command.recipient(recipientSpecFactory.build(recipient, Map.of())));
        return command;
    }

    /**
     * Command senza destinatari dai dati di una tabella Gherkin: sono ammesse solo le chiavi {@code subject},
     * {@code physicalCommunication} e {@code feePolicy}, le altre sono un errore dello scenario.
     */
    public LegalNotificationCreationCommand fromNotificationData(User sender, Map<String, String> data) {
        LegalNotificationCreationCommand command = new LegalNotificationCreationCommand().sender(sender);
        data.forEach((key, value) -> {
            switch (key) {
                case "subject" -> command.subject(value);
                case "physicalCommunication" -> command.physicalCommunication(PhysicalCommunicationType.valueOf(value));
                case "feePolicy" -> command.feePolicy(FeePolicy.valueOf(value));
                default -> throw new IllegalArgumentException("Dato della notifica non riconosciuto: " + key);
            }
        });
        return command;
    }
}
