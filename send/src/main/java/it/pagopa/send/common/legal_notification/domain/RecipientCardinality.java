package it.pagopa.send.common.legal_notification.domain;

/** Quanti destinatari generare per una notifica: uno solo o tutti quelli disponibili per il tipo indicato. */
public enum RecipientCardinality {
    SINGLE,
    MULTI
}
