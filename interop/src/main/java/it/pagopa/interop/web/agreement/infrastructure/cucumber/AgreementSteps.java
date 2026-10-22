package it.pagopa.interop.web.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Then;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;
    private final CurrentUserSession currentUserSession;

    @Then("il sistema impedisce l'accesso a {tenant} per la pagina indicata del {tenant} per la fruizione del {currentEService}")
    public void blockUnauthorizedAccessToErogazioneSection(Tenant consumer, Tenant producer, EService eService) {
        if(consumer.getName().equals(producer.getName())){
            throw new IllegalArgumentException("Consumer and Producer are the same; for this test they must be different");
        }

        currentUserSession.set(User.getTenantAdmin(producer), producer);
        String agreementRequestId = agreementUseCase.accessToErogazioneSection(eService);
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        agreementUseCase.blockUnauthorizedAccessToErogazioneSection(agreementRequestId);
    }
}
