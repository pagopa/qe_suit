package it.pagopa.send.suite.contract;

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

    // dati di prova: il codice fiscale non valido blocca sempre l'invio del form

    private static final String INVALID_TAX_ID = "ABC";
    private static final String FIRST_NAME = "Mario";
    private static final String LAST_NAME = "Rossi";
    private static final String BUSINESS_NAME = "Ditta di prova";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e del form

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

    // presenza degli elementi e dei pulsanti, compresi quelli che compaiono in base alle scelte

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

    // stato del form all'apertura della pagina

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

    // messaggi di validazione mostrati dal portale

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
                )
        );
    }
}
