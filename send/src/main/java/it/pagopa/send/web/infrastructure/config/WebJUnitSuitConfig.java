package it.pagopa.send.web.infrastructure.config;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import it.frontend.e2e.framework.web.adapter.selenium.SeleniumApiAdapter;
import it.frontend.e2e.framework.web.config.WebSuiteBuilder;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.infrastructure.context.InMemoryBrowserContext;
import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.web.infrastructure.config.WebConfig;
import it.pagopa.send.web.infrastructure.config.suit.AuthenticatedLocatableCapabilityHandler;
import it.pagopa.send.web.infrastructure.config.suit.AuthenticatedLocatableCapabilityImpl;
import it.pagopa.send.web.login.infrastructure.DynamicUserTokenProvider;
import it.pagopa.send.web.login.infrastructure.SelfCareSessionPayloadFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

/**
 * Equivalente di {@link WebConfig} per i test JUnit puri (contract test), che girano fuori da uno
 * scenario Cucumber: {@code cucumber-glue} (lo scope dietro {@code @ScenarioScope}) non è
 * registrato, quindi non si possono riusare i bean {@code @ScenarioScope} già scansionati. Si
 * ricostruisce qui, a mano, la catena adapter → {@link AuthenticatedLocatableCapabilityImpl}, con lo
 * stesso meccanismo a sessionStorage usato da Cucumber. La capability legge l'utente da
 * {@link CurrentUserSession#getCurrentActor()} al momento della navigazione (impostato da
 * {@code WebBrowserContractValidator#as}/{@code #asRecipient}) e tiene l'URL corrente in un
 * {@link InMemoryBrowserContext} proprio di ogni browser.
 */
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
            CurrentUserSession currentUserSession
    ) {
        return new WebBrowserContractValidator(webContractValidator, currentUserSession);
    }

    @Bean("junitWebPresentationGateway")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    WebPresentationGateway webPresentationGateway(
            WebConfig webConfig,
            Environment environment,
            SelfCareSessionPayloadFactory sessionPayloadFactory,
            CurrentUserSession currentUserSession,
            DynamicUserTokenProvider tokenProvider
    ) {
        BrowserSettings settings = BrowserSettings.of(
                webConfig.getBrowser(),
                webConfig.isHeadless(),
                webConfig.getArguments()
        );
        IWebPresentationApiAdapter adapter = new SeleniumApiAdapter(settings);

        AuthenticatedLocatableCapabilityImpl capability = new AuthenticatedLocatableCapabilityImpl(
                adapter, currentUserSession, new InMemoryBrowserContext(), sessionPayloadFactory, tokenProvider);
        AuthenticatedLocatableCapabilityHandler handler = new AuthenticatedLocatableCapabilityHandler(capability);

        return WebSuiteBuilder.builder()
                .withAdapter(() -> adapter)
                .addHandlers(handler)
                .withLocationResolver(location -> Url.of(environment.resolvePlaceholders(location)))
                .withSelectorResolver(xpath -> XPathSelector.of(environment.resolvePlaceholders(xpath)))
                .build();
    }
}
