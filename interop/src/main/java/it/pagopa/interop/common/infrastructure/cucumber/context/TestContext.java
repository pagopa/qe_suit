package it.pagopa.interop.common.infrastructure.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.kernel.domain.TestKind;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Setter
@Getter
public class TestContext {
    /**
     * Tipo di test attualmente in esecuzione
     * <p>
     * Viene lasciato di default a CONTRACT poiché i test di questo tipo non vengono eseguiti mediante
     * scenari Cucumber ma tramite JUnit 5 e pertanto non potrebbero essere associati ad alcun tag @Contract
     * </p>
     */
    private TestKind currentTestKind = TestKind.CONTRACT;
}
