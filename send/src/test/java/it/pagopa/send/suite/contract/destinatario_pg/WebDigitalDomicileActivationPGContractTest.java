package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.DigitalDomicileActivationScenarios;
import it.pagopa.send.web.address.infrastructure.suit.DigitalDomicileActivationPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.DigitalDomicileActivationPGPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
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
 * Contract test del wizard "Attiva domicilio digitale su SEND" dell'impresa
 * ({@code {baseUrl}/recapiti/domicilio-digitale/attivazione}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi comuni a tutti i passi;</li>
 *     <li>un gruppo per ogni passo del wizard, con testi e pulsanti del passo;</li>
 *     <li>messaggi di validazione di email e cellulare al secondo passo;</li>
 *     <li>finestra del codice di verifica aperta aggiungendo l'email.</li>
 * </ul>
 * Gli scenari comuni con il wizard del cittadino sono in {@link DigitalDomicileActivationScenarios}.
 * Il contenuto del secondo passo dipende dai recapiti di cortesia dell'impresa: ogni scenario lo verifica se presente.
 * Nessuno scenario preme "Conferma" o "Inserisci PEC"; le validazioni usano solo valori non validi anche senza spazi.
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
public class WebDigitalDomicileActivationPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // testi attesi

    // le etichette sono quelle dei campi per aggiungere i recapiti, gli unici visti con Petrarca
    private static final DigitalDomicileActivationScenarios.Texts TEXTS = new DigitalDomicileActivationScenarios.Texts(
            List.of("Come funziona", "Inserisci l'email aziendale", "Riepilogo"),
            List.of("Un ente invia una notifica SEND per l’impresa", "Ricevi un messaggio", "Accedi alla notifica"),
            List.of(
                    "La notifica viene consegnata in modo sicuro e con valore legale tramite la piattaforma",
                    "Quando arriva una notifica su SEND per la tua impresa, ti avvisiamo tramite email e, se lo desideri, anche via SMS",
                    "Apri il dettaglio della notifica digitale sulla piattaforma SEND"
            ),
            "L’email per ricevere avvisi sulle notifiche SEND",
            "L’email aziendale dove avviseremo l’impresa quando c’è una nuova comunicazione a valore legale su SEND.",
            "Email aziendale",
            "Cellulare aziendale"
    );

    // passo 1
    private static final String PEC_SECTION_TITLE = "Preferisci usare la PEC aziendale?";
    private static final String PEC_SECTION_DESCRIPTION = "In alternativa, puoi usare una PEC come domicilio digitale per le notifiche di SEND.";
    private static final String INSERT_PEC = "Inserisci PEC";

    // passo 2
    private static final String SMS_TITLE = "Numero di cellulare";
    private static final String SMS_DESCRIPTION = "Se premi Aggiungi numero salvi questo recapito per ricevere avvisi sulle notifiche SEND per l’impresa. Puoi gestirlo nella sezione Recapiti.";
    private static final String ADD_NUMBER = "Aggiungi numero";

    // dati di prova: recapiti validi, che fanno inviare un codice di verifica e non vengono mai salvati
    private static final String CODE_EMAIL = "provaemail@test.it";
    private static final String CODE_SMS = "348000000";

    private final WebBrowserContractValidator webContractValidator;

    // testi comuni a tutti i passi

    @TestFactory
    Stream<DynamicTest> shouldShowActivationWizardTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(DigitalDomicileActivationPGPage.class)
                .tests(DigitalDomicileActivationScenarios.texts(TEXTS));
    }

    // passo 1: come funziona

    @TestFactory
    Stream<DynamicTest> shouldShowHowItWorksStep() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(DigitalDomicileActivationPGPage.class)
                .tests(Stream.concat(
                        DigitalDomicileActivationScenarios.howItWorksStep(TEXTS),
                        Stream.of(
                                new WebScenario<>(
                                        "sezione per usare una PEC aziendale, con inserisci PEC non premuto",
                                        page -> {},
                                        page -> {
                                            page.pecSectionTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_SECTION_TITLE));
                                            page.pecSectionDescription().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_SECTION_DESCRIPTION));
                                            page.insertPecButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(INSERT_PEC));
                                        }
                                )
                        )
                ));
    }

    // passo 2: inserisci l'email aziendale

    @TestFactory
    Stream<DynamicTest> shouldShowEmailStep() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(DigitalDomicileActivationPGPage.class)
                .tests(Stream.concat(
                        DigitalDomicileActivationScenarios.emailStep(TEXTS),
                        Stream.of(
                                new WebScenario<>(
                                        "se il cellulare è da aggiungere, aggiungi numero di cellulare apre il campo",
                                        page -> page.continueButton().click(),
                                        page -> {
                                            DigitalDomicileActivationPage.EmailSection section = page.emailSection();
                                            if (!section.content().read().contains(DigitalDomicileActivationScenarios.ADD_SMS)) {
                                                // L'impresa ha già un cellulare: il pulsante per aggiungerlo non c'è, quindi il test termina
                                                return;
                                            }
                                            section.addSmsButton().click();
                                            section.smsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_TITLE));
                                            section.smsDescription().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_DESCRIPTION));
                                            section.smsInputLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TEXTS.smsLabel()));
                                            Assertions.assertThat(section.smsInput().read()).isEmpty();
                                            section.addSmsSaveButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_NUMBER));
                                            section.cancelSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DigitalDomicileActivationScenarios.CANCEL));
                                        }
                                )
                        )
                ));
    }

    // passo 3: riepilogo
    // TODO verificare il riepilogo quando ci sarà un utente PG di test con un'email aziendale e senza domicilio digitale:
    //  al 13/10/2026 Petrarca non ha un'email aziendale e il riepilogo non si raggiunge senza salvarne una

    // messaggi di validazione di email e cellulare al passo 2: solo valori non validi

    @TestFactory
    Stream<DynamicTest> shouldValidateEmailStep() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(DigitalDomicileActivationPGPage.class)
                .tests(DigitalDomicileActivationScenarios.emailStepValidations(TEXTS));
    }

    // finestra del codice di verifica: il codice non viene mai scritto e "Conferma" non viene mai premuto

    @TestFactory
    Stream<DynamicTest> shouldShowEmailCodeDialog() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(DigitalDomicileActivationPGPage.class)
                .tests(DigitalDomicileActivationScenarios.emailCodeDialog(TEXTS, CODE_EMAIL));
    }

    // TODO riattivare quando ci sarà un numero di cellulare di test: ogni esecuzione manda due SMS a CODE_SMS, che potrebbe
    //  essere assegnato a una persona. Gli scenari sono stati verificati il 13/10/2026 e la finestra è uguale a quella
    //  dell'email, con i testi del cellulare
    // @TestFactory
    // Stream<DynamicTest> shouldShowSmsCodeDialog() {
    //     return webContractValidator.asRecipient(RECIPIENT)
    //             .on(DigitalDomicileActivationPGPage.class)
    //             .tests(DigitalDomicileActivationScenarios.smsCodeDialog(TEXTS, CODE_SMS));
    // }
}
