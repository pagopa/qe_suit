package it.pagopa.send.suite.contract.partner_tecnologico;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.journey.application.SendJourney;
import it.pagopa.send.common.user.domain.User;
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
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.time.Duration;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Contract test della ricerca nella pagina "I tuoi enti" del BackOffice dei Partner Tecnologici, aperta con la sessione
 * del PT {@code PT_1}. Se il PT non ha enti deleganti, prima si crea la delega di {@code COMUNE_1}.
 * <p>
 * Testi e selettori vengono dal sorgente di pn-pt-webapp (PN-21981).
 */
@Tag("backofficePt")
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
    // limite del parametro search di GET /bff/v1/pt/institutions: il campo accetta testo più lungo, l'errore compare alla ricerca
    private static final int MAX_LENGTH = 250;
    private static final String TOO_LONG = "x".repeat(MAX_LENGTH + 1);
    private static final String MAX_LENGTH_ERROR = "Scrivi massimo " + MAX_LENGTH + " caratteri";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;
    private final SendJourney sendJourney;

    /**
     * Ricerca degli enti:
     * <ul>
     *     <li>con meno di 3 caratteri compare l'errore e "Cerca" resta abilitato;</li>
     *     <li>una stringa senza corrispondenze mostra il messaggio di nessun risultato;</li>
     *     <li>oltre il numero massimo di caratteri compare l'errore;</li>
     *     <li>dopo aver svuotato il filtro con la X si può fare una nuova ricerca;</li>
     *     <li>la ricerca non distingue tra maiuscole e minuscole;</li>
     *     <li>con i risultati in una sola pagina (la ricerca per codice fiscale del primo ente della lista, che trova solo
     *     quell'ente) il paginatore non ha né pulsanti di pagina né il numero di righe per pagina.</li>
     * </ul>
     */
    @TestFactory
    Stream<DynamicTest> shouldSearchInstitutions() {
        User partner = userProvider.resolve(UserAlias.PT_1);
        // prima di aprire la pagina: se il PT non ha enti deleganti si crea la delega di COMUNE_1
        sendJourney.withPtDelegation(userProvider.resolve(UserAlias.COMUNE_1), partner);
        return webContractValidator.as(partner)
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
                                "oltre il numero massimo di caratteri: errore",
                                page -> page.searchInstitutions(TOO_LONG),
                                page -> page.searchError().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(MAX_LENGTH_ERROR))
                        ),

                        new WebScenario<>(
                                "filtro svuotato con la X e nuova ricerca",
                                page -> {
                                    page.searchInstitutions(NO_MATCH);
                                    // la X compare solo con il focus nel campo
                                    page.searchInput().write("");
                                    page.clearSearchButton().click();
                                },
                                page -> {
                                    page.searchInput().readAndAssert(h -> Assertions.assertThat(h).isEmpty());
                                    page.searchInstitutions(OTHER_NO_MATCH);
                                    // resta visibile il titolo della ricerca precedente finché la nuova non è conclusa
                                    eventually(() -> Assertions.assertThat(page.noResultsTitle().read()).isEqualTo(noResultsTitle(OTHER_NO_MATCH)));
                                }
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
                                "risultati in una sola pagina: né pulsanti di pagina né righe per pagina",
                                page -> page.searchInstitutions(page.institutionTaxCodes().read()),
                                page -> {
                                    // la lista precedente resta visibile finché la ricerca non è conclusa
                                    eventually(() -> Assertions.assertThat(page.institutionNames().readAll()).as("enti trovati").hasSize(1));
                                    Assertions.assertThat(page.pageButtons().readAll()).as("pulsanti di pagina").isEmpty();
                                    Assertions.assertThat(page.rowsPerPage().readAll()).as("numero di righe per pagina").isEmpty();
                                }
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
