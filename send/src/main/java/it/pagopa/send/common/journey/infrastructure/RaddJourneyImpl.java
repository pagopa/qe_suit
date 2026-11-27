package it.pagopa.send.common.journey.infrastructure;

import it.pagopa.application.context.EntityStore;
import it.pagopa.send.common.journey.application.RaddJourney;
import it.pagopa.send.common.radd.application.RaddUseCase;
import it.pagopa.send.common.radd.domain.RaddTransaction;
import it.pagopa.send.common.radd.domain.RaddUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Composer fluente per le precondizioni RADD, sul modello di {@link DelegationJourneyImpl}: bean singleton senza
 * stato proprio, gli esiti restano in {@code EntityStore}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RaddJourneyImpl implements RaddJourney<RaddJourneyImpl> {

    private final RaddUseCase raddUseCase;
    private final EntityStore entityStore;

    @Override
    public RaddJourneyImpl withUploadedDocuments() {
        RaddUpload upload = raddUseCase.uploadDocuments();
        entityStore.upsert(upload);
        log.info("Documenti RADD caricati per l'operazione {} (fileKey {})", upload.operationId(), upload.fileKey());
        return this;
    }

    @Override
    public RaddJourneyImpl startActTransaction(String iun, String recipientTaxId) {
        return store(raddUseCase.startActTransaction(iun, recipientTaxId));
    }

    @Override
    public RaddJourneyImpl startAorTransaction(String recipientTaxId) {
        return store(raddUseCase.startAorTransaction(recipientTaxId));
    }

    private RaddJourneyImpl store(RaddTransaction transaction) {
        entityStore.upsert(transaction);
        log.info("Start transaction RADD {} {}: status.code {}",
                transaction.type(), transaction.operationId(), transaction.code());
        return this;
    }
}
