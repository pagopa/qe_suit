package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.domain.context.ClientContext;
import it.pagopa.interop.domain.enums.InteropClientType;
import it.pagopa.interop.domain.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientParameterType {
    private final ClientContext clientContext;

    @ParameterType("CONSUMER|Consumer|consumer|API|Api|api")
    public InteropClientType clientType(String type){
        return InteropClientType.valueOf(type);
    }

    @ParameterType("client|client creato")
    public Client currentClient(){
        return clientContext.getLast();
    }

}
