package it.pagopa.send.common.infrastructure.config;

import it.pagopa.application.context.BrowserContext;
import it.pagopa.application.context.EntityStore;
import it.pagopa.application.context.LastApiResponseStore;
import it.pagopa.application.context.TestContext;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.infrastructure.context.InMemoryBrowserContext;
import it.pagopa.infrastructure.context.InMemoryCurrentChannel;
import it.pagopa.infrastructure.context.InMemoryEntityStore;
import it.pagopa.infrastructure.context.InMemoryTestContext;
import it.pagopa.send.common.infrastructure.context.InMemoryCurrentUserSession;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.common.kernel.domain.Channel;
import it.pagopa.send.common.notification.domain.LegalNotificationCreationRequest;
import it.pagopa.send.controller.creazione_notifica.NotificationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.MDC;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

/**
 * {@code @Profile("junit")} esplicito: {@code TestBootApp} usa un {@code @ComponentScan}
 * dichiarato manualmente (non quello implicito di {@code @SpringBootApplication}), per cui
 * l'esclusione automatica delle classi {@code @TestConfiguration} dalla scansione non è
 * affidabile — questa classe finisce comunque scansionata anche nel contesto Cucumber. Senza
 * questo profilo i suoi bean collidono per nome con quelli equivalenti di {@code CucumberConfig}.
 */
@TestConfiguration
@Profile("junit")
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
    CurrentChannel<Channel> currentChannel() {
        InMemoryCurrentChannel<Channel> currentChannel = new InMemoryCurrentChannel<>();
        currentChannel.setCurrentChannel(Channel.B2B);
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
        return new it.pagopa.infrastructure.context.InMemoryLastApiResponseStore();
    }

    /**
     * Equivalente non scoped del bean {@code @ScenarioScope} di {@code
     * LegalNotificationRoutingConfig}, usato dalle implementazioni di {@code
     * LegalNotificationGateway} (es. {@code B2BLegalNotificationGateway}) fuori da uno scenario
     * Cucumber.
     */
    @Bean
    LegalNotificationCreationRequest legalNotificationCreationRequest() {
        return new LegalNotificationCreationRequest();
    }

    /**
     * Equivalente non scoped di {@code NotificationContext} (di norma {@code @ScenarioScope}),
     * usato dalle implementazioni di {@code LegalNotificationGateway} fuori da uno scenario
     * Cucumber.
     */
    @Bean
    NotificationContext notificationContext() {
        return new NotificationContext();
    }
}
