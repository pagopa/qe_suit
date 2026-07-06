package it.pagopa.interop.new_arch.common.infrastructure.cucumber.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.new_arch.common.kernel.domain.TestKind;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Setter
@Getter
//TODO: assicurarsi di scrivere l'hook cucumber
public class TestContext {
    private TestKind currentTestKind = TestKind.FLOW;
}
