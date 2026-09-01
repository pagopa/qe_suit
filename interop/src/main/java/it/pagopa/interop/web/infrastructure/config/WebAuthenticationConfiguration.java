package it.pagopa.interop.web.infrastructure.config;

import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.application.context.BrowserContext;
import it.pagopa.infrastructure.browser.AuthenticatedLocatableCapability;
import it.pagopa.infrastructure.browser.AuthenticatedLocatableCapabilityHandler;
import it.pagopa.infrastructure.contract.browser.WebSessionProvider;
import it.pagopa.interop.bff.infrastructure.security.bearer.BearerAuthProvider;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cucumber")
public class WebAuthenticationConfiguration {

    @Bean
    WebSessionProvider webSessionConfigurer(
            @Qualifier("cucumberWebAdapter") IWebPresentationApiAdapter adapter,
            BrowserContext browserContext,
            CurrentUserSession currentUserSession,
            BearerAuthProvider bearerAuthProvider,
            @Value("${interop.web.catalog}") String catalogUrl
    ) {
        return () -> {
            var currentUser = currentUserSession.getUser();
            var currentTenant = currentUserSession.getTenant();
            String sessionToken = bearerAuthProvider.getToken(currentUser, currentTenant);

            if (browserContext.getCurrentUrl() == null) {
                adapter.navigateTo(Url.of(catalogUrl));
            }

            adapter.setLocalStorageItem("token", sessionToken);
        };
    }

    @Bean
    AuthenticatedLocatableCapability authenticatedLocatableCapability(
            @Qualifier("cucumberWebAdapter") IWebPresentationApiAdapter adapter,
            BrowserContext browserContext,
            WebSessionProvider webSessionConfigurer
    ) {
        return new AuthenticatedLocatableCapability(adapter, browserContext, webSessionConfigurer);
    }

    @Bean
    AuthenticatedLocatableCapabilityHandler authenticatedLocatableCapabilityHandler(
            AuthenticatedLocatableCapability capability
    ) {
        return new AuthenticatedLocatableCapabilityHandler(capability);
    }
}
