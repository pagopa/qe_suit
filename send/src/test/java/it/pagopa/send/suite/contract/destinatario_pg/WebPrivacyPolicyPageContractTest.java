package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.PrivacyPolicyPGPage;
import it.pagopa.send.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test della pagina dell'informativa privacy di SEND della persona giuridica
 * ({@code {baseUrl}/informativa-privacy}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>indice delle sezioni.</li>
 * </ul>
 * La rotta è pubblica, ma la pagina si apre come impresa {@code IMPRESA_1}: l'infrastruttura dei contract test apre
 * le pagine solo con un utente autenticato. Il testo è un documento legale caricato da OneTrust: se cambiano i titoli
 * delle sezioni il test fallisce e va aggiornato insieme al documento.
 */
@SpringBootTest(classes = {
        TestBootApp.class,
        JunitContextConfig.class,
        WebJUnitSuitConfig.class
}, properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebPrivacyPolicyPageContractTest {

    // testi attesi

    private static final String TITLE = "Informativa sul trattamento dei dati personali";
    private static final List<String> SECTION_TITLES = List.of(
            "Titolare del trattamento",
            "Responsabile Protezione Dati",
            "Categorie di dati e finalità",
            "Base giuridica del trattamento",
            "Categorie di destinatari",
            "Trasferimenti verso paesi terzi",
            "Periodo di conservazione",
            "Diritti degli interessati",
            "Traduzioni",
            "Modifiche",
            "Cookie, traccianti e altre informazioni memorizzate"
    );
    // l'indice ha in più "Introduzione" per la prima sezione, quella con il titolo dell'informativa
    private static final List<String> INDEX = Stream.concat(Stream.of("Introduzione"), SECTION_TITLES.stream()).toList();

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Testi della pagina: titolo e titoli delle 11 sezioni. Il testo sotto il titolo non viene verificato: oggi è un
     * avviso temporaneo di OneTrust, non l'introduzione del documento.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowPrivacyPolicyTexts() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.IMPRESA_1))
                .on(PrivacyPolicyPGPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<PrivacyPolicyPGPage>> textScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "titolo",
                        page -> {},
                        page -> page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE))
                ),

                new WebScenario<>(
                        "titoli delle sezioni",
                        page -> {},
                        page -> page.sectionTitles().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(SECTION_TITLES))
                )
        );
    }

    /**
     * Indice delle sezioni: le voci, nell'ordine, e il link di ognuna a una sezione della pagina ({@code
     * #otnotice-section-…}). La pagina ha due indici, uno per desktop e uno nascosto per mobile: si usa quello per
     * desktop.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowPrivacyPolicyIndex() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.IMPRESA_1))
                .on(PrivacyPolicyPGPage.class)
                .tests(indexScenarios());
    }

    private Stream<WebScenario<PrivacyPolicyPGPage>> indexScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "voci dell'indice",
                        page -> {},
                        page -> page.indexLinks().readAllAndAssert(h -> Assertions.assertThat(h).containsExactlyElementsOf(INDEX))
                ),

                new WebScenario<>(
                        "ogni voce dell'indice porta a una sezione della pagina",
                        page -> {},
                        page -> {
                            List<String> hrefs = page.indexLinks().getAll().orElse(List.of()).stream()
                                    .map(WebPresentationElement::getAttributes)
                                    .map(attributes -> attributes.get("href"))
                                    .toList();
                            Assertions.assertThat(hrefs)
                                    .hasSize(page.sections().readAll().size())
                                    .allSatisfy(href -> Assertions.assertThat(href).contains("/informativa-privacy#otnotice-section-"));
                        }
                )
        );
    }
}
