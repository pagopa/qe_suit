package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Pagination;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.agreement.infrastructure.page.AgreementRequestPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = {
                "spring.profiles.include=junit",
                "channel.web.browser=chrome",
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAgreementRequestPaginationContractTest {

    private static final Tenant PRODUCER = Tenant.COMUNE_DI_MILANO;
    private static final Tenant CONSUMER = Tenant.COMUNE_DI_POZZALLO;

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> shouldShowPaginationNavbarRegardlessOfOffset() {
        interopJourney
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(CONSUMER, UserRole.ADMIN)
                .linkAgreement(AgreementState.PENDING);

        return webContractValidator
                .as(
                        User.getTenantAdmin(CONSUMER),
                        CONSUMER
                )
                .on(AgreementRequestPage.class, "10", "0")
                .tests(scenarios());
    }

    private Stream<WebScenario<AgreementRequestPage>> scenarios() {
        return Stream.of(
                baseOffsetScenario(),
                highOffsetScenario()
        );
    }

    private WebScenario<AgreementRequestPage> baseOffsetScenario() {
        return new WebScenario<>(
                "offset = 0: la navbar di paginazione è presente",
                page -> {

                },
                page -> {
                    Assertions.assertThat(page.pagination().exists())
                            .as("Pagination navbar is present")
                            .isTrue();

                    Assertions.assertThat(page.pagination().isUsable())
                            .as("Pagination navbar is usable")
                            .isTrue();
                }
        );
    }

    private WebScenario<AgreementRequestPage> highOffsetScenario() {
        return new WebScenario<>(
                "offset elevato mantiene la navbar visibile a fine pagina",
                page -> page.navigateTo("10", "1000"),
                page -> {
                    Assertions.assertThat(page.pagination().exists())
                            .as("Pagination navbar is present")
                            .isTrue();

                    Assertions.assertThat(page.pagination().isUsable())
                            .as("Pagination navbar is usable")
                            .isTrue();
                }
        );
    }
}

