package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DigitalDomicileManagementPFPage;
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
 * Contract test della pagina "Gestisci il tuo domicilio digitale" del cittadino
 * ({@code {baseUrl}/recapiti/domicilio-digitale/gestione}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e domicilio digitale attivo;</li>
 *     <li>opzioni "Trasferisci su SEND" e "Personalizza per ente" e dove portano;</li>
 *     <li>form "Personalizza per ente" e i suoi messaggi di validazione.</li>
 * </ul>
 * La pagina è disponibile solo a un utente con un domicilio digitale attivo; "Trasferisci su SEND" c'è solo con il
 * domicilio su PEC e ogni scenario lo verifica se presente. Nessuno scenario conferma il wizard di trasferimento; gli
 * scenari del form lasciano sempre vuoto "Ente mittente", quindi "Conferma" mostra solo i messaggi e non salva nulla.
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
public class WebDigitalDomicileManagementPFContractTest {

    // testi attesi

    private static final String TITLE = "Gestisci il tuo domicilio digitale";
    private static final String OPTIONS_TITLE = "Scegli un’opzione";
    private static final String BACK = "Indietro";
    private static final String ACTIVE = "Attivo";

    // opzioni
    private static final String TRANSFER = "Trasferisci su SEND";
    private static final String TRANSFER_TITLE = "Trasferisci il domicilio digitale sulla piattaforma SEND";
    private static final String TRANSFER_DESCRIPTION = "La piattaforma SEND ti garantisce consegne sicure e con valore legale. Il servizio è gratuito e non richiede strumenti aggiuntivi.";
    private static final String CUSTOMIZE = "Personalizza per ente";
    private static final String CUSTOMIZE_TITLE = "Personalizza il tuo domicilio digitale per ente mittente";
    private static final String CUSTOMIZE_DESCRIPTION = "Scegli dove ricevere le notifiche che ti invia un ente specifico.";
    private static final List<String> TRANSFER_STEPS = List.of("Come funziona", "Inserisci la tua email", "Riepilogo");

    // form personalizza per ente
    // le etichette dei campi obbligatori finiscono con uno spazio sottile e l'asterisco
    private static final String REQUIRED = " *";
    private static final String FORM_TITLE = "Inserisci l’ente e il recapito da associare";
    private static final String FORM_DESCRIPTION = "Il domicilio digitale personalizzato dove riceverai le notifiche SEND che ti invia un ente specifico";
    private static final String REQUIRED_FIELDS = "*Campi obbligatori";
    private static final String SENDER_LABEL = "Ente mittente" + REQUIRED;
    private static final String CHANNEL_TYPE_LABEL = "Tipologia" + REQUIRED;
    private static final List<String> CHANNEL_TYPES = List.of("Indirizzo PEC", "Domicilio digitale SEND");
    private static final String PEC_LABEL = "Indirizzo PEC" + REQUIRED;
    private static final String DISCLAIMER = "Accetto che questo indirizzo sia utilizzato da SEND in via prioritaria rispetto a eventuali PEC registrate presso un ente mittente o presente nei pubblici registri." + REQUIRED;
    private static final String CONFIRM = "Conferma";

    // messaggi di validazione
    private static final String REQUIRED_MESSAGE = "Campo obbligatorio";
    private static final String INVALID_PEC_MESSAGE = "Indirizzo PEC non valido";

    // dati di prova: non validi anche senza spazi

    private static final String INVALID_PEC = "abc";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e domicilio attivo

    @TestFactory
    Stream<DynamicTest> shouldShowManagementTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileManagementPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "titolo, titolo delle opzioni e indietro",
                                page -> {},
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.optionsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(OPTIONS_TITLE));
                                    // "Indietro" torna alla pagina precedente nella cronologia, che dipende da come si arriva qui
                                    page.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                }
                        ),

                        new WebScenario<>(
                                "domicilio digitale attivo con stato e indirizzo",
                                page -> {},
                                page -> {
                                    page.status().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ACTIVE));
                                    page.pecValue().readAndAssert(h -> Assertions.assertThat(h).isNotBlank());
                                }
                        )
                ));
    }

    // opzioni

    @TestFactory
    Stream<DynamicTest> shouldShowManagementOptions() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileManagementPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "opzione personalizza per ente e, se domicilio su PEC, trasferisci su SEND",
                                page -> {},
                                page -> {
                                    page.customizeBySenderButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CUSTOMIZE));
                                    if (!page.content().read().contains(TRANSFER)) {
                                        // domicilio su SEND: c'è solo l'opzione per ente
                                        page.optionTitles().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(CUSTOMIZE_TITLE));
                                        page.optionDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(CUSTOMIZE_DESCRIPTION));
                                        return;
                                    }
                                    page.optionTitles().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(TRANSFER_TITLE, CUSTOMIZE_TITLE));
                                    page.optionDescriptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactly(TRANSFER_DESCRIPTION, CUSTOMIZE_DESCRIPTION));
                                    page.transferToSendButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TRANSFER));
                                }
                        ),

                        new WebScenario<>(
                                "se presente, trasferisci su SEND apre il wizard di trasferimento",
                                page -> {},
                                page -> {
                                    if (!page.content().read().contains(TRANSFER)) {
                                        // Domicilio già su SEND: l'opzione non c'è, quindi il test termina
                                        return;
                                    }
                                    page.transferToSendButton().click();
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TRANSFER_TITLE));
                                    // il passo corrente mostra il numero davanti al nome, quindi il nome si confronta senza il numero
                                    page.transferSteps().readAllAndAssert(h -> Assertions.assertThat(h)
                                            .map(s -> s.replaceFirst("^\\d+\\s*", ""))
                                            .containsExactlyElementsOf(TRANSFER_STEPS));
                                }
                        ),

                        new WebScenario<>(
                                "personalizza per ente apre il form con ente, tipologia, conferma e indietro",
                                page -> page.customizeBySenderButton().click(),
                                page -> {
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CUSTOMIZE_TITLE));
                                    DigitalDomicileManagementPFPage.CustomizeBySenderForm form = page.customizeBySenderForm();
                                    form.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(FORM_TITLE));
                                    form.description().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(FORM_DESCRIPTION));
                                    form.requiredFieldsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_FIELDS));
                                    form.senderLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SENDER_LABEL));
                                    Assertions.assertThat(form.senderInput().read()).isEmpty();
                                    form.channelTypeLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CHANNEL_TYPE_LABEL));
                                    form.confirmButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONFIRM));
                                    form.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                                }
                        ),

                        new WebScenario<>(
                                "tipologia propone indirizzo PEC e domicilio digitale SEND",
                                page -> page.customizeBySenderButton().click(),
                                page -> {
                                    page.customizeBySenderForm().channelTypeSelect().click();
                                    page.channelTypeOptions().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(CHANNEL_TYPES));
                                }
                        ),

                        new WebScenario<>(
                                "tipologia indirizzo PEC aggiunge il campo PEC e l'accettazione",
                                page -> page.customizeBySenderButton().click(),
                                page -> {
                                    DigitalDomicileManagementPFPage.CustomizeBySenderForm form = page.customizeBySenderForm();
                                    form.channelTypeSelect().click();
                                    page.pecOption().click();
                                    form.pecLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_LABEL));
                                    Assertions.assertThat(form.pecInput().read()).isEmpty();
                                    form.disclaimerLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DISCLAIMER));
                                }
                        ),

                        new WebScenario<>(
                                "indietro del form torna alle opzioni",
                                page -> page.customizeBySenderButton().click(),
                                page -> {
                                    page.customizeBySenderForm().backButton().click();
                                    page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                                    page.optionsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(OPTIONS_TITLE));
                                }
                        )
                ));
    }

    // messaggi di validazione del form: "Ente mittente" resta vuoto, quindi "Conferma" non salva nulla

    @TestFactory
    Stream<DynamicTest> shouldValidateCustomizeBySenderForm() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DigitalDomicileManagementPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "form vuoto: ente e tipologia obbligatori",
                                page -> page.customizeBySenderButton().click(),
                                page -> {
                                    DigitalDomicileManagementPFPage.CustomizeBySenderForm form = page.customizeBySenderForm();
                                    form.confirmButton().click();
                                    form.senderErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_MESSAGE));
                                    form.channelTypeErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_MESSAGE));
                                }
                        ),

                        pecScenario("tipologia PEC con PEC non valida", INVALID_PEC, INVALID_PEC_MESSAGE),
                        pecScenario("tipologia PEC con PEC vuota", "", INVALID_PEC_MESSAGE)
                ));
    }

    /**
     * Apre il form, sceglie la tipologia "Indirizzo PEC", scrive il valore e preme "Conferma" senza scegliere l'ente:
     * controlla il messaggio della PEC e quelli dei campi obbligatori ente e accettazione.
     */
    private WebScenario<DigitalDomicileManagementPFPage> pecScenario(String name, String value, String expectedMessage) {
        return new WebScenario<>(
                name,
                page -> page.customizeBySenderButton().click(),
                page -> {
                    DigitalDomicileManagementPFPage.CustomizeBySenderForm form = page.customizeBySenderForm();
                    form.channelTypeSelect().click();
                    page.pecOption().click();
                    form.pecInput().write(value);
                    form.confirmButton().click();
                    form.pecErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(expectedMessage));
                    form.senderErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_MESSAGE));
                    form.disclaimerErrorMessage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_MESSAGE));
                }
        );
    }
}
