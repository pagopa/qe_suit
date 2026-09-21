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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
        classes = {TestBootApp.class,JunitContextConfig.class,WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome", }  // "channel.web.headless=false",
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAgreementContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final String DESIRED_MESSAGE_BANNER_1 = "Questa versione dell’e-service è obsoleta, ma è ancora attiva. È disponibile una nuova versione.";
    private final String DESIRED_MESSAGE_BANNER_2 = "Questa versione dell’e-service è obsoleta, ma è ancora attiva.";

    private void assertBannerIsVisible(final String webScenarioMessage, final String message, final Tenant consumer, final UUID agreementId) throws Throwable {
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
                    .on(AgreementPage.class, agreementId.toString())
                    .tests(Stream.of(scenario))
                    .toList();

            tests.get(0).getExecutable().execute();
        } finally {
            System.clearProperty("agreementId");
        }
    }

    private void assertNoBannerIsVisible(final String webScenarioMessage, final Tenant consumer, final UUID agreementId) throws Throwable {
        try {
            WebScenario<AgreementPage> scenario = new WebScenario<>(
                    webScenarioMessage,
                    page -> {},
                    page -> {
                        final boolean[] bannerPresent = {false};
                        List<String> messages = List.of(DESIRED_MESSAGE_BANNER_1, DESIRED_MESSAGE_BANNER_2);
                        messages.forEach(message -> {
                            page.alerts().forEach(
                                    alert -> {
                                        if (message.equals(alert.message().read()))
                                            bannerPresent[0] = true;
                                    }
                            );
                        });
                        Assertions.assertThat(bannerPresent[0]).isFalse();
                    }
            );

            List<DynamicTest> tests = webContractValidator
                    .as(
                            User.getTenantAdmin(consumer),
                            consumer
                    )
                    .on(AgreementPage.class, agreementId.toString())
                    .tests(Stream.of(scenario))
                    .toList();

            tests.get(0).getExecutable().execute();
        } finally {
            System.clearProperty("agreementId");
        }
    }

    // case 1
    @Test
    void shouldSeeBanner1() throws Throwable {
        interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .waitUntilEService(eservice -> eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.DEPRECATED)
                .archiveFirstEServiceDescriptor(GracePeriodDays.NUMBER_60)
                .waitUntilEService(eservice ->
                        (eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING ||
                                eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVED));

        doAssertion(true, "Should see banner for agreement update to newer version", Tenant.COMUNE_DI_MILANO, DESIRED_MESSAGE_BANNER_1);
    }

    // case 2
    @Test
    void shouldSeeBanner2() throws Throwable {
        interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .archiveEService(GracePeriodDays.NUMBER_60)
                .waitUntilEService(eservice ->
                        (eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING ||
                        eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVED));

        doAssertion(true, "Should see banner agreement", Tenant.COMUNE_DI_MILANO, DESIRED_MESSAGE_BANNER_2);
    }

    // case 3
    @Test
    void shouldSeeNoBannerWhenEserviceInArchivingStateAndAgreementIsNonUpdatable() throws Throwable {
        interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .archiveEService(GracePeriodDays.NUMBER_60)
                .waitUntilEService(eservice ->
                        (eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING ||
                        eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVED));

        doAssertion(false, "should see no banner when e-service is in archiving state and the agreement is non-updatable", Tenant.COMUNE_DI_MILANO, null);
    }

    // case 4
    @Test
    void shouldSeeNoBannerWhenDescriptorInArchivingStateAndEserviceInArchivingStateAndAgreementIsNonUpdatable() throws Throwable {
        interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .waitUntilEService(eservice -> eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.DEPRECATED)
                .archiveFirstEServiceDescriptor(GracePeriodDays.NUMBER_60)
                .archiveEService(GracePeriodDays.NUMBER_60)
                .waitUntilEService(eservice ->
                        (eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING ||
                                eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVED));

        doAssertion(false, "should see no banner when e-service is in archiving state and the agreement is non-updatable", Tenant.COMUNE_DI_MILANO, null);
    }

    private void doAssertion(final boolean assertBannerPresence, final String webScenarioMessage, final Tenant consumer, final String bannerMessage) throws Throwable {
        Agreement agreement = interopJourney.get(Agreement.class);
        if (assertBannerPresence) {
            assertBannerIsVisible(webScenarioMessage, bannerMessage, consumer, agreement.getId());
        } else {
            assertNoBannerIsVisible(webScenarioMessage, consumer, agreement.getId());
        }
    }
}