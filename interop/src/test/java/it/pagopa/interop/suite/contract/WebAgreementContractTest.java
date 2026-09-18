package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.agreement.infrastructure.page.AgreementPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

@Execution(ExecutionMode.CONCURRENT)
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
public class WebAgreementContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final String DESIRED_MESSAGE_BANNER_1 = "Questa versione dell’e-service è obsoleta, ma è ancora attiva. È disponibile una nuova versione.";
    private final String DESIRED_MESSAGE_BANNER_2 = "Questa versione dell’e-service è obsoleta, ma è ancora attiva.";

    private void assertAgreementBannerIsVisible(final String webScenarioMessage, final String message, final Tenant producer, final Tenant consumer) throws Throwable {
        try {
            WebScenario<AgreementPage> scenario = new WebScenario<>(
                    webScenarioMessage,
                    page -> {},
                    page -> {
                        final boolean[] bannerPresent = {false};
                        page.alerts().forEach(
                                alert -> {
                                    if (message.equals(alert.message().read()))
                                        bannerPresent[0] = true;
                                }
                        );
                        Assertions.assertThat(bannerPresent[0]).isTrue();
                    }
            );

            List<DynamicTest> tests = webContractValidator
                    .as(
                            User.getTenantAdmin(consumer),
                            consumer
                    )
                    .on(AgreementPage.class)
                    .tests(Stream.of(scenario))
                    .toList();

            tests.get(0).getExecutable().execute();
        } finally {
            System.clearProperty("agreementId");
        }
    }

    @Test
    void shouldSeeBanner1() throws Throwable {
        // Given: an eservice whose first descriptor has an agreement, and a newer
        // descriptor has since been published, making the first one DEPRECATED.
        interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .waitUntilEService(eservice -> eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.DEPRECATED);

        Agreement agreement = interopJourney.get(Agreement.class);
        System.setProperty("agreementId", agreement.getId().toString());

        assertAgreementBannerIsVisible(
                "Should see banner for agreement update to newer version",
                DESIRED_MESSAGE_BANNER_1,
                Tenant.PAGO_PA,
                Tenant.COMUNE_DI_MILANO
        );
    }

    @Test
    void shouldSeeBanner2() throws Throwable {
        // Given: an eservice whose first descriptor has an agreement, and a newer
        // descriptor has since been published, making the first one DEPRECATED.
        interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .archiveEService(GracePeriodDays.NUMBER_60)
                .waitUntilEService(eservice -> eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING);

        Agreement agreement = interopJourney.get(Agreement.class);
        System.setProperty("agreementId", agreement.getId().toString());
        assertAgreementBannerIsVisible(
                "Should see banner agreement",
                DESIRED_MESSAGE_BANNER_2,
                Tenant.PAGO_PA,
                Tenant.COMUNE_DI_MILANO
        );
    }

    @Disabled
    @Test
    void shouldSeeNoBanner(){}
}