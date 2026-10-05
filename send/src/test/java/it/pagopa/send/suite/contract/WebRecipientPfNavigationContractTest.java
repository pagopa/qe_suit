package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
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

    // notifiche

    // In arrivo
    @TestFactory
    Stream<DynamicTest> shouldReachNotificationListPF() {
        return reachabilityTest(NotificationPFPage.class);
    }

    // Dettaglio notifica
    /**
     * Se presente, apre il dettaglio della prima notifica in lista: se l'utente non ha notifiche la pagina non è raggiungibile
     * e il test non verifica nulla.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachNotificationDetailsPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "se presente, verifica dettaglio prima notifica",
                        page -> {},
                        page -> {
                            if (page.notificationsTable().openButtons().read() == null) {
                                // Nessuna notifica ricevuta: il dettaglio non è raggiungibile, quindi il test termina senza verificarlo
                                return;
                            }
                            page.goToNotificationDetails();
                            page.notificationDetails().assertLoaded();
                        }
                )));
    }

    // Stato della notifica (timeline)
    /**
     * Se presente, apre la timeline della prima notifica a valore legale, filtrando la lista per tipologia: se l'utente non ha
     * notifiche a valore legale la pagina non è raggiungibile e il test non verifica nulla.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachNotificationTimelinePF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NotificationPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "se presente, controllo timeline prima notifica a valore legale",
                        NotificationPFPage::filterLegalNotifications,  // la timeline esiste solo per le notifiche a valore legale: si filtra la lista su quelle
                        page -> {
                            if (page.firstLegalNotificationDetailsButton().get(FindPolicy.PRESENT).isEmpty()) {
                                // Nessuna notifica a valore legale: la timeline non è raggiungibile, quindi il test termina senza verificarla
                                return;
                            }
                            page.firstLegalNotificationDetailsButton().click();
                            page.notificationDetails().timelineDetailsButton().click();
                            page.notificationDetails().notificationTimeline().assertLoaded();  // controlli della pagina timeline
                        }
                )));
    }

    // recapiti

    // I tuoi recapiti
    @TestFactory
    Stream<DynamicTest> shouldReachAddressPF() {
        return reachabilityTest(AddressPFPage.class);
    }

    // Attiva domicilio digitale su SEND
    /**
     * Se presente, percorre il wizard di attivazione fino al riepilogo senza mai premere "Conferma": se l'utente non ha ancora
     * un'email il secondo passo chiede di inserirla e il test non prosegue oltre.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachDigitalDomicileActivationPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "se presente, controllo riepilogo wizard attivazione domicilio digitale",
                        page -> {},
                        page -> {
                            page.continueButton().click();
                            if (!"Continua".equals(page.emailSection().continueButton().read())) {
                                // Email non ancora inserita, quindi il test termina senza verificare il riepilogo
                                return;
                            }
                            page.emailSection().assertLoaded();
                            page.emailSection().continueButton().click();
                            page.summarySection().assertLoaded();
                        }
                )));
    }

    // Gestisci il tuo domicilio digitale
    /**
     * Se presente, apre la gestione del domicilio digitale da "Gestisci" nei recapiti: se l'utente non ha un domicilio digitale
     * attivo la pagina non è raggiungibile e il test non verifica nulla.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachDigitalDomicileManagementPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "se presente, controllo gestione domicilio digitale",
                        page -> {},
                        page -> {
                            if (page.pecContact().manageButton().get(FindPolicy.PRESENT).isEmpty()) {
                                // Nessun domicilio digitale attivo: la gestione non è raggiungibile, quindi il test termina senza verificarla
                                return;
                            }
                            page.pecContact().manageButton().click();
                            page.digitalDomicileManagement().assertLoaded();
                        }
                )));
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
    /**
     * Se presenti, verifica tutti i passi del wizard: quello di apertura e ciascuno di quelli raggiunti con il pulsante avanti,
     * fermandosi sull'ultimo senza premere "Conferma" o prima, su un passo che richiede una scelta.
     */
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingDigitalDomicilePF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingDigitalDomicilePFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "se presenti, controllo passi wizard Il meglio di SEND",
                        page -> {},
                        page -> {
                            int sections = page.progressItems().readAll().size();
                            page.currentSection().assertLoaded();  // passo di apertura
                            for (int i = 1; i < sections && page.canGoNext(); i++) {
                                page.next();
                                page.currentSection().assertLoaded();  // passo raggiunto con il pulsante avanti
                            }
                        }
                )));
    }

    // Onboarding: Attivazione avvisi
    /**
     * Se presenti, verifica tutti i passi del wizard: quello di apertura e ciascuno di quelli raggiunti con il pulsante avanti,
     * fermandosi sull'ultimo senza premere "Conferma".
     */
    @TestFactory
    Stream<DynamicTest> shouldReachOnboardingAlertsPF() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(OnboardingAlertsPFPage.class)
                .tests(Stream.of(new WebScenario<>(
                        "se presenti, controllo passi wizard Attivazione avvisi",
                        page -> {},
                        page -> {
                            int sections = page.progressItems().readAll().size();
                            page.currentSection().assertLoaded();  // passo di apertura
                            for (int i = 1; i < sections && page.canGoNext(); i++) {
                                page.next();
                                page.currentSection().assertLoaded();  // passo raggiunto con il pulsante avanti
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
