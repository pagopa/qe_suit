package it.pagopa.interop.bff.service;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.cucumber.context.UserContext;
import it.pagopa.interop.common.domain.enums.Tenant;
import it.pagopa.interop.common.domain.enums.User;
import it.pagopa.interop.common.domain.model.Eservice;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@ScenarioScope
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Journey {

    private final EserviceDataPreparationService eserviceService;
    private final AgreementDataPreparationService agreementService;
    private final PurposeDataPreparationService purposeService;

    private final UserContext userContext;
    private final ScenarioContext scenarioContext;

    private Tenant producerTenant;
    private User producerUser;
    private Tenant consumerTenant;
    private User consumerUser;

    public Journey publishEservice() {
        publishEservice(es -> {
        });
        return this;
    }

    public Journey publishEservice(Consumer<EServiceSeed> eservice) {
        setUserContext(producerUser, producerTenant);
        var draftEservice = eserviceService.createEservice(eservice);
        eserviceService.publishEservice(draftEservice);
        return this;
    }

    public Journey withProducer(Tenant tenant, User user) {
        setUserContext(user, tenant);
        this.producerTenant = tenant;
        this.producerUser = user;
        return this;
    }

    public Journey withConsumer(Tenant tenant, User user) {
        setUserContext(user, tenant);
        this.consumerTenant = tenant;
        this.consumerUser = user;
        return this;
    }

    public Journey addActiveAgreement() {
        setUserContext(consumerUser, consumerTenant);
        var agreement = agreementService.createAgreement(scenarioContext.getLastOrThrow(Eservice.class));
        agreementService.submitAgreement(agreement);
        return this;
    }

    public Journey addPurposeInState(PurposeVersionState state) {
        addPurposeInState(state, purpose -> {
        });
        return this;
    }

    public Journey addPurposeInState(PurposeVersionState state, Consumer<PurposeSeed> purpose) {
        setUserContext(consumerUser, consumerTenant);
        var eservice = scenarioContext.getLastOrThrow(Eservice.class);
        purposeService.createEservicePurposeWithState(eservice, state, purpose);
        return this;
    }

    private void setUserContext(User user, Tenant tenant) {
        if (user != null && tenant != null) {
            userContext.set(user, tenant);
        }
    }
}