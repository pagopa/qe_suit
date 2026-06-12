package it.pagopa.interop.common.cucumber.parameter_type;

import io.cucumber.java.ParameterType;
import it.pagopa.interop.common.cucumber.context.ScenarioContext;
import it.pagopa.interop.common.domain.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.PurposeVersionState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PurposeParameterType {
    private final ScenarioContext purposeContext;

    @ParameterType("purpose|purpose creata|finalità|finalità creata")
    public Purpose currentPurpose(String token) {
        return purposeContext.getLastOrThrow(Purpose.class);
    }

    @ParameterType("SUSPENDED")
    public PurposeVersionState purposeState(String name) {
        return PurposeVersionState.fromValue(name);
    }
}
