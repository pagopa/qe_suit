package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.domain.model.ClientAssertion;

public class ClientAssertionParameterType {
    @ParameterType("client assertion|client assertion creata")
    public ClientAssertion currentClientAssertion(String token){
        throw new IllegalArgumentException("Not implemented yet");
    }
}
