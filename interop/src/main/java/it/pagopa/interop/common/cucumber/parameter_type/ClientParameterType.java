package it.pagopa.interop.common.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.domain.enums.InteropClientType;
import it.pagopa.interop.common.domain.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientParameterType {
    private final ScenarioContext scenarioContext;

    @ParameterType("CONSUMER|Consumer|consumer|API|Api|api")
    public InteropClientType clientType(String type) {
        return InteropClientType.valueOf(type);
    }

    @ParameterType("client|client creato")
    public Client currentClient(String token) {
        return scenarioContext.getLastOrThrow(Client.class);
    }
}
