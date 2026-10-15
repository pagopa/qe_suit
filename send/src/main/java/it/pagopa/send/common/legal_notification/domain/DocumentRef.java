package it.pagopa.send.common.legal_notification.domain;

/**
 * Riferimento a un allegato già precaricato (vedi {@code DocumentPreloadService}), nella forma
 * richiesta sia dal documento principale della notifica sia dagli allegati di pagamento
 * (pagoPA/F24). Puramente di dominio: nessun riferimento al DTO OpenAPI.
 */
public record DocumentRef(String key, String versionToken, String sha256, String contentType) {
}
