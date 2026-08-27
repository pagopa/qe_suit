package it.pagopa.interop.web.infrastructure.config;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import it.frontend.e2e.framework.web.adapter.selenium.SeleniumApiAdapter;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.common.kernel.context.BrowserContext;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.web.infrastructure.config.suit.AuthenticatedLocatableCapabilityHandler;
import it.pagopa.interop.web.infrastructure.config.suit.AuthenticatedLocatableCapabilityImpl;
import it.pagopa.interop.web.infrastructure.config.suit.WebSuitConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
@Profile("junit")
public class WebJUnitSuitConfig {

    @Bean("junitWebPresentationGateway")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    WebPresentationGateway webPresentationGateway(
            WebSuitConfig webSuitConfig,
            Environment environment,
            BrowserContext browserContext,
            CurrentUserSession currentUserSession,
            BearerAuthProvider bearerAuthProvider
    ) {
        BrowserSettings settings = BrowserSettings.of(
                webSuitConfig.getBrowser(),
                webSuitConfig.isHeadless(),
                webSuitConfig.getArguments()
        );

        IWebPresentationApiAdapter adapter =
                new SeleniumApiAdapter(settings);

        AuthenticatedLocatableCapabilityImpl capability =
                new AuthenticatedLocatableCapabilityImpl(
                        adapter,
                        browserContext,
                        currentUserSession,
                        bearerAuthProvider,
                        environment.getRequiredProperty("interop.web.catalog")
                );

        AuthenticatedLocatableCapabilityHandler handler =
                new AuthenticatedLocatableCapabilityHandler(capability);

        return webSuitConfig.createWebPresentationGateway(
                environment,
                adapter,
                handler
        );
    }
}