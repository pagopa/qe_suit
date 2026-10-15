package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.NewDelegationPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Aggiungi una delega" del cittadino ({@code {baseUrl}/deleghe/nuova}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e del form;</li>
 *     <li>presenza degli elementi e dei pulsanti;</li>
 *     <li>valori iniziali del form;</li>
 *     <li>messaggi di validazione.</li>
 * </ul>
 * Nessuno scenario invia realmente la richiesta: quando viene premuto "Invia la richiesta" il codice fiscale usato
 * nei test è sempre non valido, così l'invio viene bloccato.
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
public class WebNewDelegationPFContractTest {

    // testi attesi

    private static final String TITLE = "Aggiungi una delega";
    private static final String SUBTITLE ="Inserisci i dati della persona fisica o giuridica a cui vuoi delegare la lettura delle tue notifiche.";
    private static final String REQUIRED_FIELDS = "Campi obbligatori*";
    private static final String PERSON_TYPE = "Soggetto giuridico:*";
    private static final String PERSON_TYPE_HELPER = "Seleziona la tipologia di soggetto giuridico";
    private static final String ENTITIES = "Potrà consultare le notifiche da parte di:*";
    private static final String VALIDITY_PERIOD = "Periodo di validità della delega*";
    private static final String VERIFICATION_CODE_TITLE = "Codice di verifica";
    private static final String SUBMIT = "Invia la richiesta";

    private static final String INVALID_TAX_ID_MESSAGE = "Il Codice Fiscale inserito non è corretto";
    private static final String SPACES_AT_EDGES_MESSAGE = "Elimina gli spazi all'inizio o alla fine";
    private static final String EXPIRATION_DATE_REQUIRED_MESSAGE = "La data di termine delega è obbligatoria";
    private static final String INVALID_DATE_MESSAGE = "Data errata";

    // dati di prova: il codice fiscale non valido blocca sempre l'invio del form

