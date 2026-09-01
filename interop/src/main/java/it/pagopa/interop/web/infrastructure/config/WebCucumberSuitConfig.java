package it.pagopa.interop.web.infrastructure.config;

import io.cucumber.spring.ScenarioScope;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import it.frontend.e2e.framework.web.adapter.selenium.SeleniumApiAdapter;
import it.pagopa.infrastructure.browser.AuthenticatedLocatableCapabilityHandler;
import it.pagopa.interop.web.infrastructure.config.suit.WebSuitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile("cucumber")
@RequiredArgsConstructor
public class WebCucumberSuitConfig {

    private final WebSuitConfig webSuitConfig;

    @Bean("cucumberWebAdapter")
    @ScenarioScope
    public IWebPresentationApiAdapter webAdapter() {
        BrowserSettings settings = BrowserSettings.of(
                webSuitConfig.getBrowser(),
                webSuitConfig.isHeadless(),
                webSuitConfig.getArguments()
        );

        return new SeleniumApiAdapter(settings);
    }

    @Bean("cucumberWebPresentationGateway")
    @Primary
    @ScenarioScope
    public WebPresentationGateway webPresentationGateway(
            Environment environment,
            IWebPresentationApiAdapter adapter,
            AuthenticatedLocatableCapabilityHandler authenticatedLocatableCapabilityHandler
    ) {
        return webSuitConfig.createWebPresentationGateway(
                environment,
                adapter,
                authenticatedLocatableCapabilityHandler
        );
    }
}