package it.pagopa.send.web.infrastructure.config;

import it.frontend.e2e.framework.core.model.selector.XPathSelector;
import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.frontend.e2e.framework.web.adapter.IWebPresentationApiAdapter;
import it.frontend.e2e.framework.web.adapter.model.BrowserSettings;
import it.frontend.e2e.framework.web.adapter.selenium.SeleniumApiAdapter;
import it.frontend.e2e.framework.web.config.WebSuiteBuilder;
import it.frontend.e2e.framework.web.model.location.Url;
import it.pagopa.infrastructure.contract.browser.WebContractValidator;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.kernel.context.CurrentUserSession;
import it.pagopa.send.domain.web.config.WebConfig;
import it.pagopa.send.web.infrastructure.config.suit.AuthenticatedLocatableCapabilityHandler;
import it.pagopa.send.web.infrastructure.config.suit.AuthenticatedLocatableCapabilityImpl;
import it.pagopa.send.web.infrastructure.cucumber.WebBrowserContext;
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
 * registrato, quindi non si possono riusare i bean {@code @ScenarioScope} già scansionati
 * ({@link WebBrowserContext}, {@code AuthenticatedLocatableCapabilityImpl}/{@code Handler} — che
 * dipende da quello). Si ricostruisce qui, a mano, la stessa identica catena
 * (adapter → {@link AuthenticatedLocatableCapabilityImpl}, stesso meccanismo a sessionStorage
 * usato da Cucumber, invariato) usando un {@link WebBrowserContext} semplice (oggetto Java, non
 * bean Spring: registrarlo come bean con lo stesso nome/tipo di quello scansionato farebbe fallire
 * l'avvio del contesto per bean duplicati) il cui {@code currentUser} viene letto da
 * {@link CurrentUserSession#getSender()}, già popolato a quel punto da
 * {@code LegalNotificationJourneyImpl.sendNotification}.
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
            CurrentUserSession currentUserSession
    ) {
        BrowserSettings settings = BrowserSettings.of(
                webConfig.getBrowser(),
                webConfig.isHeadless(),
                webConfig.getArguments()
        );
        IWebPresentationApiAdapter adapter = new SeleniumApiAdapter(settings);

        WebBrowserContext webBrowserContext = new WebBrowserContext();
        webBrowserContext.setCurrentUser(currentUserSession.getSender());

        AuthenticatedLocatableCapabilityImpl capability = new AuthenticatedLocatableCapabilityImpl(
                adapter, webBrowserContext, sessionPayloadFactory, environment);
        AuthenticatedLocatableCapabilityHandler handler = new AuthenticatedLocatableCapabilityHandler(capability);

        return WebSuiteBuilder.builder()
                .withAdapter(() -> adapter)
                .addHandlers(handler)
                .withLocationResolver(location -> Url.of(environment.resolvePlaceholders(location)))
                .withSelectorResolver(xpath -> XPathSelector.of(environment.resolvePlaceholders(xpath)))
                .build();
    }
}
