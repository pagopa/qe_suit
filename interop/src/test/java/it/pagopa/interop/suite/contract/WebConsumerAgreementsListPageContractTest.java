package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerAgreementsListPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerAgreementsListPageContractTest {

    private final WebBrowserContractValidator webContractValidator;

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
                        "Page title should be present",
                        page -> {},
                        page -> Assertions.assertThat(
                                page.pageTitle().read()
                        ).isNotBlank()
                ),

                new WebScenario<>(
                        "Page description should be present",
                        page -> {},
                        page -> Assertions.assertThat(
                                page.pageDescription().read()
                        ).isNotBlank()
                ),

                new WebScenario<>(
                        "E-Service filter label should be correct",
                        page -> {},
                        page -> Assertions.assertThat(
                                        page.eServiceFilterLabel().read()
                                )
                                .as("E-Service filter label")
                                .isEqualTo("Cerca per e-service")
                ),

                new WebScenario<>(
                        "E-Service filter should be empty by default",
                        page -> {},
                        page -> Assertions.assertThat(
                                        page.eServiceFilterInput().read()
                                )
                                .as("E-Service filter initial value")
                                .isEmpty()
                ),

                new WebScenario<>(
                        "Producer filter label should be correct",
                        page -> {},
                        page -> Assertions.assertThat(
                                        page.producerFilterLabel().read()
                                )
                                .as("Producer filter label")
                                .isEqualTo("Cerca per erogatore")
                ),

                new WebScenario<>(
                        "Producer filter should be empty by default",
                        page -> {},
                        page -> Assertions.assertThat(
                                        page.producerFilterInput().read()
                                )
                                .as("Producer filter initial value")
                                .isEmpty()
                ),

                new WebScenario<>(
                        "Status filter label should be correct",
                        page -> {},
                        page -> Assertions.assertThat(
                                        page.statusFilterLabel().read()
                                )
                                .as("Status filter label")
                                .isEqualTo("Stato della richiesta")
                ),

                new WebScenario<>(
                        "Status filter should be empty by default",
                        page -> {},
                        page -> Assertions.assertThat(
                                        page.statusFilterInput().read()
                                )
                                .as("Status filter initial value")
                                .isEmpty()
                ),

                //TODO MATTEO, tornarci in un secondo momento
//                new WebScenario<>(
//                        "Agreements table should be present",
//                        page -> {},
//                        page -> Assertions.assertThat(
//                                page.agreementsTable().get().isPresent()
//                        ).isTrue()
//                ),
//
//                new WebScenario<>(
//                        "Pagination should be present",
//                        page -> {},
//                        page -> Assertions.assertThat(
//                                page.pagination().get().isPresent()
//                        ).isTrue()
//                ),

                new WebScenario<>(
                        "E-Service column should be present",
                        page -> {},
                        page -> Assertions.assertThat(
                                page.eServiceColumn().read()
                        ).isNotBlank()
                ),

                new WebScenario<>(
                        "Provider column should be present",
                        page -> {},
                        page -> Assertions.assertThat(
                                page.providerColumn().read()
                        ).isNotBlank()
                ),

                new WebScenario<>(
                        "Agreement status column should be present",
                        page -> {},
                        page -> Assertions.assertThat(
                                page.statusColumn().read()
                        ).isNotBlank()
                )
        );
    }
}