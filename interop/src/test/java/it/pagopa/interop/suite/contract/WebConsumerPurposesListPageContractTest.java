package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerPurposesListPage;
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
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerPurposesListPageContractTest {

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Verifies that the consumer purposes list page shows the expected
     * title, description, filter labels and table of elements for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> consumerAgreementsListPageContract() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerPurposesListPage.class)
                .tests(contractScenarios());
    }

    private Stream<WebScenario<ConsumerPurposesListPage>> contractScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Consumer purposes list page shows the expected content and filters",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                                    softly.assertThat(page.pageDescription().read())
                                            .as("Page description")
                                            .isEqualTo("Quando le tue richieste di fruizione vengono accettate, dovrai compilare una finalità per descrivere come tratterai i dati che ricevi. Le tue finalità vengono raccolte in questa area.");
                                    softly.assertThat(page.createPurposeButton().read())
                                            .as("Crete Purpose button")
                                            .isEqualTo("Crea nuovo");

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
                                    softly.assertThat(page.providerFilterLabel().read())
                                            .as("Provider filter label")
                                            .isEqualTo("Cerca per erogatore");
                                    softly.assertThat(page.providerFilterInput().read())
                                            .as("Provider filter initial value")
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
                                            .as("E-Service column")
                                            .isEqualTo("E-service");

                                    softly.assertThat(header.cells().get(2).read())
                                            .as("Erogatore column")
                                            .isEqualTo("Erogatore");

                                    softly.assertThat(header.cells().get(3).read())
                                            .as("Status column")
                                            .isEqualTo("Stato finalità");

                                    List<TableRow> rows = page.purposesTable().tBody().rows();
                                    AtomicInteger rowCounter = new AtomicInteger(1);
                                    rows.forEach(row -> {
                                        softly.assertThat(row.cells().get(0).read())
                                                .as("Purpose cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(1).read())
                                                .as("E-Service cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(2).read())
                                                .as("Provider cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(3).read())
                                                .as("Status cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(4).read())
                                                .as("Cell with detail button at row %s", rowCounter)
                                                .isNotBlank();
                                        rowCounter.getAndIncrement();
                                    });

                                    softly.assertThat(page.pagination().get().isPresent()).isTrue();
                                }
                        )));
    }
}