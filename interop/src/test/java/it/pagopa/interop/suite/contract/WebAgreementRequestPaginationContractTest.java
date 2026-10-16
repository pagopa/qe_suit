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
                "channel.web.headless=false",
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
                    createPendingAgreementRequest();
                    page.navigateTo("10", "0");
                },
                page -> Assertions.assertThat(page.pagination())
                        .isPresent()
                        .get()
                        .extracting(Pagination::isUsable)
                        .isEqualTo(true)
        );
    }

    private WebScenario<AgreementRequestPage> highOffsetScenario() {
        return new WebScenario<>(
                "offset elevato mantiene la navbar visibile a fine pagina",
                page -> {
                    createPendingAgreementRequest();
                    page.navigateTo("10", "1000");
                },
                page -> Assertions.assertThat(page.pagination())
                        .isPresent()
                        .get()
                        .extracting(Pagination::isUsable)
                        .isEqualTo(true)
        );
    }

    private void createPendingAgreementRequest() {
        interopJourney
                .withProducer(PRODUCER, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(CONSUMER, UserRole.ADMIN)
                .linkAgreement(AgreementState.PENDING);
    }
}

