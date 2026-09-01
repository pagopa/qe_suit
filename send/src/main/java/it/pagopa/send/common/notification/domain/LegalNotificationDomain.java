package it.pagopa.send.common.notification.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class LegalNotificationDomain {
    String iun;
    String sender;
    String recipient;
    String status;
}
