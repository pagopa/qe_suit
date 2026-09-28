package it.pagopa.interop.common.infrastructure.cucumber.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.application.context.TestContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.application.TestKind;
import it.pagopa.infrastructure.logging.TestMdcKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class ApplicationHooks {

    private final TestContext testContext;
    private final CurrentChannel<Channel> currentChannel;

    // Ordine minimo: l'MDC deve essere popolato prima di qualsiasi altro hook che logga.
    @Before(order = Integer.MIN_VALUE)
    public void beforeScenario(Scenario scenario) {
        testContext.setCurrentTestKind(TestKind.FLOW);

        String scenarioName = ScenarioMdcSupport.scenarioName(scenario);
        MDC.put(TestMdcKeys.TEST_CASE_ID, ScenarioMdcSupport.testCaseId(scenario));
        MDC.put(TestMdcKeys.SCENARIO_NAME, scenarioName);
        MDC.put(TestMdcKeys.SOURCE_FILE, ScenarioMdcSupport.sourceFile(scenario));

        log.info("=== SCENARIO START: {} ===", scenarioName);
    }

    // Gli @After vengono eseguiti in ordine decrescente: l'ordine minimo garantisce
    // che l'MDC resti popolato per tutti gli altri hook di cleanup.
    @After(order = Integer.MIN_VALUE)
    public void afterScenario(Scenario scenario) {
        var errors = testContext.getEventualConsistencyErrors();

        if (!errors.isEmpty()) {
            String formattedErrors = errors.stream()
                    .map(error -> "- " + error)
                    .collect(Collectors.joining(System.lineSeparator()));

            log.error("Eventual consistency errors found:\n{}", formattedErrors);
        }

        log.info("=== SCENARIO END: {} | Status: {} ===",
                ScenarioMdcSupport.scenarioName(scenario), scenario.getStatus());

        MDC.remove(TestMdcKeys.TEST_CASE_ID);
        MDC.remove(TestMdcKeys.SCENARIO_NAME);
        MDC.remove(TestMdcKeys.SOURCE_FILE);
    }
}
