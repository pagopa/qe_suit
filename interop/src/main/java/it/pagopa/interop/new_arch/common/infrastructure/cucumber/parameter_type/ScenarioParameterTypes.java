package it.pagopa.interop.new_arch.common.infrastructure.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.contract.model.client.Client;
import it.pagopa.interop.common.contract.model.purpose.Purpose;
import it.pagopa.interop.common.contract.model.shared.ClientAssertion;
import it.pagopa.interop.common.contract.model.shared.DPoPProof;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScenarioParameterTypes {

    private final ScenarioContext scenarioContext;

    @ParameterType("dpop proof|dpop proof creata")
    public DPoPProof currentDpopProof(String token) {
        return scenarioContext.getLastOrThrow(DPoPProof.class);
    }

    @ParameterType("client|client creato")
    public Client currentClient(String token) {
        return scenarioContext.getLastOrThrow(Client.class);
    }

    @ParameterType("purpose|purpose creata|finalità|finalità creata")
    public Purpose currentPurpose(String token) {
        return scenarioContext.getLastOrThrow(Purpose.class);
    }

    @ParameterType("client assertion|client assertion creata")
    public ClientAssertion currentClientAssertion(String token) {
        return scenarioContext.getLastOrThrow(ClientAssertion.class);
    }
}
