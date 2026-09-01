package it.pagopa.interop.common.infrastructure.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.kernel.context.TestContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.kernel.TestKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class ApplicationHooks {

    private final TestContext testContext;
    private final CurrentChannel currentChannel;

    @Before
    public void beforeScenario(Scenario scenario) {
        testContext.setCurrentTestKind(TestKind.FLOW);
        MDC.put("scenario", scenario.getName());
    }

    @After
    public void afterScenario() {
        var errors = testContext.getEventualConsistencyErrors();

        if (!errors.isEmpty()) {
            String formattedErrors = errors.stream()
                    .map(error -> "- " + error)
                    .collect(Collectors.joining(System.lineSeparator()));

            log.error("Eventual consistency errors found:\n{}", formattedErrors);
        }

        MDC.remove("scenario");
    }

    @Before("@Business")
    public void beforeBusinessScenario(Scenario scenario) {
       testContext.setCurrentTestKind(TestKind.FLOW);
    }

    @Before("@Contract")
    public void beforeContractScenario(Scenario scenario) {
        testContext.setCurrentTestKind(TestKind.CONTRACT);
    }

    @Before("@BFF")
    public void beforeBFFScenario(Scenario scenario) {
        currentChannel.setCurrentChannel(Channel.BFF);
    }

    @Before("@WEB")
    public void beforeWEBScenario(Scenario scenario) {
        currentChannel.setCurrentChannel(Channel.WEB_BROWSER);
    }
}
