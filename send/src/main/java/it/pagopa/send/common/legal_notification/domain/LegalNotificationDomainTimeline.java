package it.pagopa.send.common.legal_notification.domain;

import it.pagopa.domain.Identifiable;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class LegalNotificationDomainTimeline implements Identifiable {
    String iun;
    String senderTaxId;
    String senderDenomination;
    String subject;
    NotificationStatus status;

    @Builder.Default
    List<NotificationRecipientSummary> recipients = List.of();

    /**
     * {@link Identifiable} richiede un {@link UUID}, ma l'identità naturale di una notifica è lo
     * IUN (stringa): derivato deterministicamente, così lo stesso IUN produce sempre lo stesso id
     * in {@code EntityStore}.
     */
    @Override
    public UUID getId() {
        return UUID.nameUUIDFromBytes(iun.getBytes(StandardCharsets.UTF_8));
    }
}
