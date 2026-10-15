package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.TableRow;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerAgreementsListPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.utils.FormattingUtils;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerAgreementsListPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private static final AtomicInteger TEST_INDEX_COUNTER = new AtomicInteger(1);
    private static final String TEST_NAME_PREFIX = "CON-AGREE";

    @TestFactory
    Stream<DynamicTest> consumerAgreementsListPageContract() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerAgreementsListPage.class)
                .tests(contractScenarios());
    }

    private Stream<WebScenario<ConsumerAgreementsListPage>> contractScenarios() {
        return Stream.of(
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Page title should be present"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.pageTitle().read()).isNotBlank()
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Page description should be present"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.pageDescription().read()).isNotBlank()
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "E-Service filter label should be correct"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.eServiceFilterLabel().read())
                                .as("E-Service filter label")
                                .isEqualTo("Cerca per e-service")
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "E-Service filter should be empty by default"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.eServiceFilterInput().read())
                                .as("E-Service filter initial value")
                                .isEmpty()
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Producer filter label should be correct"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.producerFilterLabel().read())
                                .as("Producer filter label")
                                .isEqualTo("Cerca per erogatore")
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Producer filter should be empty by default"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.producerFilterInput().read())
                                .as("Producer filter initial value")
                                .isEmpty()
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Status filter label should be correct"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.statusFilterLabel().read())
                                .as("Status filter label")
                                .isEqualTo("Stato della richiesta")
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Status filter should be empty by default"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.statusFilterInput().read())
                                .as("Status filter initial value")
                                .isEmpty()
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Agreements table should be present"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.agreementsTable().get().isPresent()).isTrue()
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Agreement table should expose expected columns"),
                        page -> {
                        },
                        page -> {
                            TableRow header = page.agreementsTable().tHead().rows().get(0);

                            Assertions.assertThat(header.cells().get(0).read())
                                    .as("E-Service column")
                                    .isEqualTo("E-service");

                            Assertions.assertThat(header.cells().get(1).read())
                                    .as("Erogatore column")
                                    .isEqualTo("Erogatore");

                            Assertions.assertThat(header.cells().get(2).read())
                                    .as("Status column")
                                    .isEqualTo("Stato richiesta");
                        }
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "All cells in all table rows should not be blank"),
                        page -> {
                        },
                        page -> {
                            List<TableRow> rows = page.agreementsTable().tBody().rows();
                            AtomicInteger rowCounter = new AtomicInteger(1);
                            rows.forEach(row -> {
                                Assertions.assertThat(row.cells().get(0).read())
                                        .as("E-Service cell value at row %s", rowCounter)
                                        .isNotBlank();

                                Assertions.assertThat(row.cells().get(1).read())
                                        .as("Provider cell value at row %s", rowCounter)
                                        .isNotBlank();

                                Assertions.assertThat(row.cells().get(2).read())
                                        .as("Status cell value at row %s", rowCounter)
                                        .isNotBlank();

                                Assertions.assertThat(row.cells().get(3).read())
                                        .as("Cell with detail button at row %s", rowCounter)
                                        .isEqualTo("Visualizza");
                                rowCounter.getAndIncrement();
                            });
                        }
                ),
                new WebScenario<>(
                        FormattingUtils.enumerateTests(TEST_INDEX_COUNTER.getAndIncrement(), TEST_NAME_PREFIX, "Pagination should be present"),
                        page -> {
                        },
                        page -> Assertions.assertThat(page.pagination().get().isPresent()).isTrue()
                )
        );
    }
}