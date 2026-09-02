package it.pagopa.send.common.notification.domain;

public record ResolvedF24Payment(String title, DocumentRef attachment) implements ResolvedPayment {
}
