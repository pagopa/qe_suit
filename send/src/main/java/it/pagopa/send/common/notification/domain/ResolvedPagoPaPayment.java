package it.pagopa.send.common.notification.domain;

public record ResolvedPagoPaPayment(String noticeCode, String creditorTaxId, DocumentRef attachment) implements ResolvedPayment {
}
