package it.pagopa.send.common.notification.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class LegalNotificationDomain {
    String iun;
    String senderTaxId;
    String senderDenomination;
    String subject;
    NotificationStatus status;

    @Builder.Default
    List<NotificationRecipientSummary> recipients = List.of();
}
