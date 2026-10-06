package it.pagopa.send.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.SupportPFPage;
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

import java.util.stream.Stream;

/**
 * Contract test della pagina "Come possiamo aiutarti?" del cittadino ({@code {baseUrl}/assistenza}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina e del form;</li>
 *     <li>presenza degli elementi e dei pulsanti;</li>
 *     <li>valori iniziali del form;</li>
 *     <li>messaggi di validazione e abilitazione di "Avanti".</li>
 * </ul>
 * Nessuno scenario preme "Avanti": quando le email sono valide si verifica solo che il pulsante sia abilitato.
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
public class WebSupportPFContractTest {

    // testi attesi

    private static final String TITLE = "Come possiamo aiutarti?";
    private static final String SUBTITLE = "Indica l’indirizzo email in cui desideri ricevere le risposte dell’assistenza.";
    private static final String PEC_DISCLAIMER = "Inserisci un indirizzo di posta elettronica ordinaria: le richieste inviate da un indirizzo PEC non possono essere gestite dall'assistenza.";
    private static final String MAIL = "Inserisci l’indirizzo email (no PEC)";
    private static final String CONFIRM_MAIL = "Conferma l'indirizzo email";
    private static final String PRIVACY_POLICY_TEXT = "Proseguendo dichiari di aver letto la Privacy Policy Assistenza";
    private static final String PRIVACY_POLICY_LINK = "Privacy Policy Assistenza";
    private static final String CONTINUE = "Avanti";
    private static final String BACK = "Indietro";

    private static final String INVALID_MAIL_MESSAGE = "L'indirizzo email non è valido";
    private static final String DIFFERENT_MAIL_MESSAGE = "L'indirizzo email di conferma non è uguale all'indirizzo email inserito";

    // dati di prova

    private static final String MAIL_ADDRESS = "mario.rossi@example.com";
    private static final String OTHER_MAIL_ADDRESS = "mario.bianchi@example.com";
    private static final String INVALID_MAIL_ADDRESS = "abc";

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina e del form

    @TestFactory
    Stream<DynamicTest> shouldShowSupportTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(SupportPFPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<SupportPFPage>> textScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "intestazione della pagina",
                        page -> {},
                        page -> {
                            page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                            page.subtitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(SUBTITLE));
                        }
                ),

                new WebScenario<>(
                        "avviso sulla PEC",
                        page -> {},
                        page -> page.pecDisclaimer().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PEC_DISCLAIMER))
                ),

                new WebScenario<>(
                        "etichette dei campi",
                        page -> {},
                        page -> {
                            page.mailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MAIL));
                            page.confirmMailLabel().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(CONFIRM_MAIL));
                        }
                ),

                new WebScenario<>(
                        "informativa privacy",
                        page -> {},
                        page -> page.privacyPolicyText().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PRIVACY_POLICY_TEXT))
                )
        );
    }

    // presenza degli elementi e dei pulsanti

    @TestFactory
    Stream<DynamicTest> shouldShowSupportElements() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(SupportPFPage.class)
                .tests(elementScenarios());
    }

    private Stream<WebScenario<SupportPFPage>> elementScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "pulsanti avanti e indietro",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.continueButton().get(FindPolicy.PRESENT).map(WebPresentationElement::getText)).hasValue(CONTINUE);
                            page.backButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(BACK));
                        }
                ),

                new WebScenario<>(
                        "link alla privacy policy dell'assistenza",
                        page -> {},
                        page -> {
                            page.privacyPolicyLink().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(PRIVACY_POLICY_LINK));
                            Assertions.assertThat(page.privacyPolicyLink().get(FindPolicy.PRESENT).map(link -> link.getAttributes().get("href")))
                                    .hasValueSatisfying(href -> Assertions.assertThat(href).endsWith("/privacy-policy-assistenza"));
                        }
                )
        );
    }

    // stato del form all'apertura della pagina

    @TestFactory
    Stream<DynamicTest> shouldStartWithDefaultValues() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(SupportPFPage.class)
                .tests(defaultValueScenarios());
    }

    private Stream<WebScenario<SupportPFPage>> defaultValueScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "campi email vuoti",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.mailInput().read()).isEmpty();
                            Assertions.assertThat(page.confirmMailInput().read()).isEmpty();
                        }
                ),

                new WebScenario<>(
                        "avanti disabilitato",
                        page -> {},
                        page -> Assertions.assertThat(page.continueButton().isDisabled()).isTrue()
                )
        );
    }

    // messaggi di validazione e abilitazione di "Avanti" (che non viene mai premuto)

    @TestFactory
    Stream<DynamicTest> shouldValidateSupportForm() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(SupportPFPage.class)
                .tests(validationScenarios());
    }

    private Stream<WebScenario<SupportPFPage>> validationScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "email non valida",
                        page -> {
                            page.mailInput().fill(INVALID_MAIL_ADDRESS);
                            page.mailInput().write(Keys.TAB.toString());  // il messaggio compare quando si esce dal campo
                        },
                        page -> {
                            Assertions.assertThat(page.getMailErrorMessage()).isEqualTo(INVALID_MAIL_MESSAGE);
                            Assertions.assertThat(page.continueButton().isDisabled()).isTrue();
                        }
                ),

                new WebScenario<>(
                        "email con spazi all'inizio o alla fine",
                        page -> {
                            page.mailInput().fill(" " + MAIL_ADDRESS + " ");
                            page.mailInput().write(Keys.TAB.toString());
                        },
                        page -> {
                            Assertions.assertThat(page.getMailErrorMessage()).isEqualTo(INVALID_MAIL_MESSAGE);
                            Assertions.assertThat(page.continueButton().isDisabled()).isTrue();
                        }
                ),

                new WebScenario<>(
                        "email di conferma diversa",
                        page -> {
                            page.mailInput().fill(MAIL_ADDRESS);
                            page.confirmMailInput().fill(OTHER_MAIL_ADDRESS);
                            page.confirmMailInput().write(Keys.TAB.toString());
                        },
                        page -> {
                            Assertions.assertThat(page.getConfirmMailErrorMessage()).isEqualTo(DIFFERENT_MAIL_MESSAGE);
                            Assertions.assertThat(page.continueButton().isDisabled()).isTrue();
                        }
                ),

                new WebScenario<>(
                        "email di conferma uguale ma con maiuscole diverse",
                        page -> {
                            page.mailInput().fill("Mario.Rossi@Example.com");
                            page.confirmMailInput().fill(MAIL_ADDRESS);
                            page.confirmMailInput().write(Keys.TAB.toString());
                        },
                        page -> {
                            Assertions.assertThat(page.getConfirmMailErrorMessage()).isEqualTo(DIFFERENT_MAIL_MESSAGE);
                            Assertions.assertThat(page.continueButton().isDisabled()).isTrue();
                        }
                ),

                new WebScenario<>(
                        "solo email senza conferma: avanti disabilitato",
                        page -> {
                            page.mailInput().fill(MAIL_ADDRESS);
                            page.mailInput().write(Keys.TAB.toString());
                        },
                        page -> Assertions.assertThat(page.continueButton().isDisabled()).isTrue()
                ),

                new WebScenario<>(
                        "email valide e uguali: avanti abilitato",
                        page -> {
                            page.mailInput().fill(MAIL_ADDRESS);
                            page.confirmMailInput().fill(MAIL_ADDRESS);
                            page.confirmMailInput().write(Keys.TAB.toString());
                        },
                        page -> Assertions.assertThat(page.continueButton().isDisabled()).isFalse()
                )
        );
    }
}
