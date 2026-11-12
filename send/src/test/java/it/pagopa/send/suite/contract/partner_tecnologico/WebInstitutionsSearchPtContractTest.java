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
import org.assertj.core.api.Assumptions;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.time.Duration;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Contract test della ricerca nella pagina "I tuoi enti" del BackOffice dei Partner Tecnologici, aperta con la sessione
 * del PT {@code PT_1}. Se il PT non ha enti deleganti, prima si crea la delega di {@code COMUNE_1}.
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
    private static final String OTHER_NO_MATCH = "qqqzzz";    private static final String MIN_LENGTH_ERROR = "Inserisci almeno tre caratteri";
    // limite del parametro search di GET /bff/v1/pt/institutions
    private static final int MAX_LENGTH = 250;
    // 255 caratteri: ogni blocco di 10 finisce con la sua posizione (xxxxxxx010 ... xxxxxxx250), poi "abcde" oltre il
    // massimo, così dal valore letto si vede dove si ferma il campo
    private static final String TOO_LONG = IntStream.rangeClosed(1, MAX_LENGTH / 10)
            .mapToObj(block -> String.format("%10s", String.format("%03d", block * 10)).replace(' ', 'x'))
            .collect(Collectors.joining()) + "abcde";
    private static final int ROWS_PER_PAGE = 10;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;
    private final SendJourney sendJourney;

    /**
     * Ricerca degli enti:
     * <ul>
     *     <li>con meno di 3 caratteri compare l'errore e "Cerca" resta abilitato;</li>
     *     <li>una stringa senza corrispondenze mostra il messaggio di nessun risultato;</li>
     *     <li>non si possono scrivere più caratteri del massimo;</li>
     *     <li>dopo aver svuotato il filtro con la X si può fare una nuova ricerca;</li>
     *     <li>la ricerca non distingue tra maiuscole e minuscole;</li>
     *     <li>con i risultati in una sola pagina il paginatore non ha pulsanti di pagina.</li>
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
                                "numero massimo di caratteri: non si scrive oltre il massimo",
                                page -> page.searchInput().cleanAndWrite(TOO_LONG),
                                // TODO: oggi il campo accetta tutti i caratteri e il BFF risponde 400 oltre i 250
                                page -> page.searchInput().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TOO_LONG.substring(0, MAX_LENGTH)))
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
                                "risultati in una sola pagina: nessun pulsante di pagina",
                                page -> {},
                                page -> {
                                    // con meno righe del massimo per pagina i risultati stanno in una sola pagina
                                    Assumptions.assumeThat(page.institutionNames().readAll()).hasSizeLessThan(ROWS_PER_PAGE);
                                    Assertions.assertThat(page.pageButtons().readAll()).as("pulsanti di pagina").isEmpty();
                                    page.rowsPerPage().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(String.valueOf(ROWS_PER_PAGE)));
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
