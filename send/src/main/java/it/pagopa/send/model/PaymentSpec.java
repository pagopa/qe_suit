package it.pagopa.send.model;

/**
 * Descrive un avviso di pagamento richiesto per un destinatario, prima che venga effettivamente
 * costruito (e la relativa posizione debitoria creata) da
 * {@link it.pagopa.send.utils.factory.LegalNotificationRequestFactory}. Sealed per rendere
 * l'aggiunta di una nuova tipologia di pagamento un cambiamento a compilazione (nuovo record +
 * nuovo case nello switch della factory), non una modifica silenziosa.
 */
public sealed interface PaymentSpec permits PagoPaPaymentSpec, F24PaymentSpec {
}
