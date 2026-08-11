package it.pagopa.interop.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.client.domain.Client;
import it.pagopa.interop.common.client.domain.ClientAssertion;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.infrastructure.context.EntityStore;
import it.pagopa.interop.common.kernel.security.DPoPProof;
import it.pagopa.interop.common.purpose.domain.Purpose;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//TODO: da rifattorizzare, va messo nelle rispettive cartelle infrastructure/cucumber
public class ScenarioParameterTypes {

    private final EntityStore entityStore;

    @ParameterType("dpop proof|dpop proof creata")
    public DPoPProof currentDpopProof(String token) {
        return entityStore.getLastOrThrow(DPoPProof.class);
    }

    @ParameterType("client|client creato")
    public Client currentClient(String token) {
        return entityStore.getLastOrThrow(Client.class);
    }

    @ParameterType("purpose|purpose creata|finalità|finalità creata")
    public Purpose currentPurpose(String token) {
        return entityStore.getLastOrThrow(Purpose.class);
    }

    @ParameterType("client assertion|client assertion creata")
    public ClientAssertion currentClientAssertion(String token) {
        return entityStore.getLastOrThrow(ClientAssertion.class);
    }

    @ParameterType("e-service|EService|e-service creato|EService creato")
    public EService currentEService(String token) {
        return entityStore.getLastOrThrow(EService.class);
    }

}
