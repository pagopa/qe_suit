package it.pagopa.send.suite.contract.destinatario_pg;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.suite.contract.RecipientAddressScenarios;
import it.pagopa.send.web.address.infrastructure.suit.RecipientAddressPage;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.AddressPGPage;
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
 * Contract test della pagina "Recapiti" dell'impresa ({@code {baseUrl}/recapiti}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>card del domicilio digitale;</li>
 *     <li>card dell'email aziendale e del cellulare aziendale;</li>
 *     <li>messaggi di validazione di email e cellulare, comuni al cittadino ({@link RecipientAddressScenarios}).</li>
 * </ul>
 * Il contenuto delle card dipende dai recapiti dell'impresa: ogni scenario legge il contenuto della card e verifica la
 * variante presente (recapito attivo o da attivare). Gli scenari di validazione usano solo valori non validi; nessuno
 * scenario preme "Disattiva", "Gestisci" o "Inizia".
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
public class WebAddressPGContractTest {

    private static final Recipient RECIPIENT = Recipient.PETRARCA;

    // testi attesi

    private static final String TITLE = "Recapiti";
    // "tua impresa" è un link alla scheda dell'impresa
    private static final String SUBTITLE = "Gestisci i recapiti digitali su cui ricevere le comunicazioni a valore legale di SEND e/o i relativi messaggi di cortesia per la tua impresa.";
    private static final String ACTIVE = "Attivo";
    private static final String TO_ACTIVATE = "Da attivare";
    private static final String MODIFY = "Modifica";
    private static final String DISABLE = "Disattiva";

    // card del domicilio digitale
    private static final String LEGAL_TITLE = "Il domicilio digitale della tua impresa";
    private static final String SERCQ_VALUE = "Piattaforma SEND";
    private static final String SERCQ_DESCRIPTION = "Quando un ente invia alla tua impresa una notifica SEND, viene recapitata in modo sicuro e con valore legale tramite la piattaforma stessa.";
    private static final String MANAGE = "Gestisci";
    private static final String WHY_USEFUL = "Perché è utile?";
    private static final List<String> BENEFIT_TITLES = List.of("Notifiche 100% digitali", "Risparmio sulle spese di postalizzazione", "Tutto in un unico spazio");
    private static final List<String> BENEFIT_DESCRIPTIONS = List.of(
            "Le notifiche SEND non ti arriveranno più in forma cartacea.",
            "Non dovrai preoccuparti di ritirare i documenti cartacei e pagare i relativi costi.",
            "Le notifiche SEND saranno recapitate in modo sicuro e saranno tutte disponibili nell’area riservata SEND della tua impresa."
    );
    private static final String START = "Inizia";

    // card dell'email aziendale
    private static final String EMAIL_TITLE = "Indirizzo email aziendale";
    private static final String EMAIL_DESCRIPTION = "Quando c’è una notifica per la tua impresa, ti informiamo con una email all’indirizzo che hai scelto.";
    private static final String EMAIL_TO_ADD_DESCRIPTION = "Aggiungi un indirizzo email aziendale su cui possiamo avvisarti quando c’è una notifica SEND per la tua impresa.";
    private static final String EMAIL_INPUT = "Email aziendale";
    private static final String ADD_EMAIL = "Aggiungi email";
    private static final String ADD_SMS = "Aggiungi numero di cellulare";

    // card del cellulare aziendale
    private static final String SMS_TITLE = "Cellulare aziendale";
    private static final String SMS_DESCRIPTION = "Quando c’è una notifica per la tua impresa, ti avvisiamo con un SMS.";
    private static final String SMS_INPUT = "Cellulare aziendale";
    private static final String SAVE_SMS = "Aggiungi numero";
    private static final String CANCEL = "Annulla";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina

