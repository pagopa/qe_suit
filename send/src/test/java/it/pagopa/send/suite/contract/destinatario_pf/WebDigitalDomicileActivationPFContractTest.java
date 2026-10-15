package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DigitalDomicileActivationPFPage;
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
 * Contract test del wizard "Attiva domicilio digitale su SEND" del cittadino
 * ({@code {baseUrl}/recapiti/domicilio-digitale/attivazione}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi comuni a tutti i passi;</li>
 *     <li>un gruppo per ogni passo del wizard, con testi e pulsanti del passo.</li>
 * </ul>
 * Il contenuto del secondo passo dipende dai recapiti di cortesia dell'utente (recapiti con "Modifica" oppure campi per
 * aggiungerli) e non viene verificato, come l'avviso sulla PEC del primo passo. Il riepilogo si raggiunge solo se
 * l'utente ha un'email di cortesia, quindi non viene verificato.
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

    private static final String TITLE = "Attiva domicilio digitale su SEND";
    private static final List<String> STEPS = List.of("Come funziona", "Inserisci la tua email", "Riepilogo");
    private static final String CONTINUE = "Continua";
    private static final String CANCEL = "Annulla";
    private static final String BACK = "Indietro";

    // passo 1
    private static final String HOW_IT_WORKS = "Come funziona";
    private static final List<String> INFO_TITLES = List.of("Un ente ti invia una notifica su SEND", "Ricevi un messaggio", "Accedi alla notifica");
    private static final List<String> INFO_DESCRIPTIONS = List.of(
            "La notifica ti viene consegnata in modo sicuro e con valore legale tramite la piattaforma",
            "Quando ti arriva una notifica su SEND, ti avvisiamo tramite email e sui canali che preferisci",
            "Apri il dettaglio della notifica digitale sulla piattaforma SEND o, se hai attivato il servizio, direttamente dall’app IO"
    );
    private static final String DELIVERED = "consegnata";
    private static final String DELIVERED_DIALOG_TITLE = "Valore giuridico della notifica sul domicilio digitale SEND";
    private static final String DELIVERED_DIALOG_DESCRIPTION = "Se scegli SEND come domicilio digitale, dopo 7 giorni dalla consegna la notifica risulterà legalmente recapitata, anche se non l’hai consultata.";
    private static final String UNDERSTAND = "Ok, ho capito";

    // passo 2
    private static final String EMAIL_STEP_TITLE = "La tua email per ricevere avvisi sulle notifiche SEND";
    private static final String EMAIL_STEP_DESCRIPTION = "L’email dove ti avviseremo quando ricevi una comunicazione a valore legale su SEND.";

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Testi comuni a tutti i passi: titolo del wizard e nomi dei passi "Come funziona", "Inserisci la tua email" e
     * "Riepilogo".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowActivationWizardTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo e nomi dei passi",
                                page -> {},
                                page -> {
                                    page.wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    // il passo corrente mostra il numero davanti al nome, quindi il nome si confronta senza il numero
                                    page.howItWorksProgressLabel().readAndAssert(h -> Assertions.assertThat(h.replaceFirst("^\\d+\\s*", "")).isEqualTo(STEPS.get(0)));
                                    page.insertEmailProgressLabel().readAndAssert(h -> Assertions.assertThat(h.replaceFirst("^\\d+\\s*", "")).isEqualTo(STEPS.get(1)));
                                    page.summaryProgressLabel().readAndAssert(h -> Assertions.assertThat(h.replaceFirst("^\\d+\\s*", "")).isEqualTo(STEPS.get(2)));
                                }
                        )
                ));
    }

    /**
     * Passo 1, "Come funziona": titolo, i tre punti con titolo e descrizione, "Continua" e "Annulla", di cui si
     * controlla solo il testo perché torna alla pagina precedente nella cronologia del browser. Il link "consegnata"
     * apre la finestra sul valore giuridico della notifica, con titolo, descrizione e "Ok, ho capito".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowHowItWorksStep() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "passo come funziona con i tre punti, continua e annulla",
                                page -> {},
                                page -> {
                                    page.howItWorksTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(HOW_IT_WORKS));
                                    page.infoTitles().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(INFO_TITLES));
                                    page.infoDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(INFO_DESCRIPTIONS));
                                    page.deliveredLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELIVERED));
                                    page.continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONTINUE));
                                    // "Annulla" torna alla pagina precedente nella cronologia, che dipende da come si arriva qui
                                    page.cancelButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CANCEL));
                                }
                        ),


                        new WebScenario<>(
                                "consegnata apre la finestra sul valore giuridico della notifica",
                                page -> page.deliveredLink().click(),
                                page -> {
                                    DigitalDomicileActivationPFPage.DeliveredDialog dialog = page.deliveredDialog();
                                    dialog.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELIVERED_DIALOG_TITLE));
                                    dialog.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELIVERED_DIALOG_DESCRIPTION));
                                    dialog.understandButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(UNDERSTAND));
                                }
                        )
                ));
    }

    /**
     * Passo 2, "Inserisci la tua email": titolo, descrizione, "Continua" e "Indietro".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowEmailStep() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "passo email: titolo, descrizione, continua e indietro",
                                page -> page.continueButton().click(),
                                page -> {
                                    DigitalDomicileActivationPFPage.EmailSection section = page.emailSection();
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_STEP_TITLE));
                                    section.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_STEP_DESCRIPTION));
                                    section.continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONTINUE));
                                    section.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                }
                        )
                ));
    }
}
