package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DigitalDomicileActivationPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.Keys;
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

    private static final String TITLE = "Attiva domicilio digitale su SEND";
    private static final List<String> STEPS = List.of("Come funziona", "Inserisci la tua email", "Riepilogo");
    private static final String CONTINUE = "Continua";
    private static final String CANCEL = "Annulla";
    private static final String BACK = "Indietro";
    private static final String MODIFY = "Modifica";

    // passo 1
    private static final String HOW_IT_WORKS = "Come funziona";
    private static final List<String> INFO_TITLES = List.of("Un ente ti invia una notifica su SEND", "Ricevi un messaggio", "Accedi alla notifica");
    private static final List<String> INFO_DESCRIPTIONS = List.of(
            "La notifica ti viene consegnata in modo sicuro e con valore legale tramite la piattaforma",
            "Quando ti arriva una notifica su SEND, ti avvisiamo tramite email e sui canali che preferisci",
            "Apri il dettaglio della notifica digitale sulla piattaforma SEND o, se hai attivato il servizio, direttamente dall’app IO"
    );
    private static final String DELIVERED = "consegnata";
    private static final String PEC_REPLACEMENT_INFO = "La piattaforma SEND sostituirà la PEC come tuo domicilio digitale.";
    private static final String DELIVERED_DIALOG_TITLE = "Valore giuridico della notifica sul domicilio digitale SEND";
    private static final String DELIVERED_DIALOG_DESCRIPTION = "Se scegli SEND come domicilio digitale, dopo 7 giorni dalla consegna la notifica risulterà legalmente recapitata, anche se non l’hai consultata.";
    private static final String UNDERSTAND = "Ok, ho capito";

    // passo 2
    private static final String EMAIL_STEP_TITLE = "La tua email per ricevere avvisi sulle notifiche SEND";
    private static final String EMAIL_STEP_DESCRIPTION = "L’email dove ti avviseremo quando ricevi una comunicazione a valore legale su SEND.";
    private static final String EMAIL_LABEL = "Indirizzo email";
    private static final String SMS_LABEL = "Numero di cellulare";
    private static final String ADD_EMAIL = "Aggiungi email";
    private static final String SMS_QUESTION = "Vuoi ricevere gli avvisi anche via SMS?";
    private static final String ADD_SMS = "Aggiungi numero di cellulare";

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
    private static final String CONFIRM = "Conferma";

    // messaggi di validazione
    private static final String INVALID_EMAIL_MESSAGE = "Indirizzo email non valido";
    private static final String INVALID_SMS_MESSAGE = "Numero di cellulare non valido";

    // dati di prova: non validi anche senza spazi, perché un valore valido avvierebbe l'invio del codice di verifica

    private static final String INVALID_EMAIL = "abc";
    private static final String INVALID_SMS = "123";

    private final WebBrowserContractValidator webContractValidator;

    // testi comuni a tutti i passi

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

    // passo 1: come funziona

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
                                "se l'utente ha già una PEC, avviso che SEND la sostituirà",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(PEC_REPLACEMENT_INFO)) {
                                        // L'utente non ha una PEC come domicilio digitale: l'avviso non c'è, quindi il test termina
                                        return;
                                    }
                                    page.pecReplacementInfo().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_REPLACEMENT_INFO));
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

    // passo 2: inserisci la tua email

    @TestFactory
    Stream<DynamicTest> shouldShowEmailStep() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileActivationPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "passo email: titolo, descrizione, indietro e, se presenti, email e cellulare con modifica",
                                page -> page.continueButton().click(),
                                page -> {
                                    DigitalDomicileActivationPFPage.EmailSection section = page.emailSection();
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_STEP_TITLE));
                                    section.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_STEP_DESCRIPTION));
                                    section.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                    String content = section.content().read();
                                    if (content.contains(MODIFY)) {
                                        // l'utente ha già email e cellulare di cortesia: il passo li mostra con "Modifica" e "Continua"
                                        section.emailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_LABEL));
                                        section.emailValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                        section.modifyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                        if (content.contains(SMS_LABEL)) {
                                            section.smsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_LABEL));
                                            section.smsValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                            section.modifySmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                        }
                                        section.continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONTINUE));
                                        return;
                                    }
                                    // l'utente non ha un'email di cortesia: il passo chiede di aggiungerla
                                    section.emailInputLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_LABEL));
                                    Assertions.assertThat(section.emailInput().read()).isEmpty();
                                    section.addEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_EMAIL));
                                    section.smsQuestion().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_QUESTION));
                                    section.addSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_SMS));
                                    section.continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONTINUE));
                                }
                        ),

                        new WebScenario<>(
                                "se l'email è da aggiungere, continua non porta al riepilogo",
                                page -> page.continueButton().click(),
                                page -> {
                                    DigitalDomicileActivationPFPage.EmailSection section = page.emailSection();
                                    if (!section.content().read().contains(ADD_EMAIL)) {
                                        // L'utente ha già un'email di cortesia: "Continua" porta al riepilogo, quindi il test termina
                                        return;
                                    }
                                    section.continueButton().click();
                                    // si resta al secondo passo, senza messaggi
                                    page.currentProgressLabel().readAndAssert(h -> Assertions.assertThat(h.replaceFirst("^\\d+\\s*", "")).isEqualTo(STEPS.get(1)));
                                    section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_STEP_TITLE));
                                }
                        )
                ));
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
                                    section.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                    Assertions.assertThat(section.activateButton().get(FindPolicy.PRESENT).map(b -> b.getText())).hasValue(CONFIRM);
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
                .tests(Stream.of(
                        editScenario("se presente, modifica email non valida", true, INVALID_EMAIL, INVALID_EMAIL_MESSAGE),
                        editScenario("se presente, modifica email vuota", true, "", INVALID_EMAIL_MESSAGE),
                        editScenario("se presente, modifica cellulare non valido", false, INVALID_SMS, INVALID_SMS_MESSAGE),
                        editScenario("se presente, modifica cellulare vuoto", false, "", INVALID_SMS_MESSAGE),
                        addScenario("se da aggiungere, email non valida", true, INVALID_EMAIL, INVALID_EMAIL_MESSAGE),
                        addScenario("se da aggiungere, email vuota", true, "", INVALID_EMAIL_MESSAGE),
                        addScenario("se da aggiungere, cellulare non valido", false, INVALID_SMS, INVALID_SMS_MESSAGE),
                        addScenario("se da aggiungere, cellulare vuoto", false, "", INVALID_SMS_MESSAGE)
                ));
    }

    /**
     * Va al secondo passo, scrive il valore nel campo per aggiungere l'email o il cellulare (aperto con "Aggiungi numero di
     * cellulare"), preme "Aggiungi email" o "Aggiungi numero" e controlla il messaggio. Se l'utente ha già i recapiti lo
     * scenario termina senza verificarlo.
     */
    private WebScenario<DigitalDomicileActivationPFPage> addScenario(String name, boolean email, String value, String expectedMessage) {
        return new WebScenario<>(
                name,
                page -> page.continueButton().click(),
                page -> {
                    DigitalDomicileActivationPFPage.EmailSection section = page.emailSection();
                    String content = section.content().read();
                    if (!content.contains(email ? ADD_EMAIL : ADD_SMS)) {
                        // Il recapito è già presente: il campo per aggiungerlo non c'è, quindi il test termina senza verificarlo
                        return;
                    }
                    if (!email) {
                        section.addSmsButton().click();
                    }
                    (email ? section.emailInput() : section.smsInput()).write(value);
                    (email ? section.addEmailButton() : section.addSmsSaveButton()).click();
                    (email ? section.emailErrorMessage() : section.smsErrorMessage())
                            .readAndAssert(h -> Assertions.assertThat(h).isEqualTo(expectedMessage));
                }
        );
    }

    /**
     * Va al secondo passo, preme "Modifica" sull'email o sul cellulare, sostituisce il valore, preme "Conferma" e controlla
     * il messaggio. Se il recapito non c'è lo scenario termina senza verificarlo.
     */
    private WebScenario<DigitalDomicileActivationPFPage> editScenario(String name, boolean email, String value, String expectedMessage) {
        return new WebScenario<>(
                name,
                page -> page.continueButton().click(),
                page -> {
                    DigitalDomicileActivationPFPage.EmailSection section = page.emailSection();
                    String content = section.content().read();
                    if (!content.contains(MODIFY) || (!email && !content.contains(SMS_LABEL))) {
                        // Il recapito non è presente: "Modifica" non c'è, quindi il test termina senza verificarlo
                        return;
                    }
                    (email ? section.modifyEmailButton() : section.modifySmsButton()).click();
                    (email ? section.editEmailInput() : section.editSmsInput()).write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + value);
                    (email ? section.saveEmailButton() : section.saveSmsButton()).click();
                    (email ? section.emailErrorMessage() : section.smsErrorMessage())
                            .readAndAssert(h -> Assertions.assertThat(h).isEqualTo(expectedMessage));
                }
        );
    }

    // va al riepilogo con "Continua"; restituisce false se il secondo passo non permette di proseguire
    private boolean goToSummary(DigitalDomicileActivationPFPage page) {
        page.continueButton().click();
        DigitalDomicileActivationPFPage.EmailSection section = page.emailSection();
        if (!section.content().read().contains(MODIFY)) {
            return false;
        }
        section.continueButton().click();
        return true;
    }
}
