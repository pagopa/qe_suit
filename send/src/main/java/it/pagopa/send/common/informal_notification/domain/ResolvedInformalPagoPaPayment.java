package it.pagopa.send.common.informal_notification.domain;

import it.pagopa.send.common.legal_notification.domain.DocumentRef;

public record ResolvedInformalPagoPaPayment(
        String noticeCode,
        String creditorTaxId,
        long amount,
        String dueDate,
        DocumentRef attachment
) {}
