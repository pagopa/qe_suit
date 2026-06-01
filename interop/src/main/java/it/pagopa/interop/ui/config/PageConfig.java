package it.pagopa.interop.ui.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import it.frontend.e2e.framework.web.adapter.selenium.SeleniumApiAdapter;
import it.frontend.e2e.framework.web.config.WebSuiteBuilder;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.ui.domain.page.catalog.EServiceCatalogPage;
import it.pagopa.interop.ui.domain.page.dev_tools.DevToolsPage;
import it.pagopa.interop.ui.domain.page.dev_tools.debug_client_assertion.DebugClientAssertionPage;
import it.pagopa.interop.ui.domain.page.eservice_creation.EServiceCreationPage;
import it.pagopa.interop.ui.domain.page.login.DashboardPage;
import it.pagopa.interop.ui.domain.page.login.LoginPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

@TestConfiguration
@Getter
@Setter
public class PageConfig {


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
