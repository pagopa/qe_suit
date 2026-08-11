package it.pagopa.interop.common.infrastructure.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.interop.common.infrastructure.cucumber.context.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ContextConfig {
    @Bean
    @ScenarioScope
    @Primary
    CurrentTestKind currentTestKind() {
        return new TestContext();
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
}
