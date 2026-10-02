package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AddressPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AppStatusPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DelegationsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DigitalDomicileActivationPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DigitalDomicileManagementPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NewDelegationPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NotificationPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingAlertsPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingDigitalDomicilePFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.OnboardingIoPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.ProfilePFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.SercqTermsOfServicePFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.SupportPFPage;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.TermsOfServicePFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.infrastructure.page.ConfigureAddressSendPage;
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
 * {@code features/pf/navigazione-send-pf.feature}, lato destinatario Persona Fisica.
 * Si veda {@link WebMittenteNavigationContractTest} per il razionale (scenario no-op: il
 * framework naviga e chiama {@code assertLoaded()} prima ancora di eseguirlo).
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
public class WebRecipientPfNavigationContractTest {

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Richiede un utente che abbia ricevuto almeno una notifica (Lucrezia su test).
     */
    @TestFactory
    Stream<DynamicTest> shouldReachNotificationPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "controllo tabella notifiche",
                        page -> {},
                        page -> page.notificationsTable().assertLoaded()
                )));
    }

    /**
     * Apre il dettaglio della prima notifica in lista: richiede un utente che abbia ricevuto almeno una notifica (Lucrezia su test).
     */
    @TestFactory
    Stream<DynamicTest> shouldReachNotificationDetailsPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "controllo dettaglio prima notifica",
                        NotificationPFPage::goToNotificationDetails,
                        page -> page.notificationDetails().assertLoaded()
                )));
    }

    /**
     * Richiede un utente con PEC, recapiti personalizzati per ente, email e cellulare già configurati (Lucrezia su test).
     */
    @TestFactory
    Stream<DynamicTest> shouldReachAddressPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "controllo recapiti configurati",
                        page -> {},
                        page -> {
                            page.pecContact().assertLoaded();
                            page.specialContacts().assertLoaded();
                            page.emailContact().assertLoaded();
                            page.smsContact().assertLoaded();
                        }
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldReachDelegationsPF() {
        return reachabilityTest(DelegationsPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachNewDelegationPF() {
        return reachabilityTest(NewDelegationPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachDigitalDomicileActivationPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "controllo passi wizard attivazione domicilio digitale",
                        page -> {
                            page.continueButton().click();
                            page.emailStep().assertLoaded();
                            page.emailStep().continueButton().click();
                        },
                        page -> page.summaryStepSection().assertLoaded()
                )));
    }

    /**
     * Richiede un utente con una PEC attiva come domicilio digitale (Lucrezia su test).
     */
    @TestFactory
    Stream<DynamicTest> shouldReachDigitalDomicileManagementPF() {
        return reachabilityTest(DigitalDomicileManagementPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachAppStatusPF() {
        return reachabilityTest(AppStatusPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachSupportPF() {
        return reachabilityTest(SupportPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachProfilePF() {
        return reachabilityTest(ProfilePFPage.class);
    }

    /**
     * Apre la timeline della prima notifica a valore legale in lista: richiede un utente che abbia ricevuto almeno una notifica a valore legale (Lucrezia su test).
     */
    @TestFactory
    Stream<DynamicTest> shouldReachNotificationTimelinePF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "controllo timeline prima notifica a valore legale",
                        page -> {
                            page.firstLegalNotificationDetailsButton().click();
                            page.notificationDetails().timelineDetailsButton().click();
                        },
                        page -> page.notificationDetails().notificationTimeline().assertLoaded()
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingPF() {
        return reachabilityTest(ConfigureAddressSendPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingDigitalDomicilePF() {
        return reachabilityTest(OnboardingDigitalDomicilePFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingAlertsPF() {
        return reachabilityTest(OnboardingAlertsPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingIoPF() {
        return reachabilityTest(OnboardingIoPFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachTermsOfServicePF() {
        return reachabilityTest(TermsOfServicePFPage.class);
    }

    @TestFactory
    Stream<DynamicTest> shouldReachSercqTermsOfServicePF() {
        return reachabilityTest(SercqTermsOfServicePFPage.class);
    }

    private <P extends Page> Stream<DynamicTest> reachabilityTest(Class<P> pageType) {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(pageType)
                .tests(Stream.of(new WebScenario<>(
                        "controllo caricamento pagina " + pageType.getSimpleName(),
                        page -> {},
                        page -> {}
                )));
    }
}
