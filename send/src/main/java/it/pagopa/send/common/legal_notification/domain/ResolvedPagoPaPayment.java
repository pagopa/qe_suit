package it.pagopa.send.common.legal_notification.domain;

public record ResolvedPagoPaPayment(String noticeCode, String creditorTaxId, DocumentRef attachment) implements ResolvedPayment {
}
