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
public class WebConsumerAgreementsListPageContractTest {

    private final WebBrowserContractValidator webContractValidator;

    /**
     * Verifies that the consumer agreements list page shows the expected
     * title, description, filter labels and table of elements for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> consumerAgreementsListPageContract() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerAgreementsListPage.class, "")
                .tests(contractScenarios());
    }

    @TestFactory
    Stream<DynamicTest> noResultOnConsumerAgreementPageContract() {
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(
                        ConsumerAgreementsListPage.class,
                        "?eservicesIds=%5B%5B\"no-result\"%2C\"11111111-1111-1111-1111-111111111111\"%5D%5D"
                )
                .tests(noResultContractScenarios());
    }

    private Stream<WebScenario<ConsumerAgreementsListPage>> contractScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Consumer agreements list page shows the expected content and filters",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                                    softly.assertThat(page.pageDescription().read())
                                            .as("Page description")
                                            .isEqualTo("Le richieste di fruizione inoltrate verranno raccolte in questa area.");

                                    softly.assertThat(page.eServiceFilterLabel().read())
                                            .as("E-Service filter label")
                                            .isEqualTo("Cerca per e-service");
                                    softly.assertThat(page.eServiceFilterInput().read())
                                            .as("E-Service filter initial value")
                                            .isEmpty();
                                    softly.assertThat(page.providerFilterLabel().read())
                                            .as("Producer filter label")
                                            .isEqualTo("Cerca per erogatore");
                                    softly.assertThat(page.providerFilterInput().read())
                                            .as("Producer filter initial value")
                                            .isEmpty();
                                    softly.assertThat(page.statusFilterLabel().read())
                                            .as("Status filter label")
                                            .isEqualTo("Stato della richiesta");
                                    softly.assertThat(page.statusFilterInput().read())
                                            .as("Status filter initial value")
                                            .isEmpty();

                                    softly.assertThat(page.agreementsTable().get().isPresent()).isTrue();
                                    TableRow header = page.agreementsTable().tHead().rows().get(0);

                                    softly.assertThat(header.cells().get(0).read())
                                            .as("E-Service column")
                                            .isEqualTo("E-service");

                                    softly.assertThat(header.cells().get(1).read())
                                            .as("Erogatore column")
                                            .isEqualTo("Erogatore");

                                    softly.assertThat(header.cells().get(2).read())
                                            .as("Status column")
                                            .isEqualTo("Stato richiesta");

                                    List<TableRow> rows = page.agreementsTable().tBody().rows();
                                    AtomicInteger rowCounter = new AtomicInteger(1);
                                    rows.forEach(row -> {
                                        softly.assertThat(row.cells().get(0).read())
                                                .as("E-Service cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(1).read())
                                                .as("Provider cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(2).read())
                                                .as("Status cell value at row %s", rowCounter)
                                                .isNotBlank();

                                        softly.assertThat(row.cells().get(3).read())
                                                .as("Cell with detail button at row %s", rowCounter)
                                                .isNotBlank();
                                rowCounter.getAndIncrement();
                            });
                            softly.assertThat(page.rowsPerPageSelector().get().isPresent()).isTrue();
                            softly.assertThat(page.pagination().get().isPresent()).isTrue();
                        })
                )
        );
    }

    private Stream<WebScenario<ConsumerAgreementsListPage>> noResultContractScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Consumer agreements list page hides the rows per page selector",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                            softly.assertThat(page.noResultMessage().get().isPresent()).isTrue();
                            softly.assertThat(page.rowsPerPageSelector().get().isPresent()).isFalse();
                            softly.assertThat(page.pagination().get().isPresent()).isFalse();
                        })
                )
        );
    }
}
