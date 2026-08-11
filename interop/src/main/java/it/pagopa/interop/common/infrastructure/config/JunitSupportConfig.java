package it.pagopa.interop.common.infrastructure.config;

import it.pagopa.interop.common.infrastructure.context.inmemory.*;
import it.pagopa.interop.common.kernel.context.*;
import it.pagopa.interop.common.kernel.domain.Channel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.MDC;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JunitSupportConfig {

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        MDC.put("scenario", testInfo.getDisplayName());
    }

    @AfterEach
    void afterEach() {
        MDC.remove("scenario");
    }

    @Bean
    CurrentChannel currentChannel() {
        InMemoryCurrentChannel currentChannel = new InMemoryCurrentChannel();
        currentChannel.setCurrentChannel(Channel.BFF);
        return currentChannel;
    }

    @Bean
    CurrentTestKind currentTestKind() {
        return new InMemoryCurrentTestKind();
    }

    @Bean
    CurrentUserSession currentUserSession() {
        return new InMemoryCurrentUserSession();
    }

    @Bean
    EntityStore entityStore() {
        return new InMemoryEntityStore();
    }

    @Bean
    LastApiResponseStore lastApiResponseStore() {
        return new InMemoryLastApiResponseStore();
    }
}
