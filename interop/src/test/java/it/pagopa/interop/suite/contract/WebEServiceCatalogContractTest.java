package it.pagopa.interop.suite.contract;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceCatalogPage;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Web Contract Test - Catalogo E-Service (/catalogo-e-service)")
public class WebEServiceCatalogContractTest {

    private static final String EXPECTED_PAGE_TITLE = "Catalogo degli e-service";
    private static final String EXPECTED_PAGE_DESCRIPTION = "Il catalogo degli e-service contiene tutte le basi dati che altri enti hanno reso disponibili. Puoi aderire a quelli di tuo interesse.";
    private static final String EXPECTED_SEARCH_NAME_PLACEHOLDER = "Cerca per nome";
    private static final String EXPECTED_SEARCH_PROVIDER_PLACEHOLDER = "Cerca per erogatore";
    private static final String EXPECTED_CARD_BUTTON_LABEL = "Visualizza";

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    @DisplayName("Verifica contratto ConsumerEServiceCatalogPage")
    Stream<DynamicTest> eServiceCatalogContractTests() {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceCatalogPage.class)
                .tests(Stream.of(
                        // CATALOG-01, 02, 03: Titolo h1 e Descrizione
                        new WebScenario<>(
                                "CATALOG-01..03: Verifica presenza, visibilità e copy h1 e descrizione",
                                page -> {},
                                page -> {
                                    page.pageTitle().readAndAssert(EXPECTED_PAGE_TITLE); //Verifica del titolo
                                    page.pageDescription().readAndAssert(EXPECTED_PAGE_DESCRIPTION); //Verifica della descrizione
                                }
                        ),

                        // CATALOG-04..07: Filtro Cerca per Nome
                        new WebScenario<>(
                                "CATALOG-04..07: Verifica campo 'Cerca per nome' e stato iniziale",
                                page -> {},
                                page -> {
                                    page.searchByName().readAndAssert(EXPECTED_SEARCH_NAME_PLACEHOLDER);
                                    assertSearchInputInitialState(page.searchByName().read());
                                }
                        ),

                        // CATALOG-08..11: Filtro Cerca per Erogatore
                        new WebScenario<>(
                                "CATALOG-08..11: Verifica filtro 'Cerca per erogatore' e stato iniziale",
                                page -> {},
                                page -> {
                                    page.searchByProvider().readAndAssert(EXPECTED_SEARCH_PROVIDER_PLACEHOLDER);
                                    assertSearchInputInitialState(page.searchByProvider().read());
                                }
                        ),

                        // CATALOG-12: Griglia risultati presente nel DOM
                        new WebScenario<>(
                                "CATALOG-12: Verifica presenza griglia card nel DOM",
                                page -> {},
                                page -> Assertions.assertThat(page.grid().get().isPresent())
                                        .as("Griglia E-Service presente nel DOM")
                                        .isTrue()
                        ),

                        // CATALOG-13..16: Contenuti descrittivi della prima Card a catalogo
                        new WebScenario<>(
                                "CATALOG-13..16: Verifica visibilità dei dettagli della prima card",
                                page -> {},
                                this::assertFirstCardDetailsAreVisible
                        ),

                        // CATALOG-17..19: Bottone azione sulla Card
                        new WebScenario<>(
                                "CATALOG-17..19: Verifica bottone 'Visualizza' nella card",
                                page -> {},
                                page -> page.cardActionButton().readAndAssert(EXPECTED_CARD_BUTTON_LABEL)
                        ),

                        // CATALOG-20: Paginazione presente nel DOM
                        new WebScenario<>(
                                "CATALOG-20: Verifica presenza elemento paginazione nel DOM",
                                page -> {},
                                page -> Assertions.assertThat(page.pagination().get().isPresent())
                                        .as("Paginazione presente nel DOM")
                                        .isTrue()
                        ),

                        // CATALOG-21: Messaggio 'Nessun risultato' ASSENTE nel DOM
                        new WebScenario<>(
                                "CATALOG-21: Verifica assenza messaggio lista vuota",
                                page -> {},
                                page -> Assertions.assertThat(page.emptyStateMessage().get().isPresent())
                                        .as("Messaggio 'nessun risultato' assente nel DOM")
                                        .isFalse()
                        )
                ));
    }

    /**
     * Verifica che il valore del campo di ricerca sia coerente con lo stato iniziale non valorizzato.
     */
    private void assertSearchInputInitialState(String value) {
        Assertions.assertThat(value)
                .as("Verifica valore iniziale campo ricerca")
                .isNotNull();
    }

    /**
     * Verifica la presenza dei campi informativi minimi nella prima card del catalogo.
     */
    private void assertFirstCardDetailsAreVisible(EServiceCatalogPage page) {
        Assertions.assertThat(page.cardProviderName().read())
                .as("Nome erogatore nella prima card")
                .isNotBlank();

        Assertions.assertThat(page.cardEServiceName().read())
                .as("Nome E-Service nella prima card")
                .isNotBlank();

        Assertions.assertThat(page.cardDescription().read())
                .as("Descrizione E-Service nella prima card")
                .isNotNull();
    }
}