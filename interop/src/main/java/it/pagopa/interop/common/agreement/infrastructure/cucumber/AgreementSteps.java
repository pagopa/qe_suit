package it.pagopa.interop.common.agreement.infrastructure.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import it.pagopa.interop.common.agreement.application.AgreementUseCase;
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

    @Given("associa un Agreement in stato DRAFT all'{currentEService}")
    public void createAgreement(EService eService) {
        agreementUseCase.createAgreement(eService, eService.getLastDraftDescriptor());
    }

    @When("il sistema impedisce a/al {tenant} di inoltrare una richiesta di fruizione per la {currentDeprecatedEServiceDescriptor} dell'{currentEService}")
    public void createAgreement(Tenant consumer, EServiceDescriptor eServiceDescriptor, EService eService) {
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        agreementUseCase.shouldFailToCreateAgreement(eService, eServiceDescriptor, AgreementCreationFailureReason.DEPRECATED_VERSION);
    }

    @Then("il sistema mostra a {tenant} un banner di informazioni che denota la versione obsoleta dell'{currentEService} con possibilità di aggiornare ad una nuova versione")
    public void consultAgreementPageAndSeeBanner1(Tenant consumer, EService eService){
        currentUserSession.set(User.getTenantAdmin(consumer), consumer);
        agreementUseCase.shouldSeeBannerAdvisingTheUpdateOfTheAgreement(eService);
    }

    @Then("il sistema mostra a {tenant} un banner di informazioni che denota la versione obsoleta dell'{currentEService}")
    public void consultAgreementPageAndSeeBanner2(Tenant consumer, EService eService){
        throw new UnsupportedOperationException("consultAgreementPageAndSeeBanner2 Not supported yet.");
    }

    @Then("il sistema non mostra alcun banner al {tenant}")
    public void consultAgreementPageAndSeeNoBanner(Tenant consumer){
        throw new UnsupportedOperationException("consultAgreementPageAndSeeNoBanner Not supported yet.");
    }

}
