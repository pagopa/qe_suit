package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.User;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.mittente.infrastructure.page.DashboardPage;
import it.pagopa.send.web.notification_creation.application.NotificationData;
import it.pagopa.send.web.notification_creation.infrastructure.factory.NotificationFactory;
import it.pagopa.send.web.notification_creation.infrastructure.page.CreateNotificationPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Invia una nuova notifica" della PA ({@code {baseUrl}/nuova-notifica}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>intestazione della pagina e nomi dei passi;</li>
 *     <li>un gruppo per ogni passo del wizard, con testi e stato iniziale del passo;</li>
 *     <li>richiesta di conferma "Vuoi uscire?" quando si lascia la pagina;</li>
 *     <li>accesso dalla dashboard con "Invia una nuova notifica".</li>
 * </ul>
 * I passi dopo il primo si raggiungono compilando i precedenti con i dati fissi di {@code simple_notification}, uguali per
 * tutte le utenze: "Dettaglio posizione debitoria" scegliendo "Avviso pagoPA", "Documentazione" scegliendo "Nessun
 * pagamento". Nessuno scenario preme "Invia", quindi nessuna notifica viene creata. L'obbligatorietà del gruppo dipende
 * dai gruppi dell'ente e non viene verificata.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebNewNotificationPaContractTest {

    // testi attesi: intestazione

    private static final String NOTIFICATIONS = "Notifiche";
    private static final String NEW_NOTIFICATION = "Nuova notifica";
    private static final String TITLE = "Invia una nuova notifica";
    private static final String SUBTITLE = "Per inviare una notifica, inserisci i dati richiesti e aggiungi eventualmente una Posizione debitoria. Se devi fare un invio massivo, scopri come funziona.";
    private static final String HOW_IT_WORKS = "scopri come funziona";
    private static final String PRELIMINARY_INFORMATIONS = "Informazioni preliminari";
    private static final String RECIPIENTS = "Destinatari";
    private static final String DEBT_POSITION = "Posizione debitoria";
    private static final String DEBT_POSITION_DETAIL = "Dettaglio posizione debitoria";
    private static final String DOCUMENTS = "Documentazione";

    // testi attesi: comuni ai passi

    private static final String REQUIRED_FIELDS = "*Campi obbligatori";
    private static final String CONTINUE = "Continua";
    private static final String SEND = "Invia";
    // MUI aggiunge ai campi obbligatori uno spazio sottile e l'asterisco
    private static final String REQUIRED = " *";
    private static final String UPLOAD = "carica il file";

    // testi attesi: passo "Informazioni preliminari"

    private static final String[] PRELIMINARY_INFORMATIONS_SECTIONS = {
            "Ente mittente",
            "Lingua di invio",
            "Contenuti della notifica",
            "Numero di protocollo",
            "Codice tassonomico",
            "Modalità per l’invio cartaceo",
            "Gestione notifica"
    };
    private static final String[] PRELIMINARY_INFORMATIONS_SUBTITLES = {
            "La notifica, i documenti e le attestazioni verranno inviati secondo le preferenze di lingua selezionate.",
            "Queste informazioni aiuteranno il destinatario a comprendere il contenuto e l'importanza della comunicazione.",
            "È il codice univoco fornito dall'Ufficio Protocollo della Pubblica Amministrazione. Serve per identificare ufficialmente la notifica e tracciarne il percorso all'interno del sistema.",
            "È il codice che definisce la tipologia di documenti notificati. Se non lo conosci, consulta l'elenco in Tassonomia SEND.",
            "Se l'invio in digitale non risultasse possibile, la notifica sarà inviata in via analogica nella modalità selezionata.",
            "Scegli il gruppo di utenti responsabile della gestione della notifica."
    };
    private static final String[] PRELIMINARY_INFORMATIONS_FIELDS = {
            "Nome dell'ente" + REQUIRED,
            "Oggetto della notifica" + REQUIRED,
            "Descrizione",
            "Numero di protocollo" + REQUIRED,
            "Codice tassonomico" + REQUIRED
    };
    // l'asterisco del gruppo compare solo se l'ente ha dei gruppi
    private static final String GROUP = "Gruppo";
    private static final String[] PRELIMINARY_INFORMATIONS_OPTIONS = {
            "Italiano",
            "Italiano e altra lingua",
            "Modello 890",
            "Raccomandata A/R"
    };

    // testi attesi: passo "Destinatari"

    private static final String[] RECIPIENTS_SECTIONS = {"Soggetto giuridico", "Indirizzo", "Domicilio digitale"};
    private static final String[] RECIPIENTS_SUBTITLES = {
            "Queste informazioni aiuteranno il destinatario a comprendere il contenuto e l'importanza della comunicazione.",
            "Scegli se consentire a SEND di inserire automaticamente l'indirizzo dai registri nazionali. L'inserimento automatico potrebbe ritardare l'invio della notifica al destinatario. In alternativa puoi compilare l'indirizzo manualmente.",
            "Se conosci l'indirizzo PEC del destinatario inseriscilo qui. In questo modo, assicuri che la notifica venga recapitata in formato digitale, riducendo i tempi di consegna rispetto ai canali analogici tradizionali."
    };
    private static final String[] RECIPIENTS_FIELDS = {
            "Codice Fiscale destinatario" + REQUIRED,
            "Nome" + REQUIRED,
            "Cognome",
            "Indirizzo PEC"
    };
    private static final String[] RECIPIENTS_OPTIONS = {
            "Persona fisica",
            "Persona giuridica",
            "Inserimento automatico da registro nazionale",
            "Inserimento manuale"
    };
    private static final String ADD_RECIPIENT = "Aggiungi un destinatario";
    private static final String BACK_TO_PRELIMINARY_INFORMATIONS = "Torna a Informazioni preliminari";

    // testi attesi: passo "Posizione debitoria"

    private static final String WHICH_PAYMENT = "Quale tipo di pagamento prevedi?";
    private static final String[] DEBT_POSITION_OPTIONS = {
            "Avviso pagoPA",
            "Modello F24",
            "Avviso pagoPA + Modello F24",
            "Nessun pagamento"
    };
    private static final String BACK_TO_RECIPIENTS = "Torna a Destinatari";

    // testi attesi: passo "Dettaglio posizione debitoria"

    private static final String[] DEBT_POSITION_DETAIL_SECTIONS = {"Costo di notifica", "Tecnologia del pagamento"};
    // la descrizione del caricamento dell'avviso pagoPA ha lo stesso stile delle descrizioni delle sezioni
    private static final String[] DEBT_POSITION_DETAIL_SUBTITLES = {
            "Scegli il tipo di costo di notifica: puoi scegliere fra quello già incluso nell’atto oppure inserire un costo di notifica personalizzato a carico del destinatario.",
            "Nella modalità sincrona la posizione debitoria è presso il sistema dell'Ente Creditore (EC), mentre nella modalità asincrona è caricata sul sistema Gestione Posizioni Debitorie (GPD) di pagoPA. Scopri di più",
            "Trascina qui il documento in formato .pdf oppure"
    };
    private static final String[] DEBT_POSITION_DETAIL_FIELDS = {
            "Codice avviso" + REQUIRED,
            "Codice fiscale ente creditore" + REQUIRED
    };
    private static final String[] DEBT_POSITION_DETAIL_OPTIONS = {
            "Incluso nell’atto (forfettario), a carico del destinatario",
            "Non incluso nell’atto (puntuale), a carico del destinatario",
            "Sincrona",
            "Asincrona"
    };
    private static final String PAGOPA_INTEGRATION_ALERT = "Se non hai informazioni certe sull’integrazione pagoPA verifica con il tuo provider tecnologico: in caso di scelta non corretta l’addebito della notifica potrebbe essere errato.";
    private static final String ADD_PAGOPA_NOTICE = "Aggiungi codice di avviso pagoPA";
    private static final String BACK_TO_DEBT_POSITION = "Torna a Posizione debitoria";

    // testi attesi: passo "Documentazione"

    private static final String ATTACHED_DOCUMENTS = "Documenti allegati";
    private static final String ATTACH_DOCUMENTS = "Allega i documenti da notificare";
    // la descrizione del caricamento del documento ha lo stesso stile delle descrizioni delle sezioni
    private static final String[] DOCUMENTS_SUBTITLES = {
            "Questi sono i documenti che saranno notificati tramite SEND a tutti i destinatari inseriti. Puoi allegare fino a un massimo di 10 documenti.",
            "Trascina qui il documento oppure"
    };
    private static final String DOCUMENT_TITLE = "Titolo del documento" + REQUIRED;
    private static final String ADD_DOCUMENT = "Aggiungi documento";

    // testi attesi: "Vuoi uscire?"

    private static final String EXIT_TITLE = "Vuoi uscire?";
    private static final String EXIT_MESSAGE = "Se esci, perderai tutti i dati inseriti finora. Per inviare una nuova notifica, dovrai ricominciare da capo.";
    private static final String CANCEL = "Annulla";
    private static final String EXIT = "Esci";

    // dati di prova: i passi si compilano con il modello della creazione di una notifica semplice

    private static final String NOTIFICATION_TEMPLATE = "simple_notification";
    private static final String ANY_TEXT = "a";

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;
    private final NotificationFactory notificationFactory;

    /**
     * Intestazione della pagina: breadcrumb "Notifiche" > "Nuova notifica", titolo, sottotitolo con il link "scopri come
     * funziona" e i nomi dei cinque passi.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNewNotificationHeader() {
        return webContractValidator.as(sender())
                .on(CreateNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "breadcrumb notifiche e nuova notifica",
                                page -> {},
                                page -> {
                                    page.notificationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS));
                                    page.breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NEW_NOTIFICATION));
                                }
                        ),

                        new WebScenario<>(
                                "titolo e sottotitolo con scopri come funziona",
                                page -> {},
                                page -> {
                                    page.header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                    page.howItWorksLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(HOW_IT_WORKS));
                                }
                        ),

                        new WebScenario<>(
                                "nomi dei passi",
                                page -> {},
                                page -> Assertions.assertThat(page.visibleTexts(page.stepNames()))
                                        .containsExactly(PRELIMINARY_INFORMATIONS, RECIPIENTS, DEBT_POSITION, DEBT_POSITION_DETAIL, DOCUMENTS)
                        )
                ));
    }

    /**
     * Passo "Informazioni preliminari" all'apertura della pagina: testi delle sezioni e stato iniziale, con il nome
     * dell'ente precompilato, "Italiano" e "Modello 890" selezionati, campi vuoti e "Continua" disabilitato.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowPreliminaryInformationsStep() {
        User sender = sender();
        return webContractValidator.as(sender)
                .on(CreateNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "testi del passo informazioni preliminari",
                                page -> {},
                                page -> {
                                    page.stepTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PRELIMINARY_INFORMATIONS));
                                    page.requiredFieldsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_FIELDS));
                                    Assertions.assertThat(page.visibleTexts(page.sectionTitles())).containsExactly(PRELIMINARY_INFORMATIONS_SECTIONS);
                                    Assertions.assertThat(page.visibleTexts(page.sectionSubtitles())).containsExactly(PRELIMINARY_INFORMATIONS_SUBTITLES);
                                    List<String> fields = page.visibleTexts(page.fieldLabels());
                                    Assertions.assertThat(fields).hasSize(PRELIMINARY_INFORMATIONS_FIELDS.length + 1).startsWith(PRELIMINARY_INFORMATIONS_FIELDS);
                                    Assertions.assertThat(fields.get(PRELIMINARY_INFORMATIONS_FIELDS.length)).startsWith(GROUP);
                                    Assertions.assertThat(page.visibleTexts(page.optionLabels())).containsExactly(PRELIMINARY_INFORMATIONS_OPTIONS);
                                    assertButtonText(page.submitButton(), CONTINUE);
                                }
                        ),

                        new WebScenario<>(
                                "stato iniziale del passo informazioni preliminari",
                                page -> {},
                                page -> {
                                    // il campo del nome dell'ente è disabilitato: il valore si legge senza attenderne la visibilità
                                    Assertions.assertThat(page.senderDenominationInput().get(FindPolicy.PRESENT).map(WebPresentationElement::getText))
                                            .hasValue(sender.getOrganization());
                                    Assertions.assertThat(page.italianRadio().isSelected()).isTrue();
                                    Assertions.assertThat(page.italianAndOtherLanguageRadio().isSelected()).isFalse();
                                    page.subjectInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.abstractInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.protocolNumberInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.taxonomyCodeInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    Assertions.assertThat(page.registeredLetter890Radio().isSelected()).isTrue();
                                    Assertions.assertThat(page.registeredLetterRadio().isSelected()).isFalse();
                                    Assertions.assertThat(page.submitButton().isDisabled()).isTrue();
                                }
                        )
                ));
    }

    /**
     * Passo "Destinatari", raggiunto compilando "Informazioni preliminari": testi delle sezioni e stato iniziale, con
     * "Persona fisica" selezionata, campi vuoti e "Continua" disabilitato.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowRecipientsStep() {
        NotificationData data = notificationFactory.load(NOTIFICATION_TEMPLATE);
        return webContractValidator.as(sender())
                .on(CreateNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "testi del passo destinatari",
                                page -> page.goToRecipientsStep(data),
                                page -> {
                                    page.stepTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(RECIPIENTS));
                                    page.requiredFieldsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_FIELDS));
                                    Assertions.assertThat(page.visibleTexts(page.sectionTitles())).containsExactly(RECIPIENTS_SECTIONS);
                                    Assertions.assertThat(page.visibleTexts(page.sectionSubtitles())).containsExactly(RECIPIENTS_SUBTITLES);
                                    Assertions.assertThat(page.visibleTexts(page.fieldLabels())).containsExactly(RECIPIENTS_FIELDS);
                                    Assertions.assertThat(page.visibleTexts(page.optionLabels())).containsExactly(RECIPIENTS_OPTIONS);
                                    Assertions.assertThat(page.visibleTexts(page.formButtons())).containsExactly(ADD_RECIPIENT);
                                    assertButtonText(page.submitButton(), CONTINUE);
                                    page.previousStepButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK_TO_PRELIMINARY_INFORMATIONS));
                                }
                        ),

                        new WebScenario<>(
                                "stato iniziale del passo destinatari",
                                page -> page.goToRecipientsStep(data),
                                page -> {
                                    Assertions.assertThat(page.naturalPersonRadio().isSelected()).isTrue();
                                    Assertions.assertThat(page.legalPersonRadio().isSelected()).isFalse();
                                    page.recipientTaxIdInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.recipientFirstNameInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.recipientLastNameInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.recipientPecInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    Assertions.assertThat(page.submitButton().isDisabled()).isTrue();
                                }
                        )
                ));
    }

    /**
     * Passo "Posizione debitoria", raggiunto compilando i passi precedenti: testi e stato iniziale, senza nessun tipo di
     * pagamento selezionato e con "Continua" disabilitato.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDebtPositionStep() {
        NotificationData data = notificationFactory.load(NOTIFICATION_TEMPLATE);
        return webContractValidator.as(sender())
                .on(CreateNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "testi del passo posizione debitoria",
                                page -> page.goToDebtPositionStep(data),
                                page -> {
                                    page.stepTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DEBT_POSITION));
                                    Assertions.assertThat(page.visibleTexts(page.sectionTitles())).containsExactly(WHICH_PAYMENT);
                                    Assertions.assertThat(page.visibleTexts(page.optionLabels())).containsExactly(DEBT_POSITION_OPTIONS);
                                    assertButtonText(page.submitButton(), CONTINUE);
                                    page.previousStepButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK_TO_RECIPIENTS));
                                }
                        ),

                        new WebScenario<>(
                                "stato iniziale del passo posizione debitoria",
                                page -> page.goToDebtPositionStep(data),
                                page -> {
                                    Assertions.assertThat(page.pagoPaRadio().isSelected()).isFalse();
                                    Assertions.assertThat(page.f24Radio().isSelected()).isFalse();
                                    Assertions.assertThat(page.pagoPaAndF24Radio().isSelected()).isFalse();
                                    Assertions.assertThat(page.noPaymentRadio().isSelected()).isFalse();
                                    Assertions.assertThat(page.submitButton().isDisabled()).isTrue();
                                }
                        )
                ));
    }

    /**
     * Passo "Dettaglio posizione debitoria", raggiunto scegliendo "Avviso pagoPA": testi del costo di notifica, della
     * tecnologia del pagamento e dell'avviso pagoPA, e stato iniziale, senza opzioni selezionate e con "Continua"
     * disabilitato.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDebtPositionDetailStep() {
        NotificationData data = notificationFactory.load(NOTIFICATION_TEMPLATE);
        return webContractValidator.as(sender())
                .on(CreateNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "testi del passo dettaglio posizione debitoria",
                                page -> page.goToDebtPositionDetailStep(data),
                                page -> {
                                    page.stepTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DEBT_POSITION_DETAIL));
                                    Assertions.assertThat(page.visibleTexts(page.sectionTitles())).containsExactly(DEBT_POSITION_DETAIL_SECTIONS);
                                    Assertions.assertThat(page.visibleTexts(page.sectionSubtitles())).map(String::strip).containsExactly(DEBT_POSITION_DETAIL_SUBTITLES);
                                    Assertions.assertThat(page.visibleTexts(page.fieldLabels())).containsExactly(DEBT_POSITION_DETAIL_FIELDS);
                                    Assertions.assertThat(page.visibleTexts(page.optionLabels())).containsExactly(DEBT_POSITION_DETAIL_OPTIONS);
                                    Assertions.assertThat(page.visibleTexts(page.alertMessages())).containsExactly(PAGOPA_INTEGRATION_ALERT);
                                    Assertions.assertThat(page.visibleTexts(page.formButtons())).containsExactly(UPLOAD, ADD_PAGOPA_NOTICE);
                                    assertButtonText(page.submitButton(), CONTINUE);
                                    page.previousStepButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK_TO_DEBT_POSITION));
                                }
                        ),

                        new WebScenario<>(
                                "stato iniziale del passo dettaglio posizione debitoria",
                                page -> page.goToDebtPositionDetailStep(data),
                                page -> {
                                    Assertions.assertThat(page.flatRateRadio().isSelected()).isFalse();
                                    Assertions.assertThat(page.deliveryModeRadio().isSelected()).isFalse();
                                    Assertions.assertThat(page.syncRadio().isSelected()).isFalse();
                                    Assertions.assertThat(page.asyncRadio().isSelected()).isFalse();
                                    Assertions.assertThat(page.submitButton().isDisabled()).isTrue();
                                }
                        )
                ));
    }

    /**
     * Passo "Documentazione", raggiunto scegliendo "Nessun pagamento": testi e stato iniziale, con il titolo del
     * documento vuoto e "Invia" disabilitato.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDocumentsStep() {
        NotificationData data = notificationFactory.load(NOTIFICATION_TEMPLATE);
        return webContractValidator.as(sender())
                .on(CreateNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "testi del passo documentazione",
                                page -> page.goToDocumentsStep(data),
                                page -> {
                                    page.stepTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ATTACHED_DOCUMENTS));
                                    page.requiredFieldsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_FIELDS));
                                    Assertions.assertThat(page.visibleTexts(page.sectionTitles())).containsExactly(ATTACH_DOCUMENTS);
                                    Assertions.assertThat(page.visibleTexts(page.sectionSubtitles())).map(String::strip).containsExactly(DOCUMENTS_SUBTITLES);
                                    Assertions.assertThat(page.visibleTexts(page.fieldLabels())).containsExactly(DOCUMENT_TITLE);
                                    Assertions.assertThat(page.visibleTexts(page.formButtons())).containsExactly(UPLOAD, ADD_DOCUMENT);
                                    assertButtonText(page.submitButton(), SEND);
                                    page.previousStepButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK_TO_DEBT_POSITION));
                                }
                        ),

                        new WebScenario<>(
                                "stato iniziale del passo documentazione",
                                page -> page.goToDocumentsStep(data),
                                page -> {
                                    page.documentTitleInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    Assertions.assertThat(page.submitButton().isDisabled()).isTrue();
                                }
                        )
                ));
    }

    /**
     * Lasciare la pagina con il breadcrumb "Notifiche" apre la richiesta di conferma "Vuoi uscire?": "Annulla" chiude la
     * richiesta e lascia il form utilizzabile, "Esci" apre la dashboard.
     */
    @TestFactory
    Stream<DynamicTest> shouldAskConfirmationBeforeLeaving() {
        return webContractValidator.as(sender())
                .on(CreateNotificationPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "richiesta di conferma dell'uscita",
                                page -> page.notificationsBreadcrumb().click(),
                                page -> {
                                    CreateNotificationPage.ExitDialog dialog = page.exitDialog();
                                    dialog.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT_TITLE));
                                    dialog.message().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT_MESSAGE));
                                    dialog.cancelButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CANCEL));
                                    dialog.exitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EXIT));
                                }
                        ),

                        new WebScenario<>(
                                "annulla chiude la richiesta e lascia il form utilizzabile",
                                page -> {
                                    page.notificationsBreadcrumb().click();
                                    page.exitDialog().cancelButton().click();
                                },
                                page -> {
                                    // con la richiesta ancora aperta il campo non sarebbe raggiungibile
                                    page.subjectInput().fill(ANY_TEXT);
                                    page.subjectInput().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ANY_TEXT));
                                }
                        ),

                        new WebScenario<>(
                                "esci apre la dashboard",
                                page -> {
                                    page.notificationsBreadcrumb().click();
                                    page.exitDialog().exitButton().click();
                                },
                                // il banner dei cookie è già stato accettato: basta il titolo della dashboard
                                page -> page.dashboard().header().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(NOTIFICATIONS))
                        )
                ));
    }

    /**
     * La pagina si apre dalla dashboard con il pulsante "Invia una nuova notifica".
     */
    @TestFactory
    Stream<DynamicTest> shouldOpenFromDashboard() {
        return webContractValidator.as(sender())
                .on(DashboardPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "invia una nuova notifica aperta dalla dashboard",
                                dashboard -> dashboard.newNotificationButton().click(),
                                dashboard -> dashboard.createNotification().assertLoaded()
                        )
                ));
    }

    private User sender() {
        return userProvider.resolve(UserAlias.COMUNE_1);
    }

    // il pulsante disabilitato non risulta visibile a Selenium: il testo si legge senza attenderne la visibilità
    private static void assertButtonText(Button button, String expected) {
        Assertions.assertThat(button.get(FindPolicy.PRESENT).map(WebPresentationElement::getText)).hasValue(expected);
    }
}
