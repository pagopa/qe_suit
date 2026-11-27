package it.pagopa.send.common.radd.application;

import it.pagopa.send.common.radd.domain.RaddTransaction;
import it.pagopa.send.common.radd.domain.RaddUpload;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;

/**
 * Casi d'uso RADD lato operatore: upload dei documenti e avvio delle transazioni ACT/AOR. Ogni tentativo usa un
 * {@code operationId} nuovo (uno gia' usato risponde code 5). Se i documenti sono temporaneamente non disponibili
 * (code 2, NotReady) lo spec chiede di riprovare con una nuova transazione: qui si ripete upload + start fino a un
 * esito diverso da NotReady.
 */
@Service
@RequiredArgsConstructor
public class RaddUseCase {

    private static final Duration NOT_READY_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration NOT_READY_INTERVAL = Duration.ofSeconds(10);

    private final RaddGateway raddGateway;

    public RaddUpload uploadDocuments() {
        return raddGateway.uploadDocuments(newOperationId());
    }

    public RaddTransaction startActTransaction(String iun, String recipientTaxId) {
        return startUntilReady(upload -> raddGateway.startActTransaction(upload, iun, recipientTaxId));
    }

    public RaddTransaction startAorTransaction(String recipientTaxId) {
        return startUntilReady(upload -> raddGateway.startAorTransaction(upload, recipientTaxId));
    }

    public String newOperationId() {
        return UUID.randomUUID().toString();
    }

    private RaddTransaction startUntilReady(Function<RaddUpload, RaddTransaction> start) {
        return PollingUtils.pollUntil(
                () -> start.apply(uploadDocuments()),
                transaction -> !transaction.notReady(),
                NOT_READY_TIMEOUT,
                NOT_READY_INTERVAL,
                "Documenti RADD ancora non disponibili (status.code 2) dopo " + NOT_READY_TIMEOUT
        );
    }
}
