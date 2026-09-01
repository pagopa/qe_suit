package it.pagopa.interop.common.infrastructure.config;

import it.pagopa.interop.common.infrastructure.context.inmemory.*;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.interop.common.infrastructure.channel.CurrentChannel;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.application.context.TestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.MDC;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JunitContextConfig {

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
    TestContext currentTestKind() {
        return new InMemoryTestContext();
    }

    @Bean
    CurrentUserSession currentUserSession() {
        return new InMemoryCurrentUserSession();
    }

    @Bean
    BrowserContext browserContext() {
        return new InMemoryBrowserContext();
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
