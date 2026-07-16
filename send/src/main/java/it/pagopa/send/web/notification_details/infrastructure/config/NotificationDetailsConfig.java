package it.pagopa.send.web.notification_details.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationDetailsPFPage;
import it.pagopa.send.domain.web.pages.destinatario.pf.NotificationPFPage;
import it.pagopa.send.domain.web.pages.mittente.DashboardPage;
import it.pagopa.send.domain.web.pages.mittente.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationStatusDetailsPage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class NotificationDetailsConfig {

    @Bean
    @ScenarioScope
    public DashboardPage dashboardPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(DashboardPage.class);
    }

    @Bean
    @ScenarioScope
    public MittenteNotificationDetailsPage mittenteNotificationDetailsPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(MittenteNotificationDetailsPage.class);
    }

    @Bean
    @ScenarioScope
    public NotificationPFPage notificationPFPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(NotificationPFPage.class);
    }

    @Bean
    @ScenarioScope
    public NotificationDetailsPFPage notificationDetailsPFPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(NotificationDetailsPFPage.class);
    }

    @Bean
    @ScenarioScope
    public NotificationStatusDetailsPage notificationStatusDetailsPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(NotificationStatusDetailsPage.class);
    }
}
