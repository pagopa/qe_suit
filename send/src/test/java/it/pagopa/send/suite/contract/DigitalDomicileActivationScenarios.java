package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.web.address.infrastructure.suit.DigitalDomicileActivationPage;
import it.pagopa.utils.async.PollingUtils;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

/**
 * Scenari comuni ai contract test del wizard "Attiva domicilio digitale su SEND" del cittadino
 * ({@code WebDigitalDomicileActivationPFContractTest}) e dell'impresa ({@code WebDigitalDomicileActivationPGContractTest}).
 * La struttura del wizard è la stessa nei due portali; i testi che cambiano sono passati da ogni test con {@link Texts}.
 * <p>
 * Le validazioni usano solo valori non validi anche togliendo gli spazi. Gli scenari della finestra del codice di verifica
 * usano invece un recapito valido, che fa inviare un codice: non scrivono mai un codice numerico e non premono mai
 * "Conferma", quindi il recapito non viene salvato.
 */
public final class DigitalDomicileActivationScenarios {

    // testi attesi
    public static final String TITLE = "Attiva domicilio digitale su SEND";
    public static final String CONTINUE = "Continua";
    public static final String CANCEL = "Annulla";
    public static final String BACK = "Indietro";
    public static final String MODIFY = "Modifica";
    public static final String CONFIRM = "Conferma";

    // passo 1
    private static final String HOW_IT_WORKS = "Come funziona";
    private static final String DELIVERED = "consegnata";
    private static final String DELIVERED_DIALOG_TITLE = "Valore giuridico della notifica sul domicilio digitale SEND";
    private static final String DELIVERED_DIALOG_DESCRIPTION = "Se scegli SEND come domicilio digitale, dopo 7 giorni dalla consegna la notifica risulterà legalmente recapitata, anche se non l’hai consultata.";
    private static final String UNDERSTAND = "Ok, ho capito";

    // passo 2
    public static final String ADD_EMAIL = "Aggiungi email";
    public static final String ADD_SMS = "Aggiungi numero di cellulare";

    // finestra del codice di verifica
    private static final String EMAIL_CODE_TITLE = "Verifica email ";
    private static final String EMAIL_CODE_DESCRIPTION = "Abbiamo inviato un codice all’indirizzo email indicato. Il codice è valido per 15 minuti.";
    private static final String EMAIL_NEW_CODE_HINT = "Non l’hai ricevuto? Controlla di aver inserito l’email corretta oppure generane uno nuovo.";
    private static final String SMS_CODE_TITLE = "Verifica numero ";
    private static final String SMS_CODE_DESCRIPTION = "Abbiamo inviato un codice al numero di cellulare indicato. Il codice è valido per 15 minuti.";
    private static final String SMS_NEW_CODE_HINT = "Non l’hai ricevuto? Controlla di aver inserito il numero di cellulare corretto oppure generane uno nuovo.";
    private static final String CODE_LABEL = "Inserisci codice";
    private static final String NEW_CODE = "generane uno nuovo";
    private static final String NOT_NUMERIC_CODE_TITLE = "Questo campo accetta solo valori numerici";
    private static final String NOT_NUMERIC_CODE_DESCRIPTION = "Controlla il codice di verifica e inseriscilo di nuovo";

    // dati di prova: codici non numerici, che il campo rifiuta senza premere "Conferma"
    private static final String LETTERS_CODE = "abcde";
    private static final String MIXED_CODE = "12ab";

    private DigitalDomicileActivationScenarios() {
    }

    /**
     * Testi del wizard che cambiano tra i portali.
     *
     * @param steps            nomi dei tre passi
     * @param infoTitles       titoli dei tre punti di "Come funziona"
     * @param infoDescriptions descrizioni dei tre punti di "Come funziona"
     * @param emailStepTitle   titolo del secondo passo
     * @param emailStepDescription descrizione del secondo passo
     * @param emailLabel       etichetta dell'email
     * @param smsLabel         etichetta del cellulare
     */
    public record Texts(List<String> steps, List<String> infoTitles, List<String> infoDescriptions,
                        String emailStepTitle, String emailStepDescription, String emailLabel, String smsLabel) {
    }

