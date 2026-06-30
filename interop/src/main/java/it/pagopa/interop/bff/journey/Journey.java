package it.pagopa.interop.bff.journey;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.bff.service.EServiceTestService;
import it.pagopa.interop.common.contract.model.agreement.AgreementState;
import it.pagopa.interop.common.contract.model.eservice.EServiceDescriptorState;
import it.pagopa.interop.common.contract.model.purpose.PurposeVersionState;
import it.pagopa.interop.common.contract.model.shared.enums.Channel;
import it.pagopa.interop.common.contract.model.shared.enums.Tenant;
import it.pagopa.interop.common.contract.model.shared.enums.User;
import it.pagopa.interop.common.cucumber.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@ScenarioScope
@RequiredArgsConstructor
public class Journey implements it.pagopa.interop.common.contract.journey.Journey {

    private final UserContext userContext;
    private final EServiceTestService eServiceTestService;

    private Tenant producerTenant;
    private User producerUser;
    private Tenant consumerTenant;
    private User consumerUser;

    @Override
    public it.pagopa.interop.common.contract.journey.Journey withProducer(Tenant tenant, User user) {
        this.producerTenant = tenant;
        this.producerUser = user;
        setUserContext(user, tenant);
        return this;
    }

    @Override
    public it.pagopa.interop.common.contract.journey.Journey withConsumer(Tenant tenant, User user) {
        this.consumerTenant = tenant;
        this.consumerUser = user;
        setUserContext(user, tenant);
        return this;
    }

    private void setUserContext(User user, Tenant tenant) {
        if (user == null || tenant == null) {
            return;
        }

        userContext.set(user, tenant);
    }

    @Override
    public it.pagopa.interop.common.contract.journey.Journey addAgreement(AgreementState agreementState) {
        return null;
    }

    @Override
    public it.pagopa.interop.common.contract.journey.Journey createEService(EServiceDescriptorState state) {
        return null;
    }

    @Override
    public it.pagopa.interop.common.contract.journey.Journey publishEService() {
        return null;
    }

    @Override
    public it.pagopa.interop.common.contract.journey.Journey addPurpose(PurposeVersionState state) {
        return null;
    }

    @Override
    public boolean supports(Channel delimiter) {
        return delimiter == Channel.BFF;
    }
}