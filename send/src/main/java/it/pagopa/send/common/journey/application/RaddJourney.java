package it.pagopa.send.common.journey.application;

/**
 * Precondizioni RADD lato operatore (JWT statico configurato, nessuna utenza in sessione). Gli esiti restano in
 * {@code EntityStore}: {@code get(RaddUpload.class)} e {@code get(RaddTransaction.class)}.
 * <p>
 * Le start non falliscono se il servizio risponde con uno {@code status.code} diverso da 0 (HTTP 200 con esito di
 * business): la {@code RaddTransaction} salvata lo riporta e il chiamante decide con {@code started()}. Le eccezioni
 * attraverso il proxy di {@link SendJourney} arriverebbero avvolte e non sarebbero distinguibili.
 */
public interface RaddJourney<SELF extends RaddJourney<SELF>> extends JourneyModule {

    /**
     * Upload di un file ZIP valido con un {@code operationId} nuovo.
     */
    SELF withUploadedDocuments();

    /**
     * Upload + start ACT per IUN, ripetuti finche' il servizio risponde NotReady (code 2).
     */
    SELF startActTransaction(String iun, String recipientTaxId);

    /**
     * Upload + start AOR, ripetuti finche' il servizio risponde NotReady (code 2).
     */
    SELF startAorTransaction(String recipientTaxId);
}
