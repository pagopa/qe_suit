package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.RecipientAddressScenarios;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AddressPFPage;
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

import static it.pagopa.send.suite.contract.RecipientAddressScenarios.INVALID_ADDRESS;
import static it.pagopa.send.suite.contract.RecipientAddressScenarios.SMS_QUESTION;

/**
 * Contract test della pagina "I tuoi recapiti" del cittadino ({@code {baseUrl}/recapiti}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>una card per gruppo: domicilio digitale, SEND sull'app IO, email e cellulare;</li>
 *     <li>messaggi di validazione dei recapiti da modificare o da aggiungere.</li>
 * </ul>
 * Il contenuto delle card dipende dai recapiti dell'utente: ogni scenario legge il contenuto della card e verifica la
 * variante presente (recapito attivo o da attivare). Gli scenari di validazione usano solo valori non validi anche senza
 * spazi, così "Conferma", "Aggiungi email" e "Aggiungi numero" non avviano mai la verifica del recapito; nessuno scenario
 * preme "Disattiva", "Elimina", "Gestisci" o "Inizia".
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
public class WebAddressPFContractTest {

    // testi attesi

    private static final String TITLE = "I tuoi recapiti";
    private static final String SUBTITLE = "Gestisci i recapiti digitali su cui ricevere le comunicazioni a valore legale di SEND e/o i relativi messaggi di cortesia.";
    private static final String ACTIVE = "Attivo";
    private static final String TO_ACTIVATE = "Da attivare";
    private static final String MODIFY = "Modifica";
    private static final String DISABLE = "Disattiva";

    // card del domicilio digitale
    private static final String LEGAL_TITLE = "Il tuo domicilio digitale";
    private static final String PEC_DESCRIPTION = "Quando un ente ti invia una notifica SEND, viene recapitata in modo sicuro e con valore legale sulla PEC che hai scelto.";
    private static final String MAIN_ADDRESS_ALERT = "Questo è l'indirizzo principale che verrà utilizzato per inviarti le notifiche in via digitale. Inserendolo, non riceverai più raccomandate cartacee.";
    private static final String MANAGE = "Gestisci";
    private static final String SPECIAL_CONTACTS_TITLE = "PERSONALIZZATI PER ENTE";
    private static final String DELETE = "Elimina";
    private static final String WHY_USEFUL = "Perché è utile?";
    private static final List<String> BENEFIT_TITLES = List.of("Notifiche 100% digitali", "Risparmio sulle spese di postalizzazione", "Tutto in un unico spazio");
    private static final List<String> BENEFIT_DESCRIPTIONS = List.of(
            "Le notifiche SEND non ti arriveranno più in forma cartacea.",
            "Non dovrai preoccuparti di ritirare i documenti cartacei e pagare i relativi costi.",
            "Le notifiche SEND ti saranno recapitate in modo sicuro e saranno tutte disponibili nella tua area riservata SEND."
    );
    private static final String START = "Inizia";

    // card di SEND sull'app IO
    private static final String IO_TITLE = "SEND sull’app IO";
    private static final String IO_DESCRIPTION = "Collegare SEND a IO è il modo più pratico per ricevere e gestire comunicazioni a valore legale: quando c’è una notifica per te, la ricevi in app e puoi pagare eventuali spese.";
    private static final String DOWNLOAD_IO_APP = "Scarica l'app IO";

    // card dell'email
    private static final String EMAIL_TITLE = "Il tuo indirizzo email";
    private static final String EMAIL_DESCRIPTION = "Quando c’è una notifica per te, ti avvisiamo con una email all’indirizzo che hai scelto.";
    private static final String EMAIL_TO_ADD_DESCRIPTION = "Aggiungi un indirizzo email su cui possiamo avvisarti quando c’è una notifica da leggere su SEND.";
    private static final String EMAIL_INPUT = "Indirizzo email";
    private static final String ADD_EMAIL = "Aggiungi email";
    private static final String ADD_SMS = "Aggiungi numero di cellulare";

    // card del cellulare
    private static final String SMS_TITLE = "Il tuo cellulare";
    private static final String SMS_DESCRIPTION = "Quando c’è una notifica per te, ti avvisiamo con un SMS.";
    private static final String SMS_INPUT = "Il tuo cellulare";
    private static final String SAVE_SMS = "Aggiungi numero";
    private static final String CANCEL = "Annulla";

    // messaggi di validazione; quelli di email e cellulare e i dati di prova sono in RecipientAddressScenarios
    private static final String INVALID_PEC_MESSAGE = "Indirizzo PEC non valido";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina

    @TestFactory
    Stream<DynamicTest> shouldShowAddressTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "intestazione della pagina",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                                }
                        ),

                        new WebScenario<>(
                                "titoli delle card",
                                page -> {},
                                page -> {
                                    page.legalContactsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(LEGAL_TITLE));
                                    page.ioContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_TITLE));
                                    page.emailContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_TITLE));
                                }
                        )
                ));
    }

    // card del domicilio digitale

    @TestFactory
    Stream<DynamicTest> shouldShowDigitalDomicileCard() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se attivo su PEC, stato, PEC, pulsanti, descrizione e avviso",
                                page -> {},
                                page -> {
                                    if (!page.legalContactsContent().read().contains(PEC_DESCRIPTION)) {
                                        // Domicilio digitale non attivo su PEC, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    AddressPFPage.PecContact pec = page.pecContact();
                                    page.legalContactsStatus().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVE));
                                    pec.pecValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                    pec.modifyPecButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                    pec.manageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MANAGE));
                                    pec.disableButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DISABLE));
                                    pec.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_DESCRIPTION));
                                    pec.mainAddressAlert().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MAIN_ADDRESS_ALERT));
                                }
                        ),

                        new WebScenario<>(
                                "se presenti, PEC personalizzate per ente con ente, PEC, modifica ed elimina",
                                page -> {},
                                page -> {
                                    if (!page.legalContactsContent().read().contains(SPECIAL_CONTACTS_TITLE)) {
                                        // Nessuna PEC personalizzata per ente, quindi il test termina senza verificarle
                                        return;
                                    }
                                    AddressPFPage.SpecialContacts special = page.specialContacts();
                                    special.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SPECIAL_CONTACTS_TITLE));
                                    special.senders().readAllAndAssert(h -> Assertions.assertThat(h).isNotEmpty().allSatisfy(s -> Assertions.assertThat(s).isNotBlank()));
                                    int count = special.senders().readAll().size();
                                    special.values().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(count).allSatisfy(v -> Assertions.assertThat(v).isNotBlank()));
                                    special.modifyButtons().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(count).allSatisfy(b -> Assertions.assertThat(b).isEqualTo(MODIFY)));
                                    special.deleteButtons().readAllAndAssert(h -> Assertions.assertThat(h).hasSize(count).allSatisfy(b -> Assertions.assertThat(b).isEqualTo(DELETE)));
                                }
                        ),

                        new WebScenario<>(
                                "se da attivare, vantaggi del domicilio digitale e pulsante inizia",
                                page -> {},
                                page -> {
                                    if (!page.legalContactsContent().read().contains(WHY_USEFUL)) {
                                        // Domicilio digitale già attivo, quindi il test termina senza verificare la card da attivare
                                        return;
                                    }
                                    AddressPFPage.DigitalDomicileToActivate toActivate = page.digitalDomicileToActivate();
                                    page.legalContactsStatus().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TO_ACTIVATE));
                                    toActivate.whyUseful().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(WHY_USEFUL));
                                    toActivate.benefitTitles().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(BENEFIT_TITLES));
                                    toActivate.benefitDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(BENEFIT_DESCRIPTIONS));
                                    toActivate.startButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(START));
                                }
                        )
                ));
    }

    // card di SEND sull'app IO

    @TestFactory
    Stream<DynamicTest> shouldShowIoCard() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "stato e, se da attivare, descrizione e pulsante per scaricare l'app",
                                page -> {},
                                page -> {
                                    String status = page.ioStatus().read();
                                    Assertions.assertThat(status).isNotBlank();
                                    if (!status.equals(TO_ACTIVATE)) {
                                        // SEND è già attivo sull'app IO: il contenuto della card cambia e si verifica solo lo stato
                                        return;
                                    }
                                    page.ioContactDescription().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(IO_DESCRIPTION));
                                    page.downloadIoAppButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DOWNLOAD_IO_APP));
                                }
                        )
                ));
    }

    // card dell'email e del cellulare

    @TestFactory
    Stream<DynamicTest> shouldShowCourtesyCards() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "email: se attiva stato, email, modifica, disattiva e descrizione; altrimenti campo per aggiungerla",
                                page -> {},
                                page -> {
                                    if (page.emailContactContent().read().contains(EMAIL_DESCRIPTION)) {
                                        AddressPFPage.EmailContact email = page.emailContact();
                                        email.status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVE));
                                        email.emailValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                        email.modifyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                        email.disableEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DISABLE));
                                        email.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_DESCRIPTION));
                                        return;
                                    }
                                    // l'utente non ha un'email di cortesia: la card chiede di aggiungerla
                                    AddressPFPage.EmailToAdd toAdd = page.emailToAdd();
                                    page.emailContact().status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TO_ACTIVATE));
                                    toAdd.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_TO_ADD_DESCRIPTION));
                                    toAdd.emailInputLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_INPUT));
                                    Assertions.assertThat(toAdd.emailInput().read()).isEmpty();
                                    toAdd.addEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_EMAIL));
                                }
                        ),

                        new WebScenario<>(
                                "cellulare: se attivo stato, numero, modifica, disattiva e descrizione; altrimenti domanda e pulsante per aggiungerlo",
                                page -> {},
                                page -> {
                                    if (page.content().read().contains(SMS_DESCRIPTION)) {
                                        AddressPFPage.SmsContact sms = page.smsContact();
                                        sms.smsContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_TITLE));
                                        sms.status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVE));
                                        sms.smsValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                        sms.modifySmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                        sms.disableSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DISABLE));
                                        sms.smsContactDescription().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_DESCRIPTION));
                                        return;
                                    }
                                    if (!page.emailContactContent().read().contains(SMS_QUESTION)) {
                                        // Né la card del cellulare né il pulsante per aggiungerlo: il test termina senza verificarli
                                        return;
                                    }
                                    page.emailToAdd().smsQuestion().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_QUESTION));
                                    page.emailToAdd().addSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_SMS));
                                }
                        ),

                        new WebScenario<>(
                                "se da aggiungere, aggiungi numero di cellulare apre il campo con aggiungi numero e annulla",
                                page -> {},
                                page -> {
                                    if (!page.emailContactContent().read().contains(SMS_QUESTION)) {
                                        // Il pulsante per aggiungere il cellulare non c'è, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    AddressPFPage.EmailToAdd toAdd = page.emailToAdd();
                                    toAdd.addSmsButton().click();
                                    toAdd.smsInputLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_INPUT));
                                    Assertions.assertThat(toAdd.smsInput().read()).isEmpty();
                                    toAdd.saveSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SAVE_SMS));
                                    toAdd.cancelSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CANCEL));
                                }
                        )
                ));
    }

    // messaggi di validazione: solo valori non validi anche senza spazi, così non parte mai la verifica del recapito

    @TestFactory
    Stream<DynamicTest> shouldValidateAddressForm() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(Stream.of(
                        // PEC attive: "Modifica", nuovo valore e "Conferma"
                        RecipientAddressScenarios.<AddressPFPage>editScenario("se attiva, modifica PEC del domicilio non valida", INVALID_ADDRESS, INVALID_PEC_MESSAGE,
                                page -> page.legalContactsContent().read().contains(PEC_DESCRIPTION),
                                page -> page.pecContact().modifyPecButton(), page -> page.pecContact().editPecInput(),
                                page -> page.pecContact().savePecButton(), page -> page.pecContact().editPecErrorMessage()),
                        RecipientAddressScenarios.<AddressPFPage>editScenario("se attiva, modifica PEC del domicilio vuota", "", INVALID_PEC_MESSAGE,
                                page -> page.legalContactsContent().read().contains(PEC_DESCRIPTION),
                                page -> page.pecContact().modifyPecButton(), page -> page.pecContact().editPecInput(),
                                page -> page.pecContact().savePecButton(), page -> page.pecContact().editPecErrorMessage()),
                        RecipientAddressScenarios.<AddressPFPage>editScenario("se presente, modifica PEC per ente non valida", INVALID_ADDRESS, INVALID_PEC_MESSAGE,
                                page -> page.legalContactsContent().read().contains(SPECIAL_CONTACTS_TITLE),
                                page -> page.specialContacts().firstModifyButton(), page -> page.specialContacts().firstEditInput(),
                                page -> page.specialContacts().firstSaveButton(), page -> page.specialContacts().firstEditErrorMessage()),
                        RecipientAddressScenarios.<AddressPFPage>editScenario("se presente, modifica PEC per ente vuota", "", INVALID_PEC_MESSAGE,
                                page -> page.legalContactsContent().read().contains(SPECIAL_CONTACTS_TITLE),
                                page -> page.specialContacts().firstModifyButton(), page -> page.specialContacts().firstEditInput(),
                                page -> page.specialContacts().firstSaveButton(), page -> page.specialContacts().firstEditErrorMessage())
                ));
    }

    // email e cellulare: "Modifica" o valore da aggiungere, comuni all'impresa

    @TestFactory
    Stream<DynamicTest> shouldValidateContactsToEdit() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(RecipientAddressScenarios.editContactValidations(EMAIL_DESCRIPTION, SMS_DESCRIPTION));
    }

    @TestFactory
    Stream<DynamicTest> shouldValidateContactsToAdd() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(AddressPFPage.class)
                .tests(RecipientAddressScenarios.addContactValidations(EMAIL_TO_ADD_DESCRIPTION));
    }
}
