package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerPurposesListPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerPurposesListPageContractTest {

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Verifies that the producer purposes list page shows the expected
     * title, description, filter labels and table of elements for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> producerPurposesListPageContract() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ProducerPurposesListPage.class)
                .tests(contractScenarios());
    }

    private Stream<WebScenario<ProducerPurposesListPage>> contractScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Producer purposes list page shows the expected content and filters",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                                    softly.assertThat(page.pageDescription().read())
                                            .as("Page description")
                                            .isEqualTo("Gli enti che intendono fruire dei tuoi e-service dovranno indicare come tratteranno i dati che eroghi. Le finalità che ricevi sono raccolte in questa area.");

                                    softly.assertThat(page.purposeNameFilterLabel().read())
                                            .as("Purpose name filter label")
                                            .isEqualTo("Cerca per nome finalità");
                                    softly.assertThat(page.purposeNameFilterInput().read())
                                            .as("Purpose name filter initial value")
                                            .isEmpty();
                                    softly.assertThat(page.eServiceFilterLabel().read())
                                            .as("E-Service filter label")
                                            .isEqualTo("Cerca per e-service");
                                    softly.assertThat(page.eServiceFilterInput().read())
                                            .as("E-Service filter initial value")
                                            .isEmpty();
                                    softly.assertThat(page.consumerFilterLabel().read())
                                            .as("Consumer filter label")
                                            .isEqualTo("Cerca per fruitore");
                                    softly.assertThat(page.consumerFilterInput().read())
                                            .as("Consumer filter initial value")
                                            .isEmpty();
                                    softly.assertThat(page.statusFilterLabel().read())
                                            .as("Status filter label")
                                            .isEqualTo("Stato della finalità");
                                    softly.assertThat(page.statusFilterInput().read())
                                            .as("Status filter initial value")
                                            .isEmpty();

                                    softly.assertThat(page.purposesTable().get().isPresent()).isTrue();
                                    TableRow header = page.purposesTable().tHead().rows().get(0);

                                    softly.assertThat(header.cells().get(0).read())
                                            .as("Purpose column")
                                            .isEqualTo("Finalità");

                                    softly.assertThat(header.cells().get(1).read())
                                            .as("Fruitore column")
                                            .isEqualTo("Fruitore");

                                    softly.assertThat(header.cells().get(2).read())
                                            .as("Status column")
                                            .isEqualTo("Stato finalità");

                                    List<TableRow> rows = page.purposesTable().tBody().rows();
                                    AtomicInteger rowCounter = new AtomicInteger(1);
                                    rows.forEach(row -> {
                                        softly.assertThat(row.cells().get(0).read())
                                                .as("Purpose cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(1).read())
                                                .as("Consumer cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(2).read())
                                                .as("Status cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(3).read())
                                                .as("Cell with detail button at row %s", rowCounter)
                                                .isNotBlank();
                                        rowCounter.getAndIncrement();
                                    });

                                    int initialSize = page.rowsPerPageSelector().getSelectedSize();
                                    softly.assertThat(initialSize)
                                            .as("Rows per page should be initialized to 10")
                                            .isEqualTo(10);
                                    List<Integer> sizeValues = page.rowsPerPageSelector().getSizeValues();
                                    softly.assertThat(sizeValues.size())
                                            .as("Rows per page selector should have more than one option")
                                            .isGreaterThan(1);

                                    int initialRowCount = page.purposesTable().tBody().rows().size();
                                    page.rowsPerPageSelector().setSize(sizeValues.get(1));
                                    int updatedRowCount = page.purposesTable().tBody().rows().size();
                                    page.rowsPerPageSelector().setSize(initialSize);
                                    if (updatedRowCount > initialRowCount) {
                                        softly.assertThat(page.pagination().exists())
                                                .as("Pagination should be displayed when more rows than the first page are available")
                                                .isTrue();
                                    } else {
                                        softly.assertThat(page.pagination().exists())
                                                .as("Pagination should not be required when all %s rows fit into a single page of %s elements", updatedRowCount, initialRowCount)
                                                .isFalse();
                                    }
                                }
                        )));
    }
}