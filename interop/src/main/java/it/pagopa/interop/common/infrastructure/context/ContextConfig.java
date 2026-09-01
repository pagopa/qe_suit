package it.pagopa.interop.common.infrastructure.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.infrastructure.context.cucumber.*;
import it.pagopa.interop.common.infrastructure.cucumber.channel.ChannelScenarioHook;
import it.pagopa.interop.common.infrastructure.cucumber.channel.ChannelStepListener;
import it.pagopa.interop.common.kernel.context.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cucumber")
public class ContextConfig {
    @Bean
    @ScenarioScope
    @Primary
    TestContext currentTestKind() {
        return new CucumberTestContext();
    }

    @Bean
    @ScenarioScope
    @Primary
    BrowserContext browserContext(){
        return new CucumberBrowserContext();
    }

    @Bean
    @ScenarioScope
    @Primary
    CurrentUserSession currentUserSession() {
        return new UserContext();
    }

    @Bean
    @ScenarioScope
    @Primary
    CurrentChannel currentChannel() {
        return new ChannelContext();
    }

    @Bean
    @ScenarioScope
    @Primary
    EntityStore entityStore() {
        return new DomainContext();
    }

    @Bean
    @ScenarioScope
    @Primary
    LastApiResponseStore lastApiResponseStore() {
        return new ApiContext();
    }

    @Bean
    @ScenarioScope
    ScenarioChannelContext scenarioChannelContext() {
        return new ScenarioChannelContext();
    }

    /**
     * The listener is a singleton but accesses scenario-scoped beans via {@link ObjectProvider}
     * so it obtains the correct instance per scenario in concurrent execution.
     */
    @Bean
    ChannelStepListener channelStepListener(
            ObjectProvider<ScenarioChannelContext> scenarioChannelContextProvider,
            ObjectProvider<CurrentChannel> currentChannelProvider) {
        ChannelStepListener listener = new ChannelStepListener(
                scenarioChannelContextProvider::getObject,
                currentChannelProvider::getObject);
        ChannelStepListener.setLiveInstance(listener);
        return listener;
    }
}
