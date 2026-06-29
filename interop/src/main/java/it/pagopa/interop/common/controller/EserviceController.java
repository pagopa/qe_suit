package it.pagopa.interop.common.controller;

import io.cucumber.java.en.Given;
import it.pagopa.interop.bff.journey.Journey;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EserviceController {

    private final Journey journey;

    @Given("un eservice creato da {tenant} con una richiesta di fruizione associata da {tenant}")
    public void setupEserviceAndAgreement(Tenant producer, Tenant consumer) {
        journey
                .withProducer(producer, User.getTenantAdmin(producer))
                .publishEservice()
                .withConsumer(consumer, User.getTenantAdmin(consumer))
                .addActiveAgreement();
    }

    @Given("un eservice creato da {tenant} con una richiesta di fruizione e una finalità associate da {tenant}")
    public void setupEservice(Tenant producer, Tenant consumer) {
        journey
                .withProducer(producer, User.getTenantAdmin(producer))
                .publishEservice()
                .withConsumer(consumer, User.getTenantAdmin(consumer))
                .addActiveAgreement()
                .addPurposeInState(PurposeVersionState.ACTIVE);
    }

    @Given("un eservice creato da {tenant} con una richiesta di fruizione e una finalità in stato {purposeState} provenienti da {tenant}")
    public void setupEservice(Tenant producer, PurposeVersionState purposeState, Tenant consumer) {
        journey
                .withProducer(producer, User.getTenantAdmin(producer))
                .publishEservice()
                .withConsumer(consumer, User.getTenantAdmin(consumer))
                .addActiveAgreement()
                .addPurposeInState(purposeState);
    }
}