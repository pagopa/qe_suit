package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.application.context.EntityStore;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementCreationFailureReason;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgreementSteps {
    private final AgreementUseCase agreementUseCase;
    private final CurrentUserSession currentUserSession;
    private final EntityStore entityStore;

    @Given("associa un Agreement in stato DRAFT all'{currentEService}")
    public void createAgreement(EService eService) {
        Agreement agreement = agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor());
        entityStore.upsert(agreement);
    }

    @When("il sistema impedisce a/al {tenant} di inoltrare una richiesta di fruizione per la {currentArchivedEServiceDescriptor} dell'{currentEService}")
    @Given("un {currentEService} creato dal {tenant} con versione v2 attiva e con fruizione attiva di {tenant}")
    public void createEserviceToBeArchivedWithNewVersionAndActiveAgreement(EService eService, Tenant producer, Tenant consumer) {
        throw new UnsupportedOperationException("createEserviceToBeArchivedWithNewVersionAndActiveAgreement Not supported yet.");
    }

    @Given("un {currentEService} in archiviazione creato dal {tenant} con una versione in archiviazione dopo la fruizione di {tenant}")
    public void createEserviceToBeArchivedWithArchivedVersionAndActiveAgreement(EService eService, Tenant producer, Tenant consumer) {
        throw new UnsupportedOperationException("createEserviceToBeArchivedWithActiveAgreement Not supported yet.");
        Agreement agreement = agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor());
        entityStore.upsert(agreement);
    }

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

    @Then("il sistema mostra a {tenant} un banner di informazioni che denota la versione obsoleta dell'{currentEService} con possibilità di aggiornare ad una nuova versione")
    public void consultAgreementPageAndSeeBanner1(Tenant consumer, EService eService){
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        agreementUseCase.shouldSeeBannerAdvisingTheUpdateOfTheAgreement(eService);
    }

    @Then("il sistema mostra a {tenant} un banner di informazioni che denota la versione obsoleta dell'{currentEService}")
    public void consultAgreementPageAndSeeBanner2(Tenant consumer, EService eService){
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        agreementUseCase.shouldSeeBannerAdvisingTheUpdateOfTheAgreement(eService);
    }

    @Then("il sistema mostra a {tenant} un banner di informazioni che denota la versione obsoleta dell'{currentEService} con possibilità di aggiornare ad una nuova versione")
    public void consultAgreementPageAndSeeBanner1(Tenant consumer, EService eService){
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        agreementUseCase.shouldSeeBannerAdvisingTheUpdateOfTheAgreement(eService);
    }

    @Then("il sistema mostra a {tenant} un banner di informazioni che denota la versione obsoleta dell'{currentEService}")
    public void consultAgreementPageAndSeeBanner2(Tenant consumer, EService eService){
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        agreementUseCase.shouldSeeBannerAdvisingTheUpdateOfTheAgreement(eService);
    }

    @Then("il sistema non mostra alcun banner al {tenant}")
    public void consultAgreementPageAndSeeNoBanner(Tenant consumer){
        throw new UnsupportedOperationException("consultAgreementPageAndSeeNoBanner Not supported yet.");
    }


    @Then("il sistema non mostra alcun banner al {tenant}")
    public void consultAgreementPageAndSeeNoBanner(Tenant consumer){
        throw new UnsupportedOperationException("consultAgreementPageAndSeeNoBanner Not supported yet.");
    }

}
