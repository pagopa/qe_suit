package it.pagopa.send.common.infrastructure.context;

import io.cucumber.spring.ScenarioScope;
import it.pagopa.send.common.infrastructure.context.cucumber.ChannelContext;
import it.pagopa.send.common.infrastructure.context.cucumber.UserSessionContext;
import it.pagopa.send.common.kernel.context.CurrentChannel;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
//@Profile("cucumber")
public class ContextConfig {

    @Bean
    @ScenarioScope
    @Primary
    CurrentChannel currentChannel() {
        return new ChannelContext();
    }

    @Bean
    @ScenarioScope
    @Primary
    CurrentUserSession currentUserSession() {
        return new UserSessionContext();
    }

}