    /**
     * Titolo del wizard e nomi dei passi.
     */
    public static <P extends DigitalDomicileActivationPage> Stream<WebScenario<P>> texts(Texts texts) {
        return Stream.of(
                new WebScenario<>(
                        "titolo e nomi dei passi",
                        page -> {},
                        page -> {
                            page.wizardTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                            // il passo corrente mostra il numero davanti al nome, quindi il nome si confronta senza il numero
                            page.howItWorksProgressLabel().readAndAssert(h -> Assertions.assertThat(withoutNumber(h)).isEqualTo(texts.steps().get(0)));
                            page.insertEmailProgressLabel().readAndAssert(h -> Assertions.assertThat(withoutNumber(h)).isEqualTo(texts.steps().get(1)));
                            page.summaryProgressLabel().readAndAssert(h -> Assertions.assertThat(withoutNumber(h)).isEqualTo(texts.steps().get(2)));
                        }
                )
        );
    }

    /**
     * Primo passo: i tre punti di "Come funziona", "Continua", "Annulla" e la finestra aperta da "consegnata".
     */
    public static <P extends DigitalDomicileActivationPage> Stream<WebScenario<P>> howItWorksStep(Texts texts) {
        return Stream.of(
                new WebScenario<>(
                        "passo come funziona con i tre punti, continua e annulla",
                        page -> {},
                        page -> {
                            page.howItWorksTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(HOW_IT_WORKS));
                            page.infoTitles().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(texts.infoTitles()));
                            page.infoDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(texts.infoDescriptions()));
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
                            DigitalDomicileActivationPage.DeliveredDialog dialog = page.deliveredDialog();
                            dialog.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELIVERED_DIALOG_TITLE));
                            dialog.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELIVERED_DIALOG_DESCRIPTION));
                            dialog.understandButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(UNDERSTAND));
                        }
                )
        );
    }

    /**
     * Secondo passo: con i recapiti già presenti li mostra con "Modifica", altrimenti chiede di aggiungere l'email; senza
     * email "Continua" non porta al riepilogo.
     */
    public static <P extends DigitalDomicileActivationPage> Stream<WebScenario<P>> emailStep(Texts texts) {
        return Stream.of(
                new WebScenario<>(
                        "passo email: titolo, descrizione, indietro e, se presenti, email e cellulare con modifica",
                        page -> page.continueButton().click(),
                        page -> {
                            DigitalDomicileActivationPage.EmailSection section = page.emailSection();
                            section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(texts.emailStepTitle()));
                            section.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(texts.emailStepDescription()));
                            section.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                            String content = section.content().read();
                            if (content.contains(MODIFY)) {
                                // l'utente ha già email e cellulare di cortesia: il passo li mostra con "Modifica" e "Continua"
                                section.emailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(texts.emailLabel()));
                                section.emailValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                section.modifyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                if (content.contains(texts.smsLabel())) {
                                    section.smsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(texts.smsLabel()));
                                    section.smsValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    section.modifySmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                }
                                section.continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONTINUE));
                                return;
                            }
                            // l'utente non ha un'email di cortesia: il passo chiede di aggiungerla
                            section.emailInputLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(texts.emailLabel()));
                            Assertions.assertThat(section.emailInput().read()).isEmpty();
                            section.addEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_EMAIL));
                            section.smsQuestion().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(RecipientAddressScenarios.SMS_QUESTION));
                            section.addSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_SMS));
                            section.continueButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONTINUE));
                        }
                ),

                new WebScenario<>(
                        "se l'email è da aggiungere, continua non porta al riepilogo",
                        page -> page.continueButton().click(),
                        page -> {
                            DigitalDomicileActivationPage.EmailSection section = page.emailSection();
                            if (!section.content().read().contains(ADD_EMAIL)) {
                                // L'utente ha già un'email di cortesia: "Continua" porta al riepilogo, quindi il test termina
                                return;
                            }
                            section.continueButton().click();
                            // si resta al secondo passo, senza messaggi
                            page.currentProgressLabel().readAndAssert(h -> Assertions.assertThat(withoutNumber(h)).isEqualTo(texts.steps().get(1)));
                            section.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(texts.emailStepTitle()));
                        }
                )
        );
    }

    /**
     * Messaggi di validazione del secondo passo, uguali nei due portali: email e cellulare da modificare, se presenti, o da
     * aggiungere. Solo valori non validi.
     */
    public static <P extends DigitalDomicileActivationPage> Stream<WebScenario<P>> emailStepValidations(Texts texts) {
        return Stream.of(
                DigitalDomicileActivationScenarios.<P>editScenario("se presente, modifica email non valida", true, RecipientAddressScenarios.INVALID_ADDRESS, RecipientAddressScenarios.INVALID_EMAIL_MESSAGE, texts),
                DigitalDomicileActivationScenarios.<P>editScenario("se presente, modifica email vuota", true, "", RecipientAddressScenarios.INVALID_EMAIL_MESSAGE, texts),
                DigitalDomicileActivationScenarios.<P>editScenario("se presente, modifica cellulare non valido", false, RecipientAddressScenarios.INVALID_SMS, RecipientAddressScenarios.INVALID_SMS_MESSAGE, texts),
                DigitalDomicileActivationScenarios.<P>editScenario("se presente, modifica cellulare vuoto", false, "", RecipientAddressScenarios.INVALID_SMS_MESSAGE, texts),
                DigitalDomicileActivationScenarios.<P>addScenario("se da aggiungere, email non valida", true, RecipientAddressScenarios.INVALID_ADDRESS, RecipientAddressScenarios.INVALID_EMAIL_MESSAGE),
                DigitalDomicileActivationScenarios.<P>addScenario("se da aggiungere, email vuota", true, "", RecipientAddressScenarios.INVALID_EMAIL_MESSAGE),
                DigitalDomicileActivationScenarios.<P>addScenario("se da aggiungere, cellulare non valido", false, RecipientAddressScenarios.INVALID_SMS, RecipientAddressScenarios.INVALID_SMS_MESSAGE),
                DigitalDomicileActivationScenarios.<P>addScenario("se da aggiungere, cellulare vuoto", false, "", RecipientAddressScenarios.INVALID_SMS_MESSAGE)
        );
    }

    /**
     * Finestra del codice di verifica aperta aggiungendo l'email: testi, codice non numerico, "generane uno nuovo" e
     * "Annulla". Ogni scenario fa inviare un codice all'email, che non viene mai salvata.
     */
    public static <P extends DigitalDomicileActivationPage> Stream<WebScenario<P>> emailCodeDialog(Texts texts, String email) {
        return codeDialog(texts, true, email, EMAIL_CODE_TITLE + email, EMAIL_CODE_DESCRIPTION, EMAIL_NEW_CODE_HINT);
    }

    /**
     * Finestra del codice di verifica aperta aggiungendo il cellulare: come {@link #emailCodeDialog}, ma ogni scenario fa
     * inviare un SMS al numero.
     */
    public static <P extends DigitalDomicileActivationPage> Stream<WebScenario<P>> smsCodeDialog(Texts texts, String number) {
        return codeDialog(texts, false, number, SMS_CODE_TITLE + number, SMS_CODE_DESCRIPTION, SMS_NEW_CODE_HINT);
    }

    private static <P extends DigitalDomicileActivationPage> Stream<WebScenario<P>> codeDialog(Texts texts, boolean email, String value,
                                                                                              String title, String description, String newCodeHint) {
        String contact = email ? "l'email" : "il cellulare";
        return Stream.of(
                new WebScenario<>(
                        "se " + contact + " è da aggiungere, finestra del codice con testi e pulsanti e codice non numerico rifiutato",
                        page -> {},
                        page -> {
                            if (!openCodeDialog(page, email, value)) {
                                // Il recapito è già presente: il campo per aggiungerlo non c'è, quindi il test termina
                                return;
                            }
                            DigitalDomicileActivationPage.CodeDialog dialog = page.codeDialog();
                            dialog.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(title));
                            dialog.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(description));
                            dialog.codeLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CODE_LABEL));
                            Assertions.assertThat(dialog.codeInput().read()).isEmpty();
                            dialog.newCodeHint().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(newCodeHint));
                            dialog.newCodeButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NEW_CODE));
                            dialog.cancelButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CANCEL));
                            // "Conferma" salverebbe il recapito: si legge e non si preme
                            dialog.confirmButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONFIRM));
                            // il campo rifiuta subito, senza "Conferma", i codici con caratteri che non sono cifre
                            for (String code : List.of(LETTERS_CODE, MIXED_CODE)) {
                                dialog.codeInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE + code);
                                dialog.errorAlert().readAndAssert(h -> Assertions.assertThat(h)
                                        .contains(NOT_NUMERIC_CODE_TITLE)
                                        .contains(NOT_NUMERIC_CODE_DESCRIPTION));
                            }
                        }
                ),

                new WebScenario<>(
                        "se " + contact + " è da aggiungere, generane uno nuovo lascia aperta la finestra e annulla torna al passo email",
                        page -> {},
                        page -> {
                            if (!openCodeDialog(page, email, value)) {
                                // Il recapito è già presente: il campo per aggiungerlo non c'è, quindi il test termina
                                return;
                            }
                            DigitalDomicileActivationPage.CodeDialog dialog = page.codeDialog();
                            // il portale invia un nuovo codice senza messaggi: la finestra resta com'è
                            dialog.newCodeButton().click();
                            dialog.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(title));
                            dialog.cancelButton().click();
                            // la finestra si chiude con un'animazione
                            PollingUtils.pollUntil(() -> dialog.confirmButton().get(FindPolicy.PRESENT).isEmpty(), closed -> closed,
                                    Duration.ofSeconds(5), Duration.ofMillis(500), "la finestra del codice è ancora aperta dopo Annulla");
                            page.currentProgressLabel().readAndAssert(h -> Assertions.assertThat(withoutNumber(h)).isEqualTo(texts.steps().get(1)));
                        }
                )
        );
    }

    /**
     * Va al secondo passo, scrive il valore nel campo per aggiungere l'email o il cellulare (aperto con "Aggiungi numero di
     * cellulare"), preme "Aggiungi email" o "Aggiungi numero" e controlla il messaggio. Se l'utente ha già i recapiti lo
     * scenario termina senza verificarlo.
     */
    private static <P extends DigitalDomicileActivationPage> WebScenario<P> addScenario(String name, boolean email, String value, String expectedMessage) {
        return new WebScenario<>(
                name,
                page -> page.continueButton().click(),
                page -> {
                    DigitalDomicileActivationPage.EmailSection section = page.emailSection();
                    if (!section.content().read().contains(email ? ADD_EMAIL : ADD_SMS)) {
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
    private static <P extends DigitalDomicileActivationPage> WebScenario<P> editScenario(String name, boolean email, String value, String expectedMessage, Texts texts) {
        return new WebScenario<>(
                name,
                page -> page.continueButton().click(),
                page -> {
                    DigitalDomicileActivationPage.EmailSection section = page.emailSection();
                    String content = section.content().read();
                    if (!content.contains(MODIFY) || (!email && !content.contains(texts.smsLabel()))) {
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

    // va al secondo passo e aggiunge il recapito valido, che apre la finestra del codice; false se il recapito c'è già
    private static boolean openCodeDialog(DigitalDomicileActivationPage page, boolean email, String value) {
        page.continueButton().click();
        DigitalDomicileActivationPage.EmailSection section = page.emailSection();
        if (!section.content().read().contains(email ? ADD_EMAIL : ADD_SMS)) {
            return false;
        }
        if (!email) {
            section.addSmsButton().click();
        }
        (email ? section.emailInput() : section.smsInput()).write(value);
        (email ? section.addEmailButton() : section.addSmsSaveButton()).click();
        return true;
    }

    // il passo corrente mostra il numero davanti al nome
    private static String withoutNumber(String label) {
        return label.replaceFirst("^\\d+\\s*", "");
    }
}
