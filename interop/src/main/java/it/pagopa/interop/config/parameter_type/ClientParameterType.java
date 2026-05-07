package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.domain.enums.InteropClientType;

public class ClientParameterType {

    @ParameterType("CONSUMER|Consumer|consumer|API|Api|api")
    public InteropClientType clientType(String type){
        return InteropClientType.valueOf(type);
    }
}
