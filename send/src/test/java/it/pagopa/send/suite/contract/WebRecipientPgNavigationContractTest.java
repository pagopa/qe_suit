package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.AddressPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.ApiIntegrationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DelegatedNotificationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NewDelegationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.NotificationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.OrganizationAuthorizedRepresentativesPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.OrganizationDelegationsPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.PlatformStatusPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Sostituisce gli scenari "verifica la raggiungibilità delle pagine" di
 * {@code features/pg/navigazione-send-pg.feature}, lato destinatario Persona Giuridica.
 * Si veda {@link WebMittenteNavigationContractTest} per il razionale.
 */
@ActiveProfiles({"test", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebRecipientPgNavigationContractTest {

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldReachNotifications() {
        return reachabilityTest(NotificationPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachDelegatedNotification() {
        return reachabilityTest(DelegatedNotificationPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachOrganizationDelegations() {
        return reachabilityTest(OrganizationDelegationsPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachOrganizationAuthorizedRepresentatives() {
        return reachabilityTest(OrganizationAuthorizedRepresentativesPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachNewDelegation() {
        return reachabilityTest(NewDelegationPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachAddress() {
        return reachabilityTest(AddressPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachApiIntegration() {
        return reachabilityTest(ApiIntegrationPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachPlatformStatus() {
        return reachabilityTest(PlatformStatusPage.class);
    }

    private <P extends Page> Stream<DynamicTest> reachabilityTest(Class<P> pageType) {
        return webContractValidator.asRecipient(Recipient.PETRARCA)
                .on(pageType)
                .tests(Stream.of(new WebScenario<>(
                        "controllo caricamento pagina " + pageType.getSimpleName(),
                        page -> {},
                        page -> {}
                )));
    }
}
