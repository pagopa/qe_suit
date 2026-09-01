package it.pagopa.interop.common.infrastructure.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.infrastructure.context.cucumber.*;
import it.pagopa.kernel.context.BrowserContext;
import it.pagopa.interop.common.kernel.context.CurrentChannel;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.kernel.context.EntityStore;
import it.pagopa.kernel.context.LastApiResponseStore;
import it.pagopa.kernel.context.TestContext;
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
    BrowserContext browserContext() {
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
}