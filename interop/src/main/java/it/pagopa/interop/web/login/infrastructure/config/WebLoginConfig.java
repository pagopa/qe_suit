package it.pagopa.interop.new_arch.web.login.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.interop.new_arch.web.login.infrastructure.page.DashboardPage;
import it.pagopa.interop.new_arch.web.login.infrastructure.page.LoginPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@Getter
@Setter
public class WebLoginConfig {
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
}
