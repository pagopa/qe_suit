package it.pagopa.send.common.legal_notification.domain;

/**
 * Equivalente interno di {@code NotificationFeePolicy} (DTO OpenAPI): tiene la request di
 * creazione notifica ({@link LegalNotificationCreationRequest}) libera da riferimenti al DTO
 * esterno.
 */
public enum FeePolicy {
    FLAT_RATE,
    DELIVERY_MODE
}
