package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.AgreementRef;
import it.pagopa.interop.common.kernel.domain.Tenant;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Gestisce gli step relativi ad agreement associati a descrittori o e-service in stato ARCHIVED.
 * Queste condizioni sono predisposte dal team di sviluppo tramite data preparation,
 * poiche' al momento il team QA non puo' riprodurle autonomamente.
 */
@RequiredArgsConstructor
public class AgreementArchivedDescriptorSteps {
    // Fixture QA predisposta dalla data preparation: versione 1 archiviata, versione 2 pubblicata.
    //E-service name : "vapt-datapreparation TC-02"
    private static final UUID AGREEMENT_ID = UUID.fromString("ef49786e-59ec-4ddf-81a1-2b789ea600e1");

    private final AgreementUseCase agreementUseCase;
    private final AuthSession session;

    @Given("la richiesta di fruizione in stato sospesa di {tenant} legata alla prima versione archiviata dell'e-service")
    public void suspendAgreementForArchivedDescriptor(Tenant consumer) {
        session.set(consumer);
        Agreement agreement = agreementUseCase.getAgreement(AgreementRef.of(AGREEMENT_ID));
        Agreement suspended = agreementUseCase.suspendAgreement(agreement, null);
        agreementUseCase.awaitState(suspended, Set.of(AgreementState.SUSPENDED));
    }
}
