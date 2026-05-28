package it.pagopa.interop.domain.services.journey;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.domain.context.EserviceContext;
import it.pagopa.interop.domain.enums.Tenant;
import it.pagopa.interop.domain.enums.User;
import it.pagopa.interop.domain.services.agreement.AgreementService;
import it.pagopa.interop.domain.services.eservice.EserviceService;
import it.pagopa.interop.domain.services.purpose.PurposeService;
import it.pagopa.interop.generated.openapi.clients.bff.model.EServiceSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import it.pagopa.interop.domain.context.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@ScenarioScope
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Journey {

    private final EserviceService eserviceService;
    private final AgreementService agreementService;
    private final PurposeService purposeService;

    private final CurrentUserContext userContext;
    private final EserviceContext eserviceContext;

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
        var agreement = agreementService.createAgreement(eserviceContext.getLast());
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
        var eservice = eserviceContext.getLast();
        purposeService.createEservicePurposeWithState(eservice, state, purpose);
        return this;
    }

    private void setUserContext(User user, Tenant tenant) {
        if (user != null && tenant != null) {
            userContext.set(user, tenant);
        }
    }
}