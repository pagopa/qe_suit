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
import org.assertj.core.api.Assertions;
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
 * <p>
 * Le pagine che esistono solo se l'utente ha dei dati (dettaglio e timeline di una notifica, riepilogo del wizard di
 * attivazione, gestione del domicilio digitale, passi 2-4 di "Il meglio di SEND") non sono verificate.
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

    // notifiche

    // In arrivo
    @TestFactory
    Stream<DynamicTest> shouldReachNotificationListPF() {
        return reachabilityTest(NotificationPFPage.class);
    }

    // recapiti

    // I tuoi recapiti
    @TestFactory
    Stream<DynamicTest> shouldReachAddressPF() {
        return reachabilityTest(AddressPFPage.class);
    }

    // Attiva domicilio digitale su SEND
    @TestFactory
    Stream<DynamicTest> shouldReachDigitalDomicileActivationPF() {
        return reachabilityTest(DigitalDomicileActivationPFPage.class);
    }

    // deleghe

    // Deleghe
    @TestFactory
    Stream<DynamicTest> shouldReachDelegationsPF() {
        return reachabilityTest(DelegationsPFPage.class);
    }

    // Aggiungi una delega
    @TestFactory
    Stream<DynamicTest> shouldReachNewDelegationPF() {
        return reachabilityTest(NewDelegationPFPage.class);
    }

    // onboarding

    // Configura SEND (onboarding)
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingPF() {
        return reachabilityTest(ConfigureAddressSendPage.class);
    }

    // Onboarding: Il meglio di SEND
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingDigitalDomicilePF() {
        return reachabilityTest(OnboardingDigitalDomicilePFPage.class);
    }

    // Onboarding: Attivazione avvisi
    /**
     * Verifica tutti i passi del wizard senza premere "Conferma".
     */
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingAlertsPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingAlertsPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "controllo passi wizard Attivazione avvisi",
                        page -> {},
                        page -> {
                            // ogni passo si raggiunge con "Indietro" e "Avanti", senza fare scelte
                            for (String section : page.progressItems().readAll()) {
                                Assertions.assertThat(page.goToSection(section)).as("passo %s raggiunto", section).isTrue();
                                page.currentSection().assertLoaded();
                            }
                        }
                )));
    }

    // Onboarding: Tutto, sull'app IO
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingIoPF() {
        return reachabilityTest(OnboardingIoPFPage.class);
    }

    // altre pagine

    // Stato della piattaforma
    @TestFactory
    Stream<DynamicTest> shouldReachAppStatusPF() {
        return reachabilityTest(AppStatusPFPage.class);
    }

    // Assistenza
    @TestFactory
    Stream<DynamicTest> shouldReachSupportPF() {
        return reachabilityTest(SupportPFPage.class);
    }

    // I tuoi dati
    @TestFactory
    Stream<DynamicTest> shouldReachProfilePF() {
        return reachabilityTest(ProfilePFPage.class);
    }

    // Termini di servizio
    @TestFactory
    Stream<DynamicTest> shouldReachTermsOfServicePF() {
        return reachabilityTest(TermsOfServicePFPage.class);
    }

    // Termini di servizio SERCQ
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
