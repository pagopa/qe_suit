package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;
    private final CurrentUserSession currentUserSession;

    @Given("associa un Agreement in stato DRAFT all'{currentEService}")
    public void createAgreement(EService eService) {
        agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor());
    }

    @When("il sistema impedisce a/al {tenant} di inoltrare una richiesta di fruizione per la {currentDeprecatedEServiceDescriptor} dell'{currentEService}")
    public void createAgreement(Tenant consumer, EServiceDescriptor eServiceDescriptor, EService eService) {
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        agreementUseCase.shouldFailToCreateAgreement(eService, eServiceDescriptor, AgreementCreationFailureReason.DEPRECATED_VERSION);
    }
}
