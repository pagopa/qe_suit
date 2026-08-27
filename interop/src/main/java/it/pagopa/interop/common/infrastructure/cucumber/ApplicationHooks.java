package it.pagopa.interop.common.infrastructure.cucumber;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.interop.common.kernel.context.CurrentTestKind;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.TestKind;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ApplicationHooks {

    private final CurrentTestKind currentTestKind;
    private final CurrentChannel currentChannel;

    @Before
    public void beforeScenario(Scenario scenario) {
        currentTestKind.setCurrentTestKind(TestKind.FLOW);
        MDC.put("scenario", scenario.getName());
    }

    @After
    public void afterScenario() {
        MDC.remove("scenario");

        var errors = currentTestKind.getEventualConsistencyErrors();

        if (!errors.isEmpty()) {
            String formattedErrors = errors.stream()
                    .map(error -> "- " + error)
                    .collect(Collectors.joining(System.lineSeparator()));

            throw new RuntimeException(
                    "Eventual consistency errors found:"
                            + System.lineSeparator()
                            + formattedErrors
            );
        }
    }

    @Before("@Business")
    public void beforeBusinessScenario(Scenario scenario) {
       currentTestKind.setCurrentTestKind(TestKind.FLOW);
    }

    @Before("@Contract")
    public void beforeContractScenario(Scenario scenario) {
        currentTestKind.setCurrentTestKind(TestKind.CONTRACT);
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