    private static final String INVALID_TAX_ID = "ABC";
    private static final String FIRST_NAME = "Mario";
    private static final String LAST_NAME = "Rossi";
    private static final String BUSINESS_NAME = "Ditta di prova";

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Testi della pagina e del form: intestazione, scelta del soggetto giuridico, campi della persona fisica, scelta
     * degli enti, periodo di validità della delega e codice di verifica.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNewDelegationTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NewDelegationPFPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<NewDelegationPFPage>> textScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "intestazione della pagina",
                        page -> {},
                        page -> {
                            page.breadcrumbs().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                            page.delegationsBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Deleghe"));
                            page.currentBreadcrumb().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Nuova delega"));
                            page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                            page.requiredFieldsLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(REQUIRED_FIELDS));
                        }
                ),

                new WebScenario<>(
                        "scelta del soggetto giuridico",
                        page -> {},
                        page -> {
                            page.personType().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PERSON_TYPE));
                            page.personTypeHelper().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PERSON_TYPE_HELPER));
                            Assertions.assertThat(page.naturalPersonRadio().getLabel()).isEqualTo("Persona fisica");
                            Assertions.assertThat(page.legalPersonRadio().getLabel()).isEqualTo("Persona giuridica");
                        }
                ),

                new WebScenario<>(
                        "campi della persona fisica",
                        page -> {},
                        page -> {
                            page.firstNameLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Nome"));
                            page.lastNameLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Cognome"));
                            page.taxIdLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Codice Fiscale"));
                        }
                ),

                new WebScenario<>(
                        "scelta degli enti",
                        page -> {},
                        page -> {
                            page.entitiesLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ENTITIES));
                            page.entitiesHelper().readAndAssert(h -> Assertions.assertThat(h).startsWith("Seleziona un"));
                            Assertions.assertThat(page.allEntitiesRadio().getLabel()).isEqualTo("Tutti gli enti");
                            Assertions.assertThat(page.selectedEntitiesRadio().getLabel()).isEqualTo("Solo enti selezionati");
                        }
                ),

                new WebScenario<>(
                        "periodo di validità della delega",
                        page -> {},
                        page -> {
                            page.validityPeriodLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(VALIDITY_PERIOD));
                            page.expirationDateLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo("Termine delega"));
                        }
                ),

                new WebScenario<>(
                        "codice di verifica",
                        page -> {},
                        page -> {
                            page.verificationCodeTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(VERIFICATION_CODE_TITLE));
                            page.verificationCodeDescription().readAndAssert(h -> Assertions.assertThat(h).startsWith("Condividi questo codice con la persona delegata"));
                        }
                )
        );
    }

    /**
     * Elementi e pulsanti: codice di verifica di 5 cifre con il pulsante per copiarlo e "Invia la richiesta". Con
     * "Persona giuridica" compare la ragione sociale al posto di nome e cognome; con "Solo enti selezionati" compare
     * l'elenco degli enti, di cui si verifica solo che non sia vuoto perché i nomi dipendono dagli enti attivi sulla
     * piattaforma.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowNewDelegationElements() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NewDelegationPFPage.class)
                .tests(elementScenarios());
    }

    private Stream<WebScenario<NewDelegationPFPage>> elementScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "codice di verifica di 5 cifre con il pulsante per copiarlo",
                        page -> {},
                        page -> {
                            page.verificationCode().readAndAssert(h -> Assertions.assertThat(h.replaceAll("\\s", "")).matches("\\d{5}"));
                            page.copyVerificationCodeButton().assertLoaded();
                        }
                ),

                new WebScenario<>(
                        "pulsante di invio della richiesta",
                        page -> {},
                        page -> page.submitButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBMIT))
                ),

                new WebScenario<>(
                        "persona giuridica: ragione sociale al posto di nome e cognome",
                        page -> page.legalPersonRadio().select(),
                        page -> page.businessNameLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Ragione Sociale"))
                ),

                new WebScenario<>(
                        "solo enti selezionati: compare la scelta degli enti",
                        page -> page.selectedEntitiesRadio().select(),
                        page -> page.selectedEntitiesLabel().readAndAssert(h -> Assertions.assertThat(h).contains("Seleziona enti"))
                ),

                new WebScenario<>(
                        "solo enti selezionati: l'elenco degli enti contiene almeno un ente",
                        page -> {
                            page.selectedEntitiesRadio().select();
                            page.selectedEntitiesDropdown().click();
                        },
                        page -> page.selectedEntitiesOptions().readAllAndAssert(h -> Assertions.assertThat(h)
                                .isNotEmpty()
                                .allSatisfy(entity -> Assertions.assertThat(entity).isNotBlank()))
                )
        );
    }

    /**
     * Stato del form all'apertura della pagina: persona fisica selezionata, tutti gli enti selezionati e termine della
     * delega precompilato a domani.
     */
    @TestFactory
    Stream<DynamicTest> shouldStartWithDefaultValues() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NewDelegationPFPage.class)
                .tests(defaultValueScenarios());
    }

    private Stream<WebScenario<NewDelegationPFPage>> defaultValueScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "persona fisica selezionata",
                        page -> {},
                        page -> Assertions.assertThat(page.naturalPersonRadio().isSelected()).isTrue()
                ),

                new WebScenario<>(
                        "tutti gli enti selezionati",
                        page -> {},
                        page -> Assertions.assertThat(page.allEntitiesRadio().isSelected()).isTrue()
                ),

                new WebScenario<>(
                        "termine delega precompilato a domani",
                        page -> {},
                        page -> page.expirationDateInput().readAndAssert(h -> Assertions.assertThat(h)
                                .isEqualTo(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                )
        );
    }

    /**
     * Messaggi di validazione mostrati dal portale sotto il campo: codice fiscale non valido (persona fisica e
     * giuridica), spazi all'inizio o alla fine in nome, cognome e ragione sociale, termine della delega vuoto e data
     * errata (passata, 31 febbraio, incompleta). Se un campo obbligatorio è vuoto il browser mostra il fumetto "Compila
     * questo campo.", che non fa parte della pagina: per questo gli scenari compilano i campi obbligatori.
     */
    @TestFactory
    Stream<DynamicTest> shouldValidateNewDelegationForm() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(NewDelegationPFPage.class)
                .tests(validationScenarios());
    }

    private Stream<WebScenario<NewDelegationPFPage>> validationScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "codice fiscale non valido",
                        page -> {
                            page.firstNameInput().fill(FIRST_NAME);
                            page.lastNameInput().fill(LAST_NAME);
                            page.taxIdInput().fill(INVALID_TAX_ID);
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(page.getTaxIdErrorMessage()).isEqualTo(INVALID_TAX_ID_MESSAGE)
                ),

                new WebScenario<>(
                        "nome con spazi all'inizio o alla fine",
                        page -> {
                            page.firstNameInput().fill(" " + FIRST_NAME + " ");
                            page.lastNameInput().fill(LAST_NAME);
                            page.taxIdInput().fill(INVALID_TAX_ID);
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(page.getFirstNameErrorMessage()).isEqualTo(SPACES_AT_EDGES_MESSAGE)
                ),

                new WebScenario<>(
                        "cognome con spazi all'inizio o alla fine",
                        page -> {
                            page.firstNameInput().fill(FIRST_NAME);
                            page.lastNameInput().fill(" " + LAST_NAME + " ");
                            page.taxIdInput().fill(INVALID_TAX_ID);
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(page.getLastNameErrorMessage()).isEqualTo(SPACES_AT_EDGES_MESSAGE)
                ),

                new WebScenario<>(
                        "ragione sociale con spazi all'inizio o alla fine",
                        page -> {
                            page.legalPersonRadio().select();
                            page.businessNameInput().fill(" " + BUSINESS_NAME + " ");
                            page.taxIdInput().fill(INVALID_TAX_ID);
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(page.getBusinessNameErrorMessage()).isEqualTo(SPACES_AT_EDGES_MESSAGE)
                ),

                new WebScenario<>(
                        "persona giuridica con codice fiscale non numerico",
                        page -> {
                            page.legalPersonRadio().select();
                            page.businessNameInput().fill(BUSINESS_NAME);
                            page.taxIdInput().fill(INVALID_TAX_ID);
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(page.getTaxIdErrorMessage())
                                .startsWith("Inserisci solo numeri.")
                                .contains("Persona fisica")
                ),

                new WebScenario<>(
                        "termine delega vuoto",
                        page -> {
                            page.firstNameInput().fill(FIRST_NAME);
                            page.lastNameInput().fill(LAST_NAME);
                            page.taxIdInput().fill(INVALID_TAX_ID);
                            page.expirationDateInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE);  // il campo data ha una maschera: clean() svuota solo il giorno
                            page.submitButton().click();
                        },
                        page -> Assertions.assertThat(page.getExpirationDateErrorMessage()).isEqualTo(EXPIRATION_DATE_REQUIRED_MESSAGE)
                ),

                invalidDateScenario("termine delega con data passata", "01012020"),
                invalidDateScenario("termine delega con data impossibile", "31022027"),
                invalidDateScenario("termine delega con data incompleta", "0101")
        );
    }

    // il messaggio compare quando si esce dal campo; il campo data ha una maschera, quindi si scrivono solo le cifre
    private WebScenario<NewDelegationPFPage> invalidDateScenario(String name, String digits) {
        return new WebScenario<>(
                name,
                page -> {
                    page.expirationDateInput().write(Keys.chord(Keys.CONTROL, "a") + Keys.DELETE);
                    page.expirationDateInput().write(digits);
                    page.expirationDateInput().write(Keys.TAB.toString());
                },
                page -> Assertions.assertThat(page.getExpirationDateErrorMessage()).isEqualTo(INVALID_DATE_MESSAGE)
        );
    }
}
