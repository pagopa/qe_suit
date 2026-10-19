package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.DelegationsPFPage;
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

import java.util.stream.Stream;

/**
 * Contract test della pagina "Deleghe" del cittadino ({@code {baseUrl}/deleghe}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>presenza dei pulsanti.</li>
 * </ul>
 * Il contenuto delle sezioni "I tuoi delegati" e "Deleghe a tuo carico" dipende dalle deleghe dell'utente (sezione vuota
 * oppure tabella) e non viene verificato. Nessuno scenario crea, accetta o revoca deleghe.
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
public class WebDelegationsPFContractTest {

    // testi attesi

    private static final String TITLE = "Deleghe";
    private static final String SUBTITLE = "Qui puoi gestire i tuoi delegati e le deleghe a tuo carico. I primi sono le persone fisiche o giuridiche che hai autorizzato alla visualizzazione e gestione delle tue notifiche, le seconde sono coloro che hanno autorizzato te.";
    private static final String DELEGATES_TITLE = "I tuoi delegati";
    private static final String DELEGATORS_TITLE = "Deleghe a tuo carico";
    private static final String ADD_DELEGATION = "Aggiungi una delega";


    private final WebBrowserContractValidator webContractValidator;

    /**
     * Testi della pagina: titolo, sottotitolo e titoli delle sezioni "I tuoi delegati" e "Deleghe a tuo carico".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDelegationsTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DelegationsPFPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<DelegationsPFPage>> textScenarios() {
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
                        "titoli delle sezioni",
                        page -> {},
                        page -> {
                            page.delegatesTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELEGATES_TITLE));
                            page.delegatorsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(DELEGATORS_TITLE));
                        }
                )
        );
    }

    /**
     * Pulsante "Aggiungi una delega" e la pagina "Aggiungi una delega" che apre.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowDelegationsButtons() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(DelegationsPFPage.class)
                .tests(buttonScenarios());
    }

    private Stream<WebScenario<DelegationsPFPage>> buttonScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "pulsante aggiungi una delega",
                        page -> {},
                        page -> page.addDelegationButton().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(ADD_DELEGATION))
                ),

                new WebScenario<>(
                        "il pulsante aggiungi una delega apre la pagina aggiungi una delega",
                        page -> page.addDelegationButton().click(),
                        page -> page.newDelegation().assertLoaded()
                )
        );
    }
}
