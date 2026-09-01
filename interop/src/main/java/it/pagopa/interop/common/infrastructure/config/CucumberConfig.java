package it.pagopa.interop.common.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.infrastructure.cucumber.channel.ChannelConfig;
import it.pagopa.infrastructure.cucumber.channel.ChannelGherkinMapping;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngineConfig;
import it.pagopa.infrastructure.cucumber.channel.GherkinChannelEngine;
import it.pagopa.interop.common.infrastructure.context.cucumber.*;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.interop.common.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.infrastructure.cucumber.channel.InteropChannelEngineConfigs;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.application.context.TestContext;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Map;

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
    CurrentUserSession currentUserSession() {
        return new CucumberCurrentUserSession();
    }

    @Bean
    @ScenarioScope
    @Primary
    CurrentChannel currentChannel() {
        return new CucumberCurrentChannel();
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
    GherkinChannelEngineConfig<Channel> channelModule() {
        return InteropChannelEngineConfigs.interopChannelModule();
    }

    @Bean
    @ScenarioScope
    GherkinChannelEngine<Channel> channelRuntime(
            GherkinChannelEngineConfig<Channel> gherkinChannelEngineConfig,
            CurrentChannel currentChannel
    ) {
        return gherkinChannelEngineConfig.newRuntime(currentChannel);
    }
}