package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Readable;
import static org.assertj.core.api.Assertions.assertThat;

import it.frontend.e2e.framework.web.capability.core.Gettable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.infrastructure.suit.component.Table;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplatesListPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import java.util.List;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Web Producer E-Service Templates List Page Contract Test")
class WebProducerEServiceTemplatesListPageContractTest {

    private static final String EMPTY_TEMPLATES_ALERT_TEXT = "Non hai ancora creato template di e-service.";
    private final WebBrowserContractValidator webContractValidator;

    private static final int PAGE_SIZE = 10;

    private static final String PAGE_TITLE = "I miei template e-service";
    private static final String PAGE_SUBTITLE =
            "Gestisci ed eroga nuovi template di e-service da questa area dedicata.";
    private static final String CREATE_BUTTON_LABEL = "Crea nuovo";
    private static final String SEARCH_INPUT_LABEL = "Cerca per nome";
    private static final List<String> EXPECTED_COLUMN_HEADERS =
            List.of("Template", "Versione", "Stato", "");
    private static final List<String> EXPECTED_BREADCRUMB_LABELS =
            List.of("Erogazione", "I miei template e-service");
    private static final String EXPECTED_FIRST_BREADCRUMB_HREF = "ui/it/erogazione";


    @TestFactory
    @DisplayName("Verify visual contract for producer e-service templates list page")
    Stream<DynamicTest> testProducerEServiceTemplatesListPageContract() {
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO) , Tenant.COMUNE_DI_MILANO)
                .on(ProducerEServiceTemplatesListPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Verify initial visual contract of producer e-service templates list page",
                                page -> {},
                                page -> {
                                    assertBreadcrumbs(page);
                                    assertHeaderElements(page);
                                    assertSearchControls(page);
                                    assertTemplatesTable(page);
                                    assertPagination(page);
                                }
                        )
                ));
    }

    private void assertBreadcrumbs(ProducerEServiceTemplatesListPage page) {
        assertThat(page.breadcrumbs().get().isPresent())
                .as("Verify breadcrumbs navigation is present in DOM")
                .isTrue();
        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Verify breadcrumbs labels match approved contract")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);
        assertThat(page.breadcrumbs().links())
                .as("Verify breadcrumbs links count")
                .hasSize(1);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Verify first breadcrumb href")
                .endsWith(EXPECTED_FIRST_BREADCRUMB_HREF);
    }

    private void assertHeaderElements(ProducerEServiceTemplatesListPage page) {
        assertThat(page.title().get().isPresent())
                .as("Verify page title is present in DOM")
                .isTrue();
        assertThat(page.title().get(Gettable.FindPolicy.VISIBLE))
                .as("Verify page title is visible").isPresent();
        page.title().readAndAssert(PAGE_TITLE);

        assertThat(page.subtitle().get().isPresent())
                .as("Verify page subtitle is present in DOM").isTrue();
        assertThat(page.subtitle().get(Gettable.FindPolicy.VISIBLE))
                .as("Verify page subtitle is visible").isPresent();
        page.subtitle().readAndAssert(PAGE_SUBTITLE);

        assertThat(page.createNewButton().get().isPresent())
                .as("Verify create new template button is present in DOM").isTrue();
        assertThat(page.createNewButton().get(Gettable.FindPolicy.VISIBLE))
                .as("Verify create new template button is visible")
                .isPresent();
        assertThat(page.createNewButton().isDisabled())
                .as("Verify create new template button is enabled")
                .isFalse();
        page.createNewButton().readAndAssert(CREATE_BUTTON_LABEL);
    }

    private void assertSearchControls(ProducerEServiceTemplatesListPage page) {
        assertThat(page.searchLabel().get().isPresent())
                .as("Verify search input label is present in DOM")
                .isTrue();
        page.searchLabel().readAndAssert(SEARCH_INPUT_LABEL);

        assertThat(page.searchInput().get().isPresent())
                .as("Verify search text field is present in DOM")
                .isTrue();
        assertThat(page.searchInput().get(Gettable.FindPolicy.VISIBLE))
                .as("Verify search text field is visible")
                .isPresent();

        assertThat(page.searchButton().get(Gettable.FindPolicy.PRESENT))
                .as("Verify search filter icon button is present in DOM")
                        .isPresent();
        assertThat(page.searchButton().isDisabled())
                .as("Verify search filter icon button is initially disabled")
                .isTrue();
    }

    private void assertTemplatesTable(ProducerEServiceTemplatesListPage page) {
        Table table = page.templatesTable();

        assertThat(table.get().isPresent())
                .as("Verify e-service templates table is present in DOM")
                .isTrue();
        assertThat(table.get(Gettable.FindPolicy.VISIBLE))
                .as("Verify e-service templates table is visible")
                        .isPresent();

        assertThat(table.tHead().get().isPresent())
                .as("Verify templates table header is present in DOM")
                .isTrue();
        assertThat(table.tHead().get(Gettable.FindPolicy.VISIBLE))
                .as("Verify templates table header is visible")
                .isPresent();

        assertThat(table.tBody().get().isPresent())
                .as("Verify templates table body is present in DOM")
                .isTrue();
        assertThat(table.tBody().get(Gettable.FindPolicy.VISIBLE))
                .as("Verify templates table body is visible")
                .isPresent();

        assertThat(table.getColumnHeaders())
                .as("Verify templates table column headers match approved contract")
                .containsExactlyElementsOf(EXPECTED_COLUMN_HEADERS);

        if (page.emptyTemplatesAlert().get(Gettable.FindPolicy.PRESENT).isPresent()) {
            assertThat(page.emptyTemplatesAlert().get(Gettable.FindPolicy.VISIBLE))
                    .as("Verify empty templates alert is visible when table has no templates")
                    .isPresent();
            assertThat(page.emptyTemplatesAlert().message().read().equals(EMPTY_TEMPLATES_ALERT_TEXT))
                    .as("Verify empty templates alert message matches expected text" + page.emptyTemplatesAlert().message().read())
                    .isTrue();
        } else {
            assertThat(table.getTotalRows())
                    .as("Verify templates table displays at least one e-service template row")
                    .isGreaterThan(0);
        }
    }

    private void assertPagination(ProducerEServiceTemplatesListPage page) {
        boolean isEmptyState = page.emptyTemplatesAlert().get(Gettable.FindPolicy.VISIBLE).isPresent();
        int dataRows = isEmptyState ? 0 : page.templatesTable().getTotalRows();

        Pagination pagination = page.pagination();
        if (dataRows >= PAGE_SIZE && pagination.hasNext()) {

                assertThat(pagination.get().isPresent())
                        .as("Verify pagination component is present in DOM")
                        .isTrue();
                assertThat(pagination.get(Gettable.FindPolicy.VISIBLE))
                        .as("Verify pagination component is visible")
                        .isPresent();

                assertThat(pagination.prevBtn().get(Gettable.FindPolicy.PRESENT))
                        .as("Verify pagination previous button is present in DOM")
                        .isPresent();
                assertThat(pagination.prevBtn().isDisabled())
                        .as("Verify pagination previous button is disabled on first page")
                        .isTrue();
                assertThat(pagination.nextBtn().get(Gettable.FindPolicy.PRESENT))
                        .as("Verify pagination next button is present in DOM")
                        .isPresent();
                assertThat(pagination.nextBtn().isDisabled())
                        .as("Verify pagination next button is enabled on first page")
                        .isFalse();

                assertThat(pagination.buttons())
                        .as("Verify pagination page buttons are rendered")
                        .isNotEmpty();
        } else {
            assertThat(page.pagination().get(Gettable.FindPolicy.PRESENT))
                    .as("Pagination component should not be present in DOM when there are no templates or less than a full page of templates")
                    .isEmpty();
        }
    }
}