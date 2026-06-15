package it.pagopa.interop.web.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.web.page.catalog.EServiceCatalogPage;
import it.pagopa.interop.web.page.dev_tools.DevToolsPage;
import it.pagopa.interop.web.page.dev_tools.debug_client_assertion.DebugClientAssertionPage;
import it.pagopa.interop.web.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.web.page.login.DashboardPage;
import it.pagopa.interop.web.page.login.LoginPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@Getter
@Setter
public class WebPageConfig {


    @Bean
    @ScenarioScope
    public LoginPage loginPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(LoginPage.class);
    }

    @Bean
    @ScenarioScope
    public DashboardPage dashboardPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(DashboardPage.class);
    }

    @Bean
    @ScenarioScope
    public EServiceCatalogPage eServiceCatalogPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceCatalogPage.class);
    }

    @Bean
    @ScenarioScope
    public EServiceCreationPage eServiceCreationPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(EServiceCreationPage.class);
    }

    @Bean
    @ScenarioScope
    public DebugClientAssertionPage debugClientAssertionPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(DebugClientAssertionPage.class);
    }

    @Bean
    @ScenarioScope
    public DevToolsPage devToolsPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(DevToolsPage.class);
    }
}
