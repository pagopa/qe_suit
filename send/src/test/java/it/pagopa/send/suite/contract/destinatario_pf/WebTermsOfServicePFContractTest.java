package it.pagopa.send.suite.contract.destinatario_pf;

import it.frontend.e2e.framework.web.model.WebPresentationElement;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.send.TestBootApp;
import it.pagopa.send.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.send.common.infrastructure.config.JunitContextConfig;
import it.pagopa.send.common.user.domain.Recipient;
import it.pagopa.send.web.destinatario_pf.infrastructure.page.TermsOfServicePFPage;
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

import java.util.List;
import java.util.stream.Stream;

/**
 * Contract test della pagina dei termini e condizioni d'uso di SEND ({@code {baseUrl}/termini-di-servizio}).
 * <p>
 * I test sono organizzati per:
 * <ul>
 *     <li>testi della pagina;</li>
 *     <li>indice delle sezioni.</li>
 * </ul>
 * Il testo è un documento legale caricato da OneTrust: se cambiano i titoli delle sezioni il test fallisce e va
 * aggiornato insieme al documento.
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
public class WebTermsOfServicePFContractTest {

    // testi attesi

    private static final String TITLE = "Termini e condizioni d'uso";
    private static final String INTRODUCTION = "I presenti termini e condizioni d’uso";
    private static final List<String> SECTION_TITLES = List.of(
            "1. Descrizione del servizio",
            "2. Identificazione e accesso alla Piattaforma",
            "3. Delega per l’accesso e nomina persona di fiducia per il ritiro presso FSU/altro soggetto autorizzato",
            "4. Elezione domicilio digitale e invio digitale",
            "5. Criterio di utilizzo dei domicili digitali esistenti per il destinatario",
            "6. Recapito digitale e avviso di cortesia",
            "7. Notificazione in modalità analogica",
            "8. Costi e Spese di notificazione",
            "9. Assistenza all’Utente e malfunzionamenti della Piattaforma",
            "10. Responsabilità e obblighi dell’Utente",
            "11. Esclusioni e limitazione di responsabilità della Società",
            "12. Privacy",
            "13. Modifiche ai ToS",
            "14. Legge applicabile e foro competente",
            "15. Traduzioni"
    );
    // l'indice ha in più "Introduzione" per la prima sezione e una voce per l'ultima, che non ha titolo
    private static final List<String> INDEX = Stream.of(
            Stream.of("Introduzione"),
            SECTION_TITLES.stream(),
            Stream.of("1341 e 1342 c.c.")
    ).flatMap(s -> s).toList();

    private final WebBrowserContractValidator webContractValidator;

    // testi della pagina

    @TestFactory
    Stream<DynamicTest> shouldShowTermsOfServiceTexts() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(TermsOfServicePFPage.class)
                .tests(textScenarios());
    }

    private Stream<WebScenario<TermsOfServicePFPage>> textScenarios() {
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

    // indice delle sezioni

    @TestFactory
    Stream<DynamicTest> shouldShowTermsOfServiceIndex() {
        return webContractValidator.asRecipient(Recipient.LUCREZIA)
                .on(TermsOfServicePFPage.class)
                .tests(indexScenarios());
    }

    private Stream<WebScenario<TermsOfServicePFPage>> indexScenarios() {
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
                                    .allSatisfy(href -> Assertions.assertThat(href).contains("/termini-di-servizio#otnotice-section-"));
                        }
                )
        );
    }
}
