package it.pagopa.send.common.informal_notification.domain;

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
public class InformalNotificationDomain implements Identifiable {
    String paProtocolNumber;
    String campaignId;
    String senderDenomination;
    String subject;
    String iun;

    @Builder.Default
    List<String> messageIds = List.of();

    @Override
    public UUID getId() {
        String key = (iun != null && !iun.isBlank()) ? iun : (paProtocolNumber != null ? paProtocolNumber : UUID.randomUUID().toString());
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }
}
