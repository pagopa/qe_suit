package it.pagopa.send.common.radd.domain;

import it.pagopa.domain.Identifiable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Transazione RADD (ACT o AOR) richiesta con una start transaction. Le API RADD rispondono HTTP 200 anche agli
 * esiti di business: lo stato reale e' {@code status.code} della risposta.
 *
 * @param operationId  identificativo dell'operazione, scelto dal chiamante
 * @param type         caso d'uso
 * @param code         {@code status.code} della start: 0 OK, 2 NotReady, 3 stampa gia' eseguita, 4 documenti non
 *                     disponibili, 5 operationId gia' presente, 10 input non valido, 80 notifica annullata, 99 KO
 * @param retryAfterMs per {@code code} 2, attesa suggerita prima di una nuova transazione
 */
public record RaddTransaction(String operationId, RaddOperationType type, int code, Long retryAfterMs)
        implements Identifiable {

    public static final int OK = 0;
    public static final int NOT_READY = 2;
    public static final int ALREADY_PRINTED = 3;

    public boolean started() {
        return code == OK;
    }

    public boolean notReady() {
        return code == NOT_READY;
    }

    public boolean alreadyPrinted() {
        return code == ALREADY_PRINTED;
    }

    @Override
    public UUID getId() {
        return UUID.nameUUIDFromBytes(operationId.getBytes(StandardCharsets.UTF_8));
    }
}
