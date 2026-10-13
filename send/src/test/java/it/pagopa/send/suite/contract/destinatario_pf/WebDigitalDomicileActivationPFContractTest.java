package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.DigitalDomicileActivationScenarios;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DigitalDomicileActivationPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
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
 * Contract test del wizard "Attiva domicilio digitale su SEND" del cittadino
 * ({@code {baseUrl}/recapiti/domicilio-digitale/attivazione}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi comuni a tutti i passi;</li>
 *     <li>un gruppo per ogni passo del wizard, con testi e pulsanti del passo;</li>
 *     <li>messaggi di validazione della modifica di email e cellulare al secondo passo.</li>
 * </ul>
 * Gli scenari comuni con il wizard dell'impresa sono in {@link DigitalDomicileActivationScenarios}.
 * Il contenuto del secondo passo e del riepilogo dipende dai recapiti di cortesia dell'utente: ogni scenario lo verifica
 * se presente. Nessuno scenario preme "Conferma", che attiva il domicilio digitale; gli scenari di validazione usano
 * solo valori non validi anche senza spazi.
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
public class WebDigitalDomicileActivationPFContractTest {

    // testi attesi

    private static final String EMAIL_LABEL = "Indirizzo email";
    private static final String SMS_LABEL = "Numero di cellulare";
    private static final DigitalDomicileActivationScenarios.Texts TEXTS = new DigitalDomicileActivationScenarios.Texts(
            List.of("Come funziona", "Inserisci la tua email", "Riepilogo"),
            List.of("Un ente ti invia una notifica su SEND", "Ricevi un messaggio", "Accedi alla notifica"),
            List.of(
                    "La notifica ti viene consegnata in modo sicuro e con valore legale tramite la piattaforma",
                    "Quando ti arriva una notifica su SEND, ti avvisiamo tramite email e sui canali che preferisci",
                    "Apri il dettaglio della notifica digitale sulla piattaforma SEND o, se hai attivato il servizio, direttamente dall’app IO"
            ),
            "La tua email per ricevere avvisi sulle notifiche SEND",
            "L’email dove ti avviseremo quando ricevi una comunicazione a valore legale su SEND.",
            EMAIL_LABEL,
            SMS_LABEL
    );

    // passo 1
    private static final String PEC_REPLACEMENT_INFO = "La piattaforma SEND sostituirà la PEC come tuo domicilio digitale.";

    // passo 3
    private static final String SUMMARY_TITLE = "Il tuo riepilogo";
    private static final String LEGAL_DELIVERY_LABEL = "Le tue comunicazioni a valore legale saranno recapitate solo su:";
    private static final String DIGITAL_DOMICILE_LABEL = "Domicilio digitale";
    private static final String DIGITAL_DOMICILE_VALUE = "SEND";
    private static final String ALERTS_LABEL = "Riceverai un avviso via:";
    private static final String SUMMARY_EMAIL_TYPE = "Email";
    private static final String DISCLAIMER = "Premendo Conferma dichiari di aver letto l’Informativa Privacy, di accettare i Termini del servizio, e di aver compreso che le notifiche SEND non ti verranno inoltrate su altri domicili digitali.";
    private static final String PRIVACY_LINK = "Informativa Privacy";
    private static final String TOS_LINK = "Termini del servizio";
    // in questo wizard il portale usa l'apostrofo dritto
    private static final String MONITOR_ALERT = "Monitora i recapiti che hai scelto: una notifica digitale SEND inizia a produrre effetti giuridici anche se non l'hai consultata.";

    private final WebBrowserContractValidator webContractValidator;

    // testi comuni a tutti i passi

    @TestFactory
    Stream<DynamicTest> shouldShowActivationWizardTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(DigitalDomicileActivationScenarios.texts(TEXTS));
    }

    // passo 1: come funziona

    @TestFactory
    Stream<DynamicTest> shouldShowHowItWorksStep() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(Stream.concat(
                        DigitalDomicileActivationScenarios.howItWorksStep(TEXTS),
                        Stream.of(
                                new WebScenario<>(
                                        "se l'utente ha già una PEC, avviso che SEND la sostituirà",
                                        page -> {},
                                        page -> {
                                            if (!page.content().read().contains(PEC_REPLACEMENT_INFO)) {
                                                // L'utente non ha una PEC come domicilio digitale: l'avviso non c'è, quindi il test termina
                                                return;
                                            }
                                            page.pecReplacementInfo().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_REPLACEMENT_INFO));
                                        }
                                )
                        )
                ));
    }

    // passo 2: inserisci la tua email

    @TestFactory
    Stream<DynamicTest> shouldShowEmailStep() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(DigitalDomicileActivationScenarios.emailStep(TEXTS));
    }

    // passo 3: riepilogo ("Conferma" non viene mai premuto)

    @TestFactory
    Stream<DynamicTest> shouldShowSummaryStep() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se raggiungibile, riepilogo con domicilio SEND, recapiti, disclaimer e conferma non premuta",
                                page -> {},
                                page -> {
                                    if (!goToSummary(page)) {
                                        // L'utente non ha un'email di cortesia: il riepilogo non è raggiungibile senza inserirla
                                        return;
                                    }
                                    DigitalDomicileActivationPFPage.SummarySection section = page.summarySection();
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUMMARY_TITLE));
                                    section.legalDeliveryLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LEGAL_DELIVERY_LABEL));
                                    section.digitalDomicileLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DIGITAL_DOMICILE_LABEL));
                                    section.digitalDomicileValue().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DIGITAL_DOMICILE_VALUE));
                                    section.alertsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ALERTS_LABEL));
                                    section.contactTypes().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(t -> Assertions.assertThat(t).isIn(SUMMARY_EMAIL_TYPE, SMS_LABEL)));
                                    int contacts = section.contactTypes().readAll().size();
                                    section.contactValues().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(contacts).allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                                    section.disclaimer().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DISCLAIMER));
                                    section.monitorAlert().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MONITOR_ALERT));
                                    section.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DigitalDomicileActivationScenarios.BACK));
                                    Assertions.assertThat(section.activateButton().get(FindPolicy.PRESENT).map(b -> b.getText())).hasValue(DigitalDomicileActivationScenarios.CONFIRM);
                                }
                        ),

                        new WebScenario<>(
                                "se raggiungibile, link a informativa privacy e termini del servizio SERCQ in una nuova scheda",
                                page -> {},
                                page -> {
                                    if (!goToSummary(page)) {
                                        // L'utente non ha un'email di cortesia: il riepilogo non è raggiungibile senza inserirla
                                        return;
                                    }
                                    DigitalDomicileActivationPFPage.SummarySection section = page.summarySection();
                                    section.privacyLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PRIVACY_LINK));
                                    section.tosLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TOS_LINK));
                                    Assertions.assertThat(section.privacyLink().get(FindPolicy.PRESENT).map(a -> a.getAttributes().get("href")))
                                            .hasValueSatisfying(href -> Assertions.assertThat(href).endsWith("/informativa-privacy"));
                                    Assertions.assertThat(section.tosLink().get(FindPolicy.PRESENT).map(a -> a.getAttributes().get("href")))
                                            .hasValueSatisfying(href -> Assertions.assertThat(href).endsWith("/termini-di-servizio/sercq-send"));
                                }
                        )
                ));
    }

    // messaggi di validazione della modifica di email e cellulare al passo 2: solo valori non validi

    @TestFactory
    Stream<DynamicTest> shouldValidateEmailStep() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(DigitalDomicileActivationScenarios.emailStepValidations(TEXTS));
    }

    // va al riepilogo con "Continua"; restituisce false se il secondo passo non permette di proseguire
    private boolean goToSummary(DigitalDomicileActivationPFPage page) {
        page.continueButton().click();
        DigitalDomicileActivationPFPage.EmailSection section = page.emailSection();
        if (!section.content().read().contains(DigitalDomicileActivationScenarios.MODIFY)) {
            return false;
        }
        section.continueButton().click();
        return true;
    }
}
