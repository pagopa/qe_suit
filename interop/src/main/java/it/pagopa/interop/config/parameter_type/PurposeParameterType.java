package it.pagopa.interop.config.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.domain.context.PurposeContext;
import it.pagopa.interop.domain.model.Purpose;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PurposeParameterType {
    private final PurposeContext purposeContext;

    @ParameterType("purpose|purpose creata|finalità|finalità creata")
    public Purpose currentPurpose() {
        return purposeContext.getLast();
    }
}
