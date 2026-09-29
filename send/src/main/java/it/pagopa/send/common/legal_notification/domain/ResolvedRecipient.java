package it.pagopa.send.common.legal_notification.domain;

import it.pagopa.send.common.user.domain.UserType;
import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;

import java.util.List;

/**
 * Un destinatario dopo la risoluzione dell'identità (taxId/denominazione, eventualmente
 * sovrascritti rispetto al {@code Recipient} censito), dell'indirizzo e dei pagamenti (vedi
 * {@link ResolvedPayment}), pronto per essere tradotto nel DTO OpenAPI del canale scelto.
 */
public record ResolvedRecipient(UserType type, String taxId, String denomination,
                                 NotificationDefaults.PhysicalAddressDefaults physicalAddress,
                                 String digitalDomicile, List<ResolvedPayment> payments) {
}
