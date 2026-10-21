package it.pagopa.interop.suite.contract;

import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCatalogPage;
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

    private static final String EXPECTED_PAGE_DESCRIPTION = "Il catalogo degli e-service contiene tutte le basi dati che altri enti hanno reso disponibili. Puoi aderire a quelli di tuo interesse.";
    private static final String EXPECTED_CARD_BUTTON_LABEL = "Vai all'e-service";

    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    @DisplayName("Verifica contratto ConsumerEServiceCatalogPage")
    Stream<DynamicTest> eServiceCatalogContractTests() {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceCatalogPage.class)
                .tests(Stream.of(
                        new WebScenario<>("Verifica contratto ConsumerEServiceCatalogPage",
                                page -> {
                                },
                                page -> {
                                    // CATALOG-01, 02, 03: Titolo h1 e Descrizione
                                    page.pageTitle().readAndAssert(EServiceCatalogPage.PAGE_TITLE); //Verifica del titolo
                                    page.pageDescription().readAndAssert(EXPECTED_PAGE_DESCRIPTION); //Verifica della descrizione

                                    // CATALOG-04..07: Filtro Cerca per parola chiave
                                    page.searchByName().readAndAssert(EServiceCatalogPage.SEARCH_BY_KEY_PLACEHOLDER);
                                    assertSearchInputInitialState(page.searchByName().read());

                                    // CATALOG-08..11: Filtro Cerca per Erogatore
                                    page.searchByProvider().readAndAssert(EServiceCatalogPage.SEARCH_BY_PROVIDER_PLACEHOLDER);
                                    assertSearchInputInitialState(page.searchByProvider().read());

                                    // CATALOG-04: Verifica bottone "Cerca"
                                    Assertions.assertThat(page.searchButton().get().isPresent())
                                            .as("Il bottone Cerca deve essere presente").isTrue();
                                    Assertions.assertThat(page.searchButton().isDisabled())
                                            .as("Il bottone Cerca deve essere abilitato").isFalse();

                                    // CATALOG-05: Verifica bottone "Filtri"
                                    Assertions.assertThat(page.filtersButton().get().isPresent())
                                            .as("Il bottone Filtri deve essere presente").isTrue();
                                    Assertions.assertThat(page.filtersButton().isDisabled())
                                            .as("Il bottone Filtri deve essere abilitato").isFalse();

                                    // CATALOG-06: Verifica label contatore risultati
                                    Assertions.assertThat(page.resultsCounterLabel().get().isPresent())
                                            .as("La label dei risultati deve essere presente").isTrue();
                                    Assertions.assertThat(page.resultsCounterLabel().read())
                                            .as("La label deve contenere la dicitura 'risultati'")
                                            .contains("risultati");

                                    // CATALOG-07: Verifica bottone ordinamento
                                    Assertions.assertThat(page.sortButton().get().isPresent())
                                            .as("Il bottone di ordinamento deve essere presente").isTrue();
                                    Assertions.assertThat(page.sortButton().isDisabled())
                                            .as("Il bottone di ordinamento deve essere abilitato").isFalse();
                                    Assertions.assertThat(page.sortButton().read())
                                            .as("L'ordinamento di default deve essere 'Dal più recente'")
                                            .contains("Dal più recente");

                                    // CATALOG-12: Griglia risultati presente nel DOM
                                    Assertions.assertThat(page.grid().get().isPresent())
                                            .as("Griglia E-Service presente nel DOM")
                                            .isTrue();

                                    // CATALOG-13..16: Contenuti descrittivi della prima Card a catalogo
                                    this.assertFirstCardDetailsAreVisible(page);

                                    // CATALOG-17..19: Bottone azione sulla Card
                                    page.cardActionButton().readAndAssert(EXPECTED_CARD_BUTTON_LABEL);

                                    // CATALOG-20: Paginazione presente nel DOM
                                    Assertions.assertThat(page.pagination().get().isPresent())
                                            .as("Paginazione presente nel DOM")
                                            .isTrue();

                                    // CATALOG-21: Messaggio 'Nessun risultato' ASSENTE nel DOM
                                    Assertions.assertThat(page.emptyStateMessage().get().isPresent())
                                            .as("Messaggio 'nessun risultato' assente nel DOM")
                                            .isFalse();
                                })

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