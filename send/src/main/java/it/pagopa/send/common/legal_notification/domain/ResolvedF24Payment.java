package it.pagopa.send.common.legal_notification.domain;

public record ResolvedF24Payment(String title, DocumentRef attachment) implements ResolvedPayment {
}
