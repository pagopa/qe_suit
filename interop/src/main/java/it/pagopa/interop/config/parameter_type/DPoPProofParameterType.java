package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.domain.context.DPoPProofContext;
import it.pagopa.interop.domain.model.DPoPProof;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DPoPProofParameterType {

    private final DPoPProofContext dpopProofContext;

    @ParameterType("dpop proof|dpop proof creata")
    public DPoPProof currentDpopProof(String token){
        return dpopProofContext.getLast();
    }
}
