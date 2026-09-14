package it.pagopa.send.common.legal_notification.domain;

/**
 * Stato di una notifica legale, agnostico dal DTO OpenAPI del canale (equivalente interno di
 * {@code BffNotificationStatus}). Le interfacce dei gateway espongono solo questo tipo; solo le
 * implementazioni dei gateway conoscono il DTO OpenAPI corrispondente.
 */
public enum NotificationStatus {
    IN_VALIDATION,
    ACCEPTED,
    DELIVERING,
    DELIVERED,
    VIEWED,
    EFFECTIVE_DATE,
    PAID,
    UNREACHABLE,
    CANCELLED,
    REFUSED,
    CANCELLATION_IN_PROGRESS,
    RETURNED_TO_SENDER,
    NOTIFICATION_TIMELINE_REWORKED;

    public static NotificationStatus fromValue(String value) {
        return valueOf(value);
    }
}
