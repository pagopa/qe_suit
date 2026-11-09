package it.pagopa.send.common.informal_notification.domain;

import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;

import java.util.List;

public record ResolvedInformalRecipient(
        InformalRecipientType recipientType,
        String taxId,
        String denomination,
        String messageId,
        String email,
        String phoneNumber,
        String additionalLanguage,
        NotificationDefaults.PhysicalAddressDefaults physicalAddress,
        List<ResolvedInformalPagoPaPayment> payments
) {}
