package it.pagopa.send.suite.contract.destinatario_pf;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.AppStatusPFPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Contract test della pagina "Stato della piattaforma" del cittadino ({@code {baseUrl}/app-status}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>stato attuale dei servizi.</li>
 * </ul>
 * Lo stato è lo stesso per tutti gli utenti ma cambia nel tempo: si verificano i testi fissi e il formato dei dati, non i
 * loro valori. Dello storico dei disservizi si verifica solo il titolo: il contenuto dipende dai disservizi registrati
 * dalla piattaforma (tabella e paginazione oppure messaggio di storico vuoto) e non viene verificato.
 */
@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAppStatusPFContractTest {

    // testi attesi

    private static final String TITLE = "Stato della piattaforma";
    private static final String SUBTITLE = "Verifica il funzionamento di SEND, visualizza lo storico dei disservizi e scarica le relative attestazioni opponibili a terzi. Ogni attestazione certifica un disservizio: potrebbe esserti utile nel caso in cui questo abbia coinvolto una notifica destinata a te.";
    private static final String HISTORY_TITLE = "Storico dei disservizi";
    private static final String LAST_CHECK_PATTERN = "Ultimo aggiornamento - .+, ore \\d{2}:\\d{2}";

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Testi della pagina: titolo, sottotitolo e titolo "Storico dei disservizi".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowAppStatusTexts() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.USER_1))
                .on(AppStatusPFPage.class)
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
                                "titolo dello storico dei disservizi",
                                page -> {},
                                page -> page.downtimeHistoryTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(HISTORY_TITLE))
                        )
                ));
    }

    /**
     * Stato attuale dei servizi: presenza del messaggio, che cambia se è in corso un disservizio, e data dell'ultimo
     * aggiornamento nel formato "Ultimo aggiornamento - &lt;giorno&gt;, ore hh:mm".
     */
    @TestFactory
    Stream<DynamicTest> shouldShowCurrentStatus() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.USER_1))
                .on(AppStatusPFPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "messaggio dello stato attuale",
                                page -> {},
                                // il messaggio cambia se è in corso un disservizio: si verifica solo che ci sia
                                page -> page.statusBar().readAndAssert(h -> Assertions.assertThat(h).isNotBlank())
                        ),

                        new WebScenario<>(
                                "data e ora dell'ultimo aggiornamento",
                                page -> {},
                                page -> page.lastCheck().readAndAssert(h -> Assertions.assertThat(h).matches(LAST_CHECK_PATTERN))
                        )
                ));
    }
}
