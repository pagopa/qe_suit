package it.pagopa.send.web.campaigns.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.web.campaigns.infrastructure.page.CampaignsPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationDetailsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_details.infrastructure.page.MittenteNotificationDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.page.timeline.TimelineDetailsPage;
import it.pagopa.send.web.notification_details.infrastructure.suit.NotificationStatusDetailsPage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CampaignsConfig {

    @Bean
    @ScenarioScope
    public CampaignsPage campaignsPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(CampaignsPage.class);
    }
}
