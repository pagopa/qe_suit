package it.pagopa.send.web.login.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.login.infrastructure.page.TenantSelectionPage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class WebLoginConfig {

    @Bean
    @ScenarioScope
    public TenantSelectionPage tenantSelectionPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(TenantSelectionPage.class);
    }

    @Bean
    @ScenarioScope
    public NotificationPage notificationPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(NotificationPage.class);
    }
}
