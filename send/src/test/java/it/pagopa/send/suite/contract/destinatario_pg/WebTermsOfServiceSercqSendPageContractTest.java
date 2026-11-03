package it.pagopa.send.suite.contract.destinatario_pg;

import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.UserAlias;
import it.pagopa.send.common.user.domain.UserProvider;
import it.pagopa.send.web.destinatario_pg.infrastructure.page.TermsOfServiceSercqSendPGPage;
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
 * Contract test della pagina dei termini e condizioni d'uso del domicilio digitale SERCQ della persona giuridica
 * ({@code {baseUrl}/termini-di-servizio/sercq-send}).
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
public class WebTermsOfServiceSercqSendPageContractTest {

    // testi attesi

    private static final String TITLE = "Termini e condizioni d'uso";
    private static final String INTRODUCTION = "I presenti termini (di seguito “ToS”) regolano il rapporto tra gli utenti";
    private static final List<String> SECTION_TITLES = List.of(
            "1. Descrizione del SERCQ",
            "2. Costi",
            "3. Siti di terze parti",
            "4. Assistenza all'Utente e debug",
            "5. Responsabilità e obblighi dell'Utente",
            "6. Misure a disposizione della Società",
            "7. Esclusioni e limitazione di responsabilità",
            "8. Modifiche ai ToS",
            "9. Proprietà intellettuale",
            "10. Legge applicabile e foro competente"
    );
    // l'indice ha in più "Introduzione" per la prima sezione e una voce per l'ultima, che non ha titolo
    private static final List<String> INDEX = Stream.of(
            Stream.of("Introduzione"),
            SECTION_TITLES.stream(),
            Stream.of("1341 e 1342 c.c.")
    ).flatMap(s -> s).toList();

    private final WebBrowserContractValidator webContractValidator;
    private final UserProvider userProvider;

    /**
     * Testi della pagina: titolo, inizio dell'introduzione e titoli delle 10 sezioni.
     */
    @TestFactory
    Stream<DynamicTest> shouldShowTermsOfServiceTexts() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.IMPRESA_1))
                .on(TermsOfServiceSercqSendPGPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<TermsOfServiceSercqSendPGPage>> textScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "titolo e introduzione",
                        page -> {},
                        page -> {
                            page.title().readAndAssert(h -> Assertions.assertThat(h).isEqualTo(TITLE));
                            page.introduction().readAndAssert(h -> Assertions.assertThat(h).startsWith(INTRODUCTION));
                        }
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
    Stream<DynamicTest> shouldShowTermsOfServiceIndex() {
        return webContractValidator.asRecipient(userProvider.resolve(UserAlias.IMPRESA_1))
                .on(TermsOfServiceSercqSendPGPage.class)
                .tests(indexScenarios());
    }

    private Stream<WebScenario<TermsOfServiceSercqSendPGPage>> indexScenarios() {
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
                                    .allSatisfy(href -> Assertions.assertThat(href).contains("/termini-di-servizio/sercq-send#otnotice-section-"));
                        }
                )
        );
    }
}
