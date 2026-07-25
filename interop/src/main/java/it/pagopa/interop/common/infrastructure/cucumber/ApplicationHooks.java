package it.pagopa.interop.common.infrastructure.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import it.pagopa.interop.common.infrastructure.cucumber.context.ChannelContext;
import it.pagopa.interop.common.infrastructure.cucumber.context.TestContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.TestKind;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApplicationHooks {

    private final TestContext testContext;
    private final ChannelContext channelContext;

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
        channelContext.setCurrentChannel(Channel.BFF);
    }

    @Before("@WEB")
    public void beforeWEBScenario(Scenario scenario) {
        channelContext.setCurrentChannel(Channel.WEB_BROWSER);
    }
}
