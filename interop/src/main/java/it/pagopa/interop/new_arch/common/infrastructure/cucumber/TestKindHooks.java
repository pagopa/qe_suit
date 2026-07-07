package it.pagopa.interop.new_arch.common.infrastructure.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.interop.new_arch.common.infrastructure.cucumber.context.TestContext;
import it.pagopa.interop.new_arch.common.kernel.domain.TestKind;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestKindHooks {

    private final TestContext testContext;

    @Before("@Business")
    public void beforeBusinessScenario(Scenario scenario) {
       testContext.setCurrentTestKind(TestKind.FLOW);
    }

    @Before("@Contract")
    public void beforeContractScenario(Scenario scenario) {
        testContext.setCurrentTestKind(TestKind.CONTRACT);
    }
}
