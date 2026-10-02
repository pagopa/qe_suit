package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.tos.infrastructure.page.TOSPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.net.URI;
import java.util.ArrayList;
import java.util.stream.Stream;

/** Visual checks: DOM structure and essential copy, excluding layout and full legal text. */
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebTOSContractTest {

    private static final int EXPECTED_INDEX_COUNT = 2;
    private static final List<SectionExpectation> EXPECTED_SECTIONS = List.of(
            new SectionExpectation("Introduzione", "Termini e condizioni d'uso"),
            new SectionExpectation("Descrizione del servizio", "1. Descrizione del servizio"),
            new SectionExpectation("Costi", "2. Costi"),
            new SectionExpectation("Siti web di terze parti", "3. Siti web di terze parti"),
            new SectionExpectation("Assistenza", "4. Assistenza all’utente e debug"),
            new SectionExpectation("Obblighi dell'utente", "5. Responsabilità e obblighi dell’utente"),
            new SectionExpectation("Misure della Società", "6. Misure a disposizione della Società"),
            new SectionExpectation("Responsabilità", "7. Esclusioni e limitazione di responsabilità"),
            new SectionExpectation("Modifiche ai ToS", "8. Modifiche ai ToS"),
            // Only the numbering awaits confirmation; the section identity is checked.
            new SectionExpectation("Proprietà intellettuale", null),
            new SectionExpectation("Legge e foro", "10. Legge applicabile e foro competente")
    );

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldDisplayTermsOfService() {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(TOSPage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<TOSPage>> scenarios() {
        return Stream.of(
                new WebScenario<>("Visuale TOS: titolo pagina", page -> {},
                        page -> Assertions.assertThat(page.pageTitle().read())
                                .as("Copy titolo pagina TOS").isEqualTo("Termini di servizio")),
                new WebScenario<>("Visuale TOS: sezioni e copy essenziali", page -> {}, this::assertSections),
                new WebScenario<>("Visuale TOS: indice e destinazioni nel DOM", page -> {}, this::assertIndexes)
        );
    }

    private void assertSections(TOSPage page) {
        var sections = page.sections();
        Assertions.assertThat(sections).as("Sezioni del documento TOS").hasSize(EXPECTED_SECTIONS.size());
        for (int i = 0; i < sections.size(); i++) {
            var section = sections.get(i);
            var expected = EXPECTED_SECTIONS.get(i);
            Assertions.assertThat(section.id()).as("Identificativo: %s", expected.name()).isNotBlank();
            Assertions.assertThat(section.content().readAll())
                    .as("Paragrafi: %s", expected.name()).isNotEmpty().allSatisfy(text ->
                            Assertions.assertThat(text).isNotBlank());
            String heading = section.heading().read();
            Assertions.assertThat(heading).as("Titolo: %s", expected.name()).isNotBlank();
            if (expected.copy() != null) {
                Assertions.assertThat(heading).as("Copy: %s", expected.name()).isEqualTo(expected.copy());
            } else {
                Assertions.assertThat(heading.replaceFirst("^\\d+\\.\\s*", ""))
                        .as("Titolo senza numerazione: %s", expected.name()).isEqualTo(expected.name());
            }
        }
    }

    private void assertIndexes(TOSPage page) {
        List<String> sectionIds = page.sections().stream().map(section -> section.id()).toList();
        Assertions.assertThat(sectionIds).as("Identificativi univoci delle sezioni TOS")
                .hasSize(EXPECTED_SECTIONS.size()).doesNotHaveDuplicates().allSatisfy(id ->
                        Assertions.assertThat(id).isNotBlank());
        URI pageUri = URI.create(page.getUrl().getUrl());
        String baseHref = page.documentBaseHref();
        Assertions.assertThat(baseHref).as("Base URL del documento").isNotBlank();
        URI documentBase = pageUri.resolve(baseHref);
        var indexes = page.indexes();
        Assertions.assertThat(indexes).as("Indici TOS presenti nel DOM, incluse varianti nascoste")
                .hasSize(EXPECTED_INDEX_COUNT);
        for (int i = 0; i < indexes.size(); i++) {
            List<String> hrefs = indexes.get(i).hrefs();
            Assertions.assertThat(hrefs).as("Collegamenti indice %s", i + 1).hasSize(EXPECTED_SECTIONS.size());
            List<String> targets = new ArrayList<>();
            for (String href : hrefs) {
                Assertions.assertThat(href).as("Href indice %s", i + 1).isNotBlank();
                URI target = documentBase.resolve(href);
                Assertions.assertThat(target.getScheme()).as("Schema collegamento %s", href).isEqualTo(pageUri.getScheme());
                Assertions.assertThat(target.getRawAuthority()).as("Host collegamento %s", href).isEqualTo(pageUri.getRawAuthority());
                Assertions.assertThat(target.getPath()).as("Pagina collegamento %s", href).isEqualTo(pageUri.getPath());
                Assertions.assertThat(target.getQuery()).as("Query collegamento %s", href).isEqualTo(pageUri.getQuery());
                targets.add(target.getFragment());
            }
            Assertions.assertThat(targets).as("Destinazioni univoche e ordinate dell'indice %s", i + 1)
                    .containsExactlyElementsOf(sectionIds);
        }
    }

    private record SectionExpectation(String name, String copy) {}
}