    @TestFactory
    Stream<DynamicTest> shouldShowAddressTexts() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(AddressPGPage.class)
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
                                    page.emailContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_TITLE));
                                }
                        )
                ));
    }

    // card del domicilio digitale

    @TestFactory
    Stream<DynamicTest> shouldShowDigitalDomicileCard() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(AddressPGPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "se attivo sulla piattaforma SEND, stato, piattaforma, descrizione, gestisci e disattiva",
                                page -> {},
                                page -> {
                                    if (!page.legalContactsContent().read().contains(SERCQ_DESCRIPTION)) {
                                        // Domicilio digitale non attivo sulla piattaforma SEND, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    RecipientAddressPage.SercqContact sercq = page.sercqContact();
                                    page.legalContactsStatus().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVE));
                                    sercq.value().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SERCQ_VALUE));
                                    sercq.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SERCQ_DESCRIPTION));
                                    sercq.manageButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MANAGE));
                                    sercq.disableButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DISABLE));
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
                                    RecipientAddressPage.DigitalDomicileToActivate toActivate = page.digitalDomicileToActivate();
                                    page.legalContactsStatus().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TO_ACTIVATE));
                                    toActivate.whyUseful().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(WHY_USEFUL));
                                    toActivate.benefitTitles().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(BENEFIT_TITLES));
                                    toActivate.benefitDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(BENEFIT_DESCRIPTIONS));
                                    toActivate.startButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(START));
                                }
                        )
                ));
    }

    // card dell'email aziendale e del cellulare aziendale

    @TestFactory
    Stream<DynamicTest> shouldShowCourtesyCards() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(AddressPGPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "email: se attiva stato, email, modifica, disattiva e descrizione; altrimenti campo per aggiungerla",
                                page -> {},
                                page -> {
                                    if (page.emailContactContent().read().contains(EMAIL_DESCRIPTION)) {
                                        RecipientAddressPage.EmailContact email = page.emailContact();
                                        email.status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVE));
                                        email.emailValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                        email.modifyEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                        email.disableEmailButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DISABLE));
                                        email.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(EMAIL_DESCRIPTION));
                                        return;
                                    }
                                    // l'impresa non ha un'email aziendale: la card chiede di aggiungerla
                                    RecipientAddressPage.EmailToAdd toAdd = page.emailToAdd();
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
                                        RecipientAddressPage.SmsContact sms = page.smsContact();
                                        sms.smsContactTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_TITLE));
                                        sms.status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVE));
                                        sms.smsValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                        sms.modifySmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MODIFY));
                                        sms.disableSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DISABLE));
                                        sms.smsContactDescription().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SMS_DESCRIPTION));
                                        return;
                                    }
                                    if (!page.emailContactContent().read().contains(RecipientAddressScenarios.SMS_QUESTION)) {
                                        // Né la card del cellulare né il pulsante per aggiungerlo: il test termina senza verificarli
                                        return;
                                    }
                                    page.emailToAdd().smsQuestion().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(RecipientAddressScenarios.SMS_QUESTION));
                                    page.emailToAdd().addSmsButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_SMS));
                                }
                        ),

                        new WebScenario<>(
                                "se da aggiungere, aggiungi numero di cellulare apre il campo con aggiungi numero e annulla",
                                page -> {},
                                page -> {
                                    if (!page.emailContactContent().read().contains(RecipientAddressScenarios.SMS_QUESTION)) {
                                        // Il pulsante per aggiungere il cellulare non c'è, quindi il test termina senza verificarlo
                                        return;
                                    }
                                    RecipientAddressPage.EmailToAdd toAdd = page.emailToAdd();
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
    Stream<DynamicTest> shouldValidateContactsToEdit() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(AddressPGPage.class)
                .tests(RecipientAddressScenarios.editContactValidations(EMAIL_DESCRIPTION, SMS_DESCRIPTION));
    }

    @TestFactory
    Stream<DynamicTest> shouldValidateContactsToAdd() {
        return webContractValidator.asRecipient(RECIPIENT)
                .on(AddressPGPage.class)
                .tests(RecipientAddressScenarios.addContactValidations(EMAIL_TO_ADD_DESCRIPTION));
    }
}
