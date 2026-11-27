package it.pagopa.send.common.radd.application;

import it.pagopa.send.common.radd.domain.RaddTransaction;
import it.pagopa.send.common.radd.domain.RaddUpload;

/**
 * Operazioni RADD usate come precondizione (upload documenti e avvio transazioni), con l'operatore RADD configurato.
 * Le start restituiscono la {@link RaddTransaction} con lo {@code status.code} ricevuto, senza interpretarlo.
 */
public interface RaddGateway {

    /**
     * {@code documentUpload} + PUT del file ZIP sulla presigned url.
     */
    RaddUpload uploadDocuments(String operationId);

    RaddTransaction startActTransaction(RaddUpload upload, String iun, String recipientTaxId);

    RaddTransaction startAorTransaction(RaddUpload upload, String recipientTaxId);
}
