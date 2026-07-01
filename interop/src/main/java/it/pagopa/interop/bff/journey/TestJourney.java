package it.pagopa.interop.bff.journey;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.contract.journey.*;
import it.pagopa.interop.common.contract.model.agreement.AgreementState;
import it.pagopa.interop.common.contract.model.client.ClientKind;
import it.pagopa.interop.common.contract.model.eservice.EServiceDescriptorState;
import it.pagopa.interop.common.contract.model.purpose.PurposeVersionState;
import it.pagopa.interop.common.contract.model.shared.enums.UserRole;
import it.pagopa.interop.common.cucumber.context.UserContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ScenarioScope
public class TestJourney extends AbstractJourney<TestJourney> implements
        IBaseJourney<TestJourney>, IEServiceJourney<TestJourney>, IAgreementJourney<TestJourney>, IClientJourney<TestJourney>, IPurposeJourney<TestJourney> {

    private final EServiceJourney eServiceJourney;
    private final ClientJourney clientJourney;

    public TestJourney(UserContext userContext, EServiceJourney eServiceJourney, ClientJourney clientJourney) {
        super(userContext);
        this.eServiceJourney = eServiceJourney;
        this.clientJourney = clientJourney;
    }

    @Override
    public TestJourney createEService(EServiceDescriptorState state) {
        eServiceJourney.createEService(state);
        return this;
    }

    @Override
    public TestJourney publishEService() {
        eServiceJourney.publishEService();
        return this;
    }

    @Override
    public TestJourney addAgreement(AgreementState agreementState) {
        return null;
    }

    @Override
    public TestJourney createPurpose(PurposeVersionState state) {
        return null;
    }

    @Override
    public TestJourney createClient(ClientKind kind) {
        clientJourney.createClient(kind);
        return this;
    }

    @Override
    public TestJourney createClientAndInclude(ClientKind kind, UserRole... roles) {
        clientJourney.createClientAndInclude(kind, roles);
        return this;
    }

    @Override
    public TestJourney linkPurposeToClient() {
        clientJourney.linkPurposeToClient();
        return this;
    }

    @Override
    public TestJourney linkPurposeToClient(UUID purposeId) {
        clientJourney.linkPurposeToClient(purposeId);
        return this;
    }

    @Override
    public TestJourney generateKeyAndLinkToClient() {
        clientJourney.generateKeyAndLinkToClient();
        return this;
    }
}