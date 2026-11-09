package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;
    private final CurrentUserSession currentUserSession;
    private final InteropJourney interopJourney;

    @Given("associa un Agreement in stato DRAFT all'{currentEService}")
    public void createAgreement(EService eService) {
        agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor());
    }

    @Given("{eServiceByAlias} del/di {tenant} sottoscritto da/dal {tenant} con (una )richiesta di fruizione (in ){agreementState}")
    public void createAgreement(EService eService, Tenant producer, Tenant consumer, AgreementState targetState) {
        interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .linkAgreement(eService, targetState);
    }

    @When("il sistema impedisce a/al {tenant} di inoltrare una richiesta di fruizione per la {currentArchivedEServiceDescriptor} dell'{currentEService}")
    @When("il sistema impedisce a/al {tenant} di inoltrare una richiesta di fruizione per la {currentDeprecatedEServiceDescriptor} dell'{currentEService}")
    public void createAgreement(Tenant consumer, EServiceDescriptor eServiceDescriptor, EService eService) {
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        AgreementCreationFailureReason reason = switch (eServiceDescriptor.getState()){
            case DRAFT,SUSPENDED,ARCHIVING_SUSPENDED, WAITING_FOR_APPROVAL -> AgreementCreationFailureReason.ESERVICE_INVALID_STATE;
            case DEPRECATED -> AgreementCreationFailureReason.DEPRECATED_VERSION;
            case ARCHIVED -> AgreementCreationFailureReason.ARCHIVED_STATE;
            default -> throw new IllegalArgumentException("Status not among the ones to be handled");
        };
        agreementUseCase.shouldFailToCreateAgreement(eService, eServiceDescriptor, reason);
    }
}
