package it.pagopa.interop.common.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.infrastructure.cucumber.hook.channel.*;
import it.pagopa.interop.common.infrastructure.context.cucumber.*;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.interop.common.infrastructure.channel.CurrentChannel;
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
    ChannelModule<Channel> channelModule() {
        return ChannelModule.of(
                new ChannelGherkinMapping<>(
                        Map.of(
                                "BFF", Channel.BFF,
                                "WEB", Channel.WEB_BROWSER,
                                "WEB_BROWSER", Channel.WEB_BROWSER
                        ),
                        Map.of(
                                Channel.BFF, "BFF",
                                Channel.WEB_BROWSER, "WEB"
                        )
                ),
                new ChannelConfig<>(
                        Channel.BFF,
                        Channel.BFF,
                        Channel.BFF
                )
        );
    }

    @Bean
    @ScenarioScope
    ChannelRuntime<Channel> channelRuntime(
            ChannelModule<Channel> channelModule,
            CurrentChannel currentChannel
    ) {
        return channelModule.newRuntime(currentChannel);
    }
}