package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.infrastructure.page.component.Header;
import it.pagopa.send.web.infrastructure.page.component.Sidebar;
import it.pagopa.send.web.login.infrastructure.page.LogoutPage;
import it.pagopa.send.web.mittente.infrastructure.page.APIKeyPage;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.mittente.infrastructure.page.PlatformStatusPage;
import it.pagopa.send.web.mittente.infrastructure.page.StatisticsPage;
import it.pagopa.send.web.supporto.infrastructure.page.BackstageProfilePage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

/**
 * Sostituisce {@code features/supporto/portale-backstage-enti.feature}. A differenza delle altre
 * classi di contratto "di navigazione", qui non si usa {@code WebBrowserContractValidator} (nessun
 * utente da impersonare: l'accesso passa dal token già presente nell'URL di
 * {@link BackstageProfilePage} e dalla selezione dell'ente), e le pagine successive
 * (Dashboard/API Key/Statistics/Platform status) sono raggiunte tramite la sidebar della stessa
 * sessione browser, non via URL diretto: per questo lo scenario resta un unico {@code @Test}
 * sequenziale sullo stesso {@link WebPresentationGateway}, come previsto per i flussi non
 * esprimibili come singolo {@code WebScenario}.
 */
@ActiveProfiles({"test", "junit"})
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class WebSupportoNavigationContractTest {

    private final ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider;

    public WebSupportoNavigationContractTest(
            @Qualifier("junitWebPresentationGateway") ObjectProvider<WebPresentationGateway> webPresentationGatewayProvider
    ) {
        this.webPresentationGatewayProvider = webPresentationGatewayProvider;
    }

    @Test
    void shouldNavigateBackstagePortalForSupportUser() {
        WebPresentationGateway gateway = webPresentationGatewayProvider.getObject();
        try {
            BackstageProfilePage backstageProfilePage = gateway.bind(BackstageProfilePage.class);
            backstageProfilePage.navigateTo();
            backstageProfilePage.selectComune("Comune di Palermo");

            gateway.bind(DashboardPage.class).assertLoaded();

            gateway.bind(Sidebar.class).goToSection("API Key");
            gateway.bind(APIKeyPage.class).assertSupportCannotSeeApiKey();

            gateway.bind(Sidebar.class).goToSection("Statistics");
            gateway.bind(StatisticsPage.class).assertLoaded();

            gateway.bind(Sidebar.class).goToSection("Platform status");
            gateway.bind(PlatformStatusPage.class).assertLoaded();

            gateway.bind(Header.class).logout();
            gateway.bind(LogoutPage.class).assertLoaded();
        } finally {
            gateway.close();
        }
    }
}
