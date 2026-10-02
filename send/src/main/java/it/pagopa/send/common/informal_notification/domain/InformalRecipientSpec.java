package it.pagopa.send.common.informal_notification.domain;

import it.pagopa.send.common.legal_notification.domain.NotificationDefaults;
import it.pagopa.send.common.user.domain.Recipient;

import java.util.List;

public record InformalRecipientSpec(
        Recipient recipient,
        String taxIdOverride,
        String denominationOverride,
        InformalRecipientType recipientType,
        String email,
        String phoneNumber,
        String additionalLanguage,
        NotificationDefaults.PhysicalAddressDefaults physicalAddress,
        InformalMessageSpec messageSpec,
        List<InformalPagoPaPaymentSpec> payments
) {}
