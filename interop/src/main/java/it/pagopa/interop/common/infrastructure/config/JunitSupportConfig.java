package it.pagopa.interop.common.infrastructure.config;

import it.pagopa.interop.common.infrastructure.context.CurrentChannel;
import it.pagopa.interop.common.infrastructure.context.CurrentTestKind;
import it.pagopa.interop.common.infrastructure.context.CurrentUserSession;
import it.pagopa.interop.common.infrastructure.context.EntityStore;
import it.pagopa.interop.common.infrastructure.context.LastApiResponseStore;
import it.pagopa.interop.common.infrastructure.context.inmemory.InMemoryCurrentChannel;
import it.pagopa.interop.common.infrastructure.context.inmemory.InMemoryCurrentTestKind;
import it.pagopa.interop.common.infrastructure.context.inmemory.InMemoryCurrentUserSession;
import it.pagopa.interop.common.infrastructure.context.inmemory.InMemoryEntityStore;
import it.pagopa.interop.common.infrastructure.context.inmemory.InMemoryLastApiResponseStore;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class JunitSupportConfig {

    @Bean
    @Primary
    CurrentChannel currentChannel() {
        InMemoryCurrentChannel currentChannel = new InMemoryCurrentChannel();
        currentChannel.setCurrentChannel(Channel.BFF);
        return currentChannel;
    }

    @Bean
    @Primary
    CurrentTestKind currentTestKind() {
        return new InMemoryCurrentTestKind();
    }

    @Bean
    @Primary
    CurrentUserSession currentUserSession() {
        return new InMemoryCurrentUserSession();
    }

    @Bean
    @Primary
    EntityStore entityStore() {
        return new InMemoryEntityStore();
    }

    @Bean
    @Primary
    LastApiResponseStore lastApiResponseStore() {
        return new InMemoryLastApiResponseStore();
    }
}
