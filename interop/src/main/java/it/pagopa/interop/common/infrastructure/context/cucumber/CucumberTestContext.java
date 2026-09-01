package it.pagopa.interop.common.infrastructure.context.cucumber;

import it.pagopa.kernel.context.TestContext;
import it.pagopa.kernel.domain.TestKind;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CucumberTestContext implements TestContext {
    /**
     * Tipo di test attualmente in esecuzione
     * <p>
     * Viene lasciato di default a CONTRACT poiché i test di questo tipo non vengono eseguiti mediante
     * scenari Cucumber ma tramite JUnit 5 e pertanto non potrebbero essere associati ad alcun tag @Contract
     * </p>
     */
    private TestKind currentTestKind = TestKind.CONTRACT;

    private List<String> eventualConsistencyErrors = new ArrayList<>();

    @Override
    public void addEventualConsistencyError(String error) {
        eventualConsistencyErrors.add(error);
    }
}
