package it.pagopa.interop.suite.contract;

import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCatalogFiltersDrawer;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCatalogPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(  classes = {
        TestBootApp.class,JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit")
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
                                    Assertions.assertThat(!page.eServiceCards().isEmpty())
                                            .as("Griglia E-Service presente nel DOM")
                                            .isTrue();

                                    // CATALOG-13..16: Contenuti descrittivi della prima Card a catalogo
                                    this.assertFirstCardDetailsAreVisible(page);

                                    // CATALOG-17..19: Bottone azione sulla Card
                                    page.eServiceCards().get(0).goToEServiceButton().readAndAssert(EXPECTED_CARD_BUTTON_LABEL);

                                    // CATALOG-20: Paginazione presente nel DOM
                                    Assertions.assertThat(page.pagination().get().isPresent())
                                            .as("Paginazione presente nel DOM")
                                            .isTrue();
                                }),
                         new WebScenario<>("Verifica contratto drawer \"Filtri\" catalogo E-Service",
                                page -> page.filtersButton().click(),
                                page -> {
                                    //DRAWER-01: Drawer filtri presente nel DOM
                                    Assertions.assertThat(page.filtersDrawer().get().isPresent())
                                            .as("Drawer filtri presente nel DOM").isTrue();

                                    var drawer = page.filtersDrawer();
                                    //DRAWER-02: Titolo drawer
                                    drawer.title().readAndAssert(EServiceCatalogFiltersDrawer.TITLE);
                                    //DRAWER-03..05: Pulsanti drawer
                                    Assertions.assertThat(drawer.closeButton().isDisabled())
                                            .as("Pulsante chiusura abilitato").isFalse();
                                    Assertions.assertThat(drawer.removeFiltersButton().isDisabled())
                                            .as("Pulsante Rimuovi filtri abilitato").isFalse();
                                    Assertions.assertThat(drawer.applyFiltersButton().isDisabled())
                                            .as("Pulsante Applica abilitato").isFalse();

                                    assertQuickSelectionSection(drawer);
                                    assertTemplateSection(drawer);
                                    assertTechnicalSpecsSection(drawer);
                                    assertDelegationsSection(drawer);
                                })

                ));
    }

    private void assertQuickSelectionSection(EServiceCatalogFiltersDrawer drawer) {
        //DRAWER-06..10: Sezione Selezione rapida
        drawer.quickSelectionTitle().readAndAssert(EServiceCatalogFiltersDrawer.SECTION_QUICK_SELECTION);
        Assertions.assertThat(drawer.activeEservicesSwitch().isDisabled())
                .as("Switch E-service attivi abilitato").isFalse();
        Assertions.assertThat(drawer.usableByTenantSwitch().isDisabled())
                .as("Switch Fruibili dal tuo ente abilitato").isFalse();
        Assertions.assertThat(drawer.alreadyInUseSwitch().isDisabled())
                .as("Switch Già in fruizione abilitato").isFalse();
        Assertions.assertThat(drawer.providerCategoryInput().get().isPresent())
                .as("Input Categoria erogatore presente nel DOM").isTrue();
    }

    private void assertTemplateSection(EServiceCatalogFiltersDrawer drawer) {
        //DRAWER-11..13: Sezione Template
        drawer.templateTitle().readAndAssert(EServiceCatalogFiltersDrawer.SECTION_TEMPLATE);
        Assertions.assertThat(drawer.createdFromTemplateSwitch().isDisabled())
                .as("Switch Creati da template abilitato").isFalse();
        Assertions.assertThat(drawer.linkedPurposeTemplateSwitch().isDisabled())
                .as("Switch Con template finalità collegati abilitato").isFalse();
    }

    private void assertTechnicalSpecsSection(EServiceCatalogFiltersDrawer drawer) {
        //DRAWER-14..16: Sezione Specifiche tecniche
        drawer.technicalSpecsTitle().readAndAssert(EServiceCatalogFiltersDrawer.SECTION_TECHNICAL_SPECS);
        Assertions.assertThat(drawer.dataExchangeInput().get().isPresent())
                .as("Input Scambio dati presente nel DOM").isTrue();
        Assertions.assertThat(drawer.providesOrReceivesDataInput().get().isPresent())
                .as("Input Eroga o riceve dati presente nel DOM").isTrue();
    }

    private void assertDelegationsSection(EServiceCatalogFiltersDrawer drawer) {
        //DRAWER-17..19: Sezione Deleghe e notifiche modifica dati
        drawer.delegationsAndNotificationsTitle().readAndAssert(EServiceCatalogFiltersDrawer.SECTION_DELEGATIONS);
        Assertions.assertThat(drawer.delegationsInput().get().isPresent())
                .as("Input Deleghe presente nel DOM").isTrue();
        Assertions.assertThat(drawer.signalHubSwitch().isDisabled())
                .as("Switch Signal Hub abilitato").isFalse();
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
        Assertions.assertThat(page.eServiceCards().get(0).header().title().read())
                .as("Nome erogatore nella prima card")
                .isNotBlank();

        Assertions.assertThat(page.eServiceCards().get(0).eServiceName().read())
                .as("Nome E-Service nella prima card")
                .isNotBlank();

        Assertions.assertThat(page.eServiceCards().get(0).eServiceDescription().read())
                .as("Descrizione E-Service nella prima card")
                .isNotNull();
    }
}