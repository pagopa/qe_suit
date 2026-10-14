package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceProviderListPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

/**
 * Visual checks for direct navigation to the provider e-service list as a Comune di Milano administrator.
 * <ul>
 *     <li>Page identity: exact title and breadcrumb labels.</li>
 *     <li>Primary actions: Importa and Crea nuovo present and enabled.</li>
 *     <li>Filters: expected labels, enabled empty inputs, closed consumer combobox,
 *         and disabled name-clear control.</li>
 *     <li>List: semantic table present with three ordered labels and an unlabeled action column.</li>
 * </ul>
 * Reference: QA-18083 and the supplied e-service.html baseline.
 * Excludes layout, colors, fonts, screenshot comparisons, global navigation/header/footer,
 * environment-dependent row data, pagination, interactions, and the empty-list variant.
 * Sidebar navigation is not exercised. Mobile coverage requires execution at a verified viewport;
 * this desktop DOM baseline alone does not establish mobile rendering behavior.
 */
@SpringBootTest(classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebEServiceProviderListContractTest {
    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> shouldDisplayProviderEServiceList() {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceProviderListPage.class)
                .tests(scenarios());
    }

    private Stream<WebScenario<EServiceProviderListPage>> scenarios() {
        return Stream.of(
                        new WebScenario<>("Visual provider e-services: page title and breadcrumbs",
                                page -> {}, this::assertPageIdentity),
                        new WebScenario<>("Visual provider e-services: Importa and Crea nuovo actions enabled",
                                page -> {}, this::assertPrimaryActions),
                        new WebScenario<>("Visual provider e-services: empty enabled filters, closed combobox and disabled clear",
                                page -> {}, this::assertInitialFilters),
                        new WebScenario<>("Visual provider e-services: table and ordered column headings",
                                page -> {}, page -> {
                                    page.table().get(FindPolicy.PRESENT)
                                            .orElseThrow(() -> new IllegalStateException("Provider e-service table missing"));
                                    Assertions.assertThat(page.table().columnHeadings().readAll())
                                            .as("Provider e-service list column headings")
                                            .containsExactly("E-service", "Versione", "Stato", "");
                                })
                );
    }

    private void assertPageIdentity(EServiceProviderListPage page) {
        Assertions.assertThat(page.pageTitle().read()).as("Provider e-service page title")
                .isEqualTo("I miei e-service");
        Assertions.assertThat(page.breadcrumbs().readAll()).as("Provider e-service breadcrumbs")
                .containsExactly("Erogazione", "I miei e-service");
    }

    private void assertPrimaryActions(EServiceProviderListPage page) {
        var actions = page.primaryActions();
        Assertions.assertThat(actions).as("Provider e-service primary actions").hasSize(2);
        Assertions.assertThat(actions.stream().map(action -> action.read()).toList())
                .as("Provider e-service action labels").containsExactlyInAnyOrder("Importa", "Crea nuovo");
        for (var action : actions) {
            Assertions.assertThat(action.isDisabled()).as("Action enabled: %s", action.read()).isFalse();
        }
    }

    private void assertInitialFilters(EServiceProviderListPage page) {
        var filters = page.filters();
        Assertions.assertThat(filters.nameLabel().read()).as("Name search label").isEqualTo("Cerca per nome");
        Assertions.assertThat(filters.consumerLabel().read()).as("Consumer search label").isEqualTo("Cerca per fruitore");
        var name = filters.nameInput().get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Name search input missing"));
        var consumer = filters.consumerInput().get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Consumer search input missing"));
        Assertions.assertThat(name.getAttributes()).as("Initial name search state")
                .containsEntry("value", "").doesNotContainKey("disabled");
        Assertions.assertThat(consumer.getAttributes()).as("Initial consumer search state")
                .containsEntry("value", "").containsEntry("aria-expanded", "false").doesNotContainKey("disabled");
        Assertions.assertThat(filters.clearNameButton().isDisabled()).as("Empty name filter clear control disabled").isTrue();
    }
}
