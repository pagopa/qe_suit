package it.pagopa.interop.domain.web.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import it.frontend.e2e.framework.web.adapter.selenium.SeleniumApiAdapter;
import it.frontend.e2e.framework.web.config.WebSuiteBuilder;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.interop.domain.web.commons.page.login.LoginPage;
import it.pagopa.interop.domain.web.pages.dev_tools.DevToolsPage;
import it.pagopa.interop.domain.web.pages.dev_tools.debug_client_assertion.DebugClientAssertionPage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.List;

@TestConfiguration
@ConfigurationProperties(prefix = "channel.web")
@Getter
@Setter
public class WebConfig {

    private String browser;
    private boolean headless;
    private List<String> arguments;

    @Bean
    @ScenarioScope
    public WebPresentationGateway webPresentationGateway(Environment environment) {
        BrowserSettings settings = BrowserSettings.of(browser, headless, arguments);

        return WebSuiteBuilder.builder()
                .withAdapter(() -> new SeleniumApiAdapter(settings))
                .withLocationResolver((location) -> {
                    // Risolvo le variabili d'ambiente nella URL
                    String resolvedUrl = environment.resolvePlaceholders(location);
                    return Url.of(resolvedUrl);
                })
                .build();
    }

    @Bean
    @ScenarioScope
    public LoginPage loginPage(WebPresentationGateway webPresentationGateway) {
        return webPresentationGateway.bind(LoginPage.class);
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
