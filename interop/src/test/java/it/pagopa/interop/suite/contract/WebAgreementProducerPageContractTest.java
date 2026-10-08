package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.frontend.e2e.framework.web.domain.Component;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.agreement.infrastructure.page.AgreementProducerPage;
import it.pagopa.interop.web.agreement.infrastructure.page.component.AgreementProducerFilter;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Contract test della pagina "Richieste di fruizione ricevute" (/erogazione/richieste).
 * <p>
 * Contesto C1: sessione autenticata, Comune di Milano, Admin, apertura diretta della rotta, ambiente QA.
 * Implementa solo i controlli certi di matrix.md. Non implementati (motivo):
 * <ul>
 *     <li>ES-AG-01.09-01.11, 03.11-03.12, 04.02-04.03: richiedono dati QA non definiti</li>
 *     <li>ES-AG-01.12, 03.13: contesto C2 (menù), da verificare separatamente</li>
 *     <li>ES-AG-01.18, 01.19, 03.14, 03.15, 04.05: da confermare con TM / copy della libreria mancanti</li>
 *     <li>ES-AG-02: requisiti di visibilità non confermati</li>
 *     <li>ES-AG-03.06: richiede l'apertura del filtro (interazione)</li>
 * </ul>
 */
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAgreementProducerPageContractTest {

    private static final String DESCRIPTION = "Consulta e gestisci le richieste che gli enti ti hanno inoltrato per fruire dei tuoi e-service. Le richieste che ricevi sono raccolte in questa area.";

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Verifica la presenza nel DOM dei componenti attesi (h1, descrizione, 3 filtri, tabella,
     * intestazioni, paginazione) e l'assenza dello skeleton a caricamento completato.
     * <p>
     * Contesto: Admin Comune di Milano, apertura diretta. ID matrice: ES-AG-01.01-01.08, 01.13.
     * Precondizioni dati: nessuna.
     */
    @TestFactory
    Stream<DynamicTest> pageMustContainExpectedComponents() {
        return run(Stream.of(
                scenario("ES-AG-01.01 - Titolo h1 presente",
                        page -> assertPresent(page.pageTitle(), "Titolo h1")),
                scenario("ES-AG-01.02 - Descrizione presente",
                        page -> assertPresent(page.description(), "Descrizione")),
                scenario("ES-AG-01.03 - Filtro e-service presente",
                        page -> assertThatFilterPresent(page.eServiceFilter(), "Filtro 'Cerca per e-service'")),
                scenario("ES-AG-01.04 - Filtro fruitore presente",
                        page -> assertThatFilterPresent(page.consumerFilter(), "Filtro 'Cerca per fruitore'")),
                scenario("ES-AG-01.05 - Filtro stato presente",
                        page -> assertThatFilterPresent(page.statusFilter(), "Filtro 'Stato della richiesta'")),
                scenario("ES-AG-01.06 - Tabella presente",
                        page -> Assertions.assertThat(page.table().isPresent())
                                .as("Tabella richieste: attesa presente nel DOM").isTrue()),
                scenario("ES-AG-01.07 - Intestazioni colonna presenti",
                        page -> Assertions.assertThat(page.table().headerLabels())
                                .as("Intestazioni colonna: attese 4").hasSize(4)),
                scenario("ES-AG-01.08 - Paginazione presente",
                        page -> assertPresent(page.pagination(), "Paginazione")),
                scenario("ES-AG-01.13 - Skeleton assente a caricamento completato",
                        page -> {
                            page.waitUntilTableLoaded();
                            assertAbsent(page.tableSkeleton(), "Skeleton della tabella");
                        })
        ));
    }

    /**
     * Verifica l'assenza nel DOM dei componenti non previsti lato erogatore.
     * <p>
     * Contesto: Admin Comune di Milano, apertura diretta. ID matrice: ES-AG-01.14-01.17.
     * Precondizioni dati: nessuna (il pulsante "Modifica" è verificato come assente a prescindere dalle righe).
     */
    @TestFactory
    Stream<DynamicTest> pageMustNotContainUnexpectedComponents() {
        return run(Stream.of(
                scenario("ES-AG-01.14 - Pulsante Modifica assente",
                        page -> {
                            page.waitUntilTableLoaded();
                            assertAbsent(page.editButton(), "Pulsante 'Modifica'");
                        }),
                scenario("ES-AG-01.15 - Filtro erogatore assente",
                        page -> Assertions.assertThat(page.producerFilter().isPresent())
                                .as("Filtro 'Cerca per erogatore': atteso assente").isFalse()),
                scenario("ES-AG-01.16 - Colonna Erogatore assente",
                        page -> assertAbsent(page.producerColumnHeader(), "Colonna 'Erogatore'")),
                scenario("ES-AG-01.17 - Pulsante di creazione assente",
                        page -> assertAbsent(page.createButton(), "Pulsante di creazione"))
        ));
    }

    /**
     * Verifica i copy esatti di titolo, descrizione, label dei filtri e intestazioni di colonna.
     * <p>
     * Contesto: Admin Comune di Milano, apertura diretta. ID matrice: ES-AG-03.01-03.05, 03.07-03.10.
     * Precondizioni dati: nessuna.
     */
    @TestFactory
    Stream<DynamicTest> pageMustShowExpectedTexts() {
        return run(Stream.of(
                scenario("ES-AG-03.01 - Testo h1",
                        page -> assertText("Titolo h1", page.pageTitle().read(), AgreementProducerPage.TITLE)),
                scenario("ES-AG-03.02 - Testo descrizione",
                        page -> assertText("Descrizione", page.description().read(), DESCRIPTION)),
                scenario("ES-AG-03.03 - Label filtro e-service",
                        page -> assertText("Label filtro e-service", page.eServiceFilter().label().read(), "Cerca per e-service")),
                scenario("ES-AG-03.04 - Label filtro fruitore",
                        page -> assertText("Label filtro fruitore", page.consumerFilter().label().read(), "Cerca per fruitore")),
                scenario("ES-AG-03.05 - Label filtro stato",
                        page -> assertText("Label filtro stato", page.statusFilter().label().read(), "Stato della richiesta")),
                scenario("ES-AG-03.07/03.08/03.09/03.10 - Intestazioni colonna",
                        page -> Assertions.assertThat(page.table().readHeaderLabels())
                                .as("Intestazioni colonna (in ordine)")
                                .containsExactlyElementsOf(List.of("E-service", "Fruitore", "Stato richiesta", "")))
        ));
    }

    /**
     * Verifica lo stato iniziale prima di qualsiasi interazione: filtri abilitati e senza selezione,
     * paginazione sulla pagina 1.
     * <p>
     * Contesto: Admin Comune di Milano, apertura diretta. ID matrice: ES-AG-04.01, 04.04 (solo pagina 1).
     * Precondizioni dati: nessuna. Righe per pagina di default non verificato (valore mancante).
     */
    @TestFactory
    Stream<DynamicTest> pageMustBeInInitialState() {
        return run(Stream.of(
                scenario("ES-AG-04.01 - Filtri abilitati e senza selezione",
                        page -> SoftAssertions.assertSoftly(softly -> {
                            assertFilterInitialState(softly, "e-service", page.eServiceFilter());
                            assertFilterInitialState(softly, "fruitore", page.consumerFilter());
                            assertFilterInitialState(softly, "stato", page.statusFilter());
                        })),
                scenario("ES-AG-04.04 - Paginazione sulla pagina 1",
                        page -> assertText("Pagina selezionata", page.selectedPageButton().read().trim(), "1"))
        ));
    }

    private static void assertFilterInitialState(SoftAssertions softly, String name, AgreementProducerFilter filter) {
        softly.assertThat(filter.isEnabled()).as("Filtro " + name + ": atteso abilitato").isTrue();
        softly.assertThat(filter.hasSelection()).as("Filtro " + name + ": atteso senza selezione").isFalse();
    }

    private static void assertThatFilterPresent(AgreementProducerFilter filter, String name) {
        Assertions.assertThat(filter.isPresent()).as(name + ": atteso presente nel DOM").isTrue();
    }

    private static void assertPresent(Component component, String name) {
        Assertions.assertThat(component.get(FindPolicy.PRESENT).isPresent())
                .as(name + ": atteso presente nel DOM").isTrue();
    }

    private static void assertAbsent(Component component, String name) {
        Assertions.assertThat(component.get(FindPolicy.PRESENT).isPresent())
                .as(name + ": atteso assente dal DOM").isFalse();
    }

    private static void assertText(String element, String actual, String expected) {
        Assertions.assertThat(actual).as(element + ": testo atteso").isEqualTo(expected);
    }

    private static WebScenario<AgreementProducerPage> scenario(String name, Consumer<AgreementProducerPage> assertion) {
        return new WebScenario<>(name, page -> {
        }, assertion);
    }

    private Stream<DynamicTest> run(Stream<WebScenario<AgreementProducerPage>> scenarios) {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(AgreementProducerPage.class)
                .tests(scenarios);
    }
}

