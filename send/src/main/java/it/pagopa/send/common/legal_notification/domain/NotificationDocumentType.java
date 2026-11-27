package it.pagopa.send.common.legal_notification.domain;

/**
 * Tipi di documento di una notifica che la PA mittente scarica, come li classifica SEND nel download dei documenti
 * (il {@code documentType} di {@code /notifications/sent/{iun}/documents/{documentType}}): l'atto notificato e
 * l'avviso di avvenuta ricezione.
 */
public enum NotificationDocumentType {
    ATTACHMENT,
    AAR
}
