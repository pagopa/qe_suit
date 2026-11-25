package it.pagopa.send.web.legal_notification.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.web.mittente.infrastructure.page.NotificationSuccessPage;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Pagine della procedura guidata "Invia una nuova notifica" usate dai gateway della notifica legale sul canale Web;
 * {@code DashboardPage} e {@code MittenteNotificationDetailsPage} sono già registrate da
 * {@code WebNotificationDetailsConfig}.
 */
@TestConfiguration
public class WebLegalNotificationConfig {

    @Bean
    @ScenarioScope
    public CreateNotificationPage createNotificationPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(CreateNotificationPage.class);
    }

    @Bean
    @ScenarioScope
    public NotificationSuccessPage notificationSuccessPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(NotificationSuccessPage.class);
    }
}
