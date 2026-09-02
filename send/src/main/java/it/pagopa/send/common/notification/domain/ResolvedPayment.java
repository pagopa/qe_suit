package it.pagopa.send.common.notification.domain;

/**
 * Un avviso di pagamento dopo la risoluzione (posizione debitoria creata, allegato precaricato),
 * pronto per essere tradotto nel DTO OpenAPI del canale scelto. A differenza di {@link
 * it.pagopa.send.model.PaymentSpec} (la richiesta, prima della risoluzione), non ha side-effect
 * residui: i dati reali (notice code, allegato) sono già stati ottenuti.
 */
public sealed interface ResolvedPayment permits ResolvedPagoPaPayment, ResolvedF24Payment {
}
