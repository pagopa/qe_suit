package it.pagopa.interop.common.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.bff.journey.TestJourney;
import it.pagopa.interop.common.contract.model.agreement.AgreementState;
import it.pagopa.interop.common.contract.model.purpose.PurposeVersionState;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceJourneyController {

    private final TestJourney journey;

    @Given("un eservice creato da {tenant} con una richiesta di fruizione associata da {tenant}")
    public void setupEserviceAndAgreement(Tenant producer, Tenant consumer) {
        journey
            .withProducer(producer, User.getTenantAdmin(producer))
            .publishEService()
            .withConsumer(consumer, User.getTenantAdmin(consumer))
            .addAgreement(AgreementState.ACTIVE);
    }

    @Given("un eservice creato da {tenant} con una richiesta di fruizione e una finalità associate da {tenant}")
    public void setupEservice(Tenant producer, Tenant consumer) {
        journey
            .withProducer(producer, User.getTenantAdmin(producer))
            .publishEService()
            .withConsumer(consumer, User.getTenantAdmin(consumer))
            .addAgreement(AgreementState.ACTIVE)
            .createPurpose(PurposeVersionState.ACTIVE);
    }

    @Given("un eservice creato da {tenant} con una richiesta di fruizione e una finalità in stato {purposeState} provenienti da {tenant}")
    public void setupEservice(Tenant producer, PurposeVersionState purposeState, Tenant consumer) {
        journey
            .withProducer(producer, User.getTenantAdmin(producer))
            .publishEService()
            .withConsumer(consumer, User.getTenantAdmin(consumer))
            .addAgreement(AgreementState.ACTIVE)
            .createPurpose(purposeState);
    }
}