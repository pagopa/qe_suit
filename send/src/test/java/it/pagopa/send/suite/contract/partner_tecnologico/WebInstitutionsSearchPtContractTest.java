package it.pagopa.send.suite.contract.partner_tecnologico;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.send.web.partner_tecnologico.infrastructure.page.PtInstitutionsPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.time.Duration;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Contract test della ricerca nella pagina "I tuoi enti" del BackOffice dei Partner Tecnologici, aperta con la sessione
 * del PT {@code PT_1}, che deve avere almeno un ente delegante.
 * <p>
 * Testi e selettori vengono dal sorgente di pn-pt-webapp (PN-21981).
 */
@EnabledIf(expression = "${partner-tecnologico.enabled}", loadContext = true)
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebInstitutionsSearchPtContractTest {

    private static final String TOO_SHORT = "ab";
    private static final String NO_MATCH = "zzzqqq";
    private static final String OTHER_NO_MATCH = "qqqzzz";
    private static final String MIN_LENGTH_ERROR = "Inserisci almeno tre caratteri";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Ricerca degli enti:
     * <ul>
     *     <li>con meno di 3 caratteri compare l'errore e "Cerca" resta abilitato;</li>
     *     <li>una stringa senza corrispondenze mostra il messaggio di nessun risultato;</li>
     *     <li>dopo aver svuotato il filtro si può fare una nuova ricerca;</li>
     *     <li>la ricerca non distingue tra maiuscole e minuscole;</li>
     *     <li>con almeno un risultato il paginatore è presente.</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldSearchInstitutions() {
        return webContractValidator.as(userProvider.resolve(UserAlias.PT_1))
                .on(PtInstitutionsPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "meno di 3 caratteri: errore e cerca abilitato",
                                page -> page.searchInstitutions(TOO_SHORT),
                                page -> {
                                    page.searchError().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MIN_LENGTH_ERROR));
                                    Assertions.assertThat(page.searchButton().isDisabled()).isFalse();
                                }
                        ),

                        new WebScenario<>(
                                "stringa senza corrispondenze: nessun risultato",
                                page -> page.searchInstitutions(NO_MATCH),
                                page -> page.noResultsTitle().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(noResultsTitle(NO_MATCH)))
                        ),

                        new WebScenario<>(
                                "filtro svuotato e nuova ricerca",
                                page -> {
                                    page.searchInstitutions(NO_MATCH);
                                    page.searchInput().fill("");
                                    page.searchInstitutions(OTHER_NO_MATCH);
                                },
                                // resta visibile il titolo della ricerca precedente finché la nuova non è conclusa
                                page -> eventually(() -> Assertions.assertThat(page.noResultsTitle().read()).isEqualTo(noResultsTitle(OTHER_NO_MATCH)))
                        ),

                        new WebScenario<>(
                                "ricerca senza distinzione tra maiuscole e minuscole",
                                page -> {},
                                page -> {
                                    String name = page.institutionNames().read();
                                    assertSearchFinds(page, name, name.toUpperCase(Locale.ITALIAN));
                                    assertSearchFinds(page, name, name.toLowerCase(Locale.ITALIAN));
                                }
                        ),

                        new WebScenario<>(
                                "con almeno un risultato il paginatore è presente",
                                page -> {},
                                page -> page.pagination().readAndAssert(h -> Assertions.assertThat(h).isNotBlank())
                        )
                ));
    }

    private static void assertSearchFinds(PtInstitutionsPage page, String name, String search) {
        page.searchInstitutions(search);
        // la lista precedente resta visibile finché la ricerca non è conclusa
        eventually(() -> Assertions.assertThat(page.institutionNames().readAll())
                .isNotEmpty()
                .allSatisfy(found -> Assertions.assertThat(found).containsIgnoringCase(name)));
    }

    private static void eventually(ThrowingRunnable assertion) {
        Awaitility.await().atMost(TIMEOUT).ignoreExceptions().untilAsserted(assertion);
    }

    private static String noResultsTitle(String search) {
        return "Nessuna corrispondenza per “" + search + "”.";
    }
}
