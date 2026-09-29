package it.pagopa.send.common.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.context.CucumberBrowserContext;
import it.pagopa.infrastructure.context.CucumberCurrentChannel;
import it.pagopa.infrastructure.context.CucumberEntityStore;
import it.pagopa.infrastructure.context.CucumberLastApiResponseStore;
import it.pagopa.infrastructure.context.CucumberTestContext;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngine;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngineConfig;
import it.pagopa.send.common.infrastructure.context.cucumber.UserSessionContext;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.kernel.domain.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cucumber")
public class CucumberConfig {

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
    CurrentChannel<Channel> currentChannel() {
        return new CucumberCurrentChannel<>();
    }

    @Bean
    @ScenarioScope
    @Primary
    EntityStore entityStore() {
        return new CucumberEntityStore();
    }

    @Bean
    @ScenarioScope
    @Primary
    LastApiResponseStore lastApiResponseStore() {
        return new CucumberLastApiResponseStore();
    }

    @Bean
    @ScenarioScope
    @Primary
    CurrentUserSession currentUserSession() {
        return new UserSessionContext();
    }

    @Bean
    @ScenarioScope
    GherkinChannelEngine<Channel> channelRuntime(
            GherkinChannelEngineConfig<Channel> gherkinChannelEngineConfig,
            CurrentChannel<Channel> currentChannel
    ) {
        return gherkinChannelEngineConfig.newRuntime(currentChannel);
    }
}
