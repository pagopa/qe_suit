package it.pagopa.send.common.notification.domain;

/**
 * Identità minima di un destinatario così come restituita dalla lettura di una notifica (es.
 * {@code BffFullNotificationV1.recipients}), agnostica dal DTO OpenAPI.
 */
public record NotificationRecipientSummary(String taxId, String denomination) {
}
