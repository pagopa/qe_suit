package it.pagopa.send.common.notification.domain;

/**
 * Equivalente interno di {@code BffNewNotificationRequest.PagoPaIntModeEnum} (DTO OpenAPI): tiene
 * la request di creazione notifica ({@link LegalNotificationCreationRequest}) libera da
 * riferimenti al DTO esterno.
 */
public enum PagoPaIntMode {
    NONE,
    SYNC,
    ASYNC
}
