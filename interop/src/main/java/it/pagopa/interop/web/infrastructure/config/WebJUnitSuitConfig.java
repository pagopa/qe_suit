package it.pagopa.interop.web.infrastructure.config;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import it.frontend.e2e.framework.web.adapter.selenium.SeleniumApiAdapter;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.infrastructure.contract.browser.WebSessionProvider;
import it.pagopa.infrastructure.suit.capability.AuthenticatedLocatableCapability;
import it.pagopa.infrastructure.suit.capability.AuthenticatedLocatableCapabilityHandler;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.infrastructure.config.suit.WebSuitConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
@Profile("junit")
public class WebJUnitSuitConfig {

    @Bean
    WebContractValidator webContractValidator(
            @Qualifier("junitWebPresentationGateway")
            ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider
    ) {
        return new WebContractValidator(webPresentationGatewayProvider::getObject);
    }

    @Bean
    WebBrowserContractValidator webBrowserContractValidator(
            WebContractValidator webContractValidator,
            AuthSession authSession
    ) {
        return new WebBrowserContractValidator(webContractValidator, authSession);
    }

    @Bean("junitWebPresentationGateway")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    WebPresentationGateway webPresentationGateway(
            WebSuitConfig webSuitConfig,
            Environment environment,
            BrowserContext browserContext,
            AuthSession authSession,
            BearerAuthProvider bearerAuthProvider
    ) {
        BrowserSettings settings = BrowserSettings.of(
                webSuitConfig.getBrowser(),
                webSuitConfig.isHeadless(),
                webSuitConfig.getArguments(),
                webSuitConfig.getWaits().toSettings()
        );

        IWebPresentationApiAdapter adapter =
                new SeleniumApiAdapter(settings);

        WebSessionProvider webSessionConfigurer = () -> {
            var currentUser = authSession.getActor();
            var currentTenant = authSession.getTenant();
            String sessionToken = bearerAuthProvider.getToken((User) currentUser, currentTenant);

            if (browserContext.getCurrentUrl() == null) {
                adapter.navigateTo(Url.of(environment.getRequiredProperty("interop.web.catalog")));
            }

            adapter.setLocalStorageItem("token", sessionToken);
        };

        AuthenticatedLocatableCapability capability =
                new AuthenticatedLocatableCapability(
                        adapter,
                        browserContext,
                        webSessionConfigurer
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