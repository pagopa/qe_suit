package it.pagopa.send.common.legal_notification.domain;

import it.pagopa.send.common.user.domain.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Tutto quello che serve per creare una notifica legale: il mittente, i soli campi della notifica da valorizzare e
 * i destinatari già risolti. Si compone con setter fluenti; i campi non impostati restano ai valori di default.
 * Trasporta solo i dati: la creazione la esegue il Journey.
 */
@Getter
public class LegalNotificationCreationCommand {

    private User sender;
    private String subject;
    private PhysicalCommunicationType physicalCommunication;
    private FeePolicy feePolicy;
    private final List<RecipientSpec> recipients = new ArrayList<>();

    public LegalNotificationCreationCommand sender(User sender) {
        this.sender = sender;
        return this;
    }

    public LegalNotificationCreationCommand subject(String subject) {
        this.subject = subject;
        return this;
    }

    public LegalNotificationCreationCommand physicalCommunication(PhysicalCommunicationType physicalCommunication) {
        this.physicalCommunication = physicalCommunication;
        return this;
    }

    public LegalNotificationCreationCommand feePolicy(FeePolicy feePolicy) {
        this.feePolicy = feePolicy;
        return this;
    }

    /** Aggiunge un destinatario; ripetibile, nell'ordine di chiamata. */
    public LegalNotificationCreationCommand recipient(RecipientSpec recipient) {
        this.recipients.add(recipient);
        return this;
    }
}
