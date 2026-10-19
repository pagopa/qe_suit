package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.domain.Page;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
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
import static org.assertj.core.api.Assertions.assertThat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import it.pagopa.send.web.infrastructure.page.ConfigureAddressSendPage;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
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
@ActiveProfiles({"hotfix", "junit"})
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
})
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@Slf4j
public class WebRecipientPfNavigationContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    // notifiche

    /**
     * "In arrivo" ({@code /notifiche}): {@code assertLoaded()} controlla il titolo e i campi dei filtri.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachNotificationListPF() {
        return reachabilityTest(NotificationPFPage.class);
    }

    // recapiti

    /**
     * "I tuoi recapiti" ({@code /recapiti}): {@code assertLoaded()} controlla il titolo e le card del domicilio
     * digitale, dell'app IO e dell'email.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachAddressPF() {
        return reachabilityTest(AddressPFPage.class);
    }

    /**
     * "Attiva domicilio digitale su SEND" ({@code /recapiti/domicilio-digitale/attivazione}): {@code assertLoaded()}
     * controlla il titolo del wizard, i tre passi, "Continua" e "Annulla".
     */
    @TestFactory
    Stream<DynamicTest> shouldReachDigitalDomicileActivationPF() {
        return reachabilityTest(DigitalDomicileActivationPFPage.class);
    }

    // deleghe

    /**
     * "Deleghe" ({@code /deleghe}): {@code assertLoaded()} controlla il titolo, "Aggiungi una delega" e le due sezioni.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachDelegationsPF() {
        return reachabilityTest(DelegationsPFPage.class);
    }

    /**
     * "Aggiungi una delega" ({@code /deleghe/nuova}): {@code assertLoaded()} controlla il titolo, i campi e i pulsanti
     * del form.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachNewDelegationPF() {
        return reachabilityTest(NewDelegationPFPage.class);
    }

    // onboarding

    /**
     * "Configura SEND" ({@code /onboarding}): {@code assertLoaded()} controlla il titolo, i pulsanti delle due card
     * mostrate a tutti e "Salta e vai alle tue notifiche".
     */
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingPF() {
        return reachabilityTest(ConfigureAddressSendPage.class);
    }

    /**
     * "Il meglio di SEND" ({@code /onboarding/domicilio-digitale}): {@code assertLoaded()} controlla il titolo, "Esci"
     * e i nomi dei quattro passi.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingDigitalDomicilePF() {
        return reachabilityTest(OnboardingDigitalDomicilePFPage.class);
    }

    /**
     * "Attivazione avvisi" ({@code /onboarding/avvisi}): verifica tutti i passi del wizard, ognuno con un contenuto,
     * senza premere "Conferma".
     */
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingAlertsPF() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.USER_1))
                .on(OnboardingAlertsPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "controllo passi wizard Attivazione avvisi",
                        page -> {},
                        page -> {
                            // ogni passo si raggiunge con "Indietro" e "Avanti", senza fare scelte
                            for (String section : page.progressItems().readAll()) {
                                assertThat(page.goToSection(section)).as("passo %s raggiunto", section).isTrue();
                                page.currentSection().assertLoaded();
                            }
                        }
                )));
    }

    /**
     * "Tutto, sull'app IO" ({@code /onboarding/io}): {@code assertLoaded()} controlla il titolo, "Esci", "Scarica l'app
     * IO" e "Ho già scaricato e installato l'app".
     */
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingIoPF() {
        return reachabilityTest(OnboardingIoPFPage.class);
    }

    // altre pagine

    /**
     * "Stato della piattaforma" ({@code /app-status}): {@code assertLoaded()} controlla il titolo, lo stato attuale e
     * l'ultimo aggiornamento.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachAppStatusPF() {
        return reachabilityTest(AppStatusPFPage.class);
    }

    /**
     * "Assistenza" ({@code /assistenza}): {@code assertLoaded()} controlla il titolo, i campi e i pulsanti del form.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachSupportPF() {
        return reachabilityTest(SupportPFPage.class);
    }

    /**
     * "I tuoi dati" ({@code /profilo}): {@code assertLoaded()} controlla il titolo e le tre righe dei dati.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachProfilePF() {
        return reachabilityTest(ProfilePFPage.class);
    }

    /**
     * "Termini di servizio" ({@code /termini-di-servizio}): {@code assertLoaded()} controlla il titolo e la presenza di
     * almeno una sezione.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachTermsOfServicePF() {
        return reachabilityTest(TermsOfServicePFPage.class);
    }

    /**
     * "Termini di servizio SERCQ" ({@code /termini-di-servizio/sercq-send}): {@code assertLoaded()} controlla il titolo
     * e la presenza di almeno una sezione.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachSercqTermsOfServicePF() {
        return reachabilityTest(SercqTermsOfServicePFPage.class);
    }

    private <P extends Page> Stream<DynamicTest> reachabilityTest(Class<P> pageType) {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.USER_1))
                .on(pageType)
                .tests(Stream.of(new WebScenario<>(
                        "controllo caricamento pagina " + pageType.getSimpleName(),
                        page -> {},
                        page -> {}
                )));
    }
}
