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
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebAgreementContractTest {

    private static final String DESIRED_MESSAGE_BANNER_1 = "Questa versione dell’e-service è obsoleta, ma è ancora attiva. È disponibile una nuova versione.";
    private static final String DESIRED_MESSAGE_BANNER_2 = "Questa versione dell’e-service è obsoleta, ma è ancora attiva.";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> shouldSeeBanner1() {
        Agreement agreement = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .waitUntilEService(eservice -> eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.DEPRECATED)
                .archiveFirstEServiceDescriptor(GracePeriodDays.NUMBER_60)
                .waitUntilEService(eservice ->
                        eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING
                                || eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVED
                )
                .get(Agreement.class);

        return assertBannerIsVisible(
                "Should see banner for agreement update to newer version",
                DESIRED_MESSAGE_BANNER_1,
                Tenant.COMUNE_DI_MILANO,
                agreement.getId()
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldSeeBanner2() {
        Agreement agreement = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .archiveEService(GracePeriodDays.NUMBER_60)
                .waitUntilEService(eservice ->
                        eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING
                                || eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVED
                )
                .get(Agreement.class);

        return assertBannerIsVisible(
                "Should see banner agreement",
                DESIRED_MESSAGE_BANNER_2,
                Tenant.COMUNE_DI_MILANO,
                agreement.getId()
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldSeeNoBannerWhenEserviceInArchivingStateAndAgreementIsNonUpdatable() {
        Agreement agreement = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .addDescriptor(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .archiveEService(GracePeriodDays.NUMBER_60)
                .waitUntilEService(eservice ->
                        eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING
                                || eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVED
                )
                .get(Agreement.class);

        return assertNoBannerIsVisible(
                "Should see no banner when e-service is in archiving state and the agreement is non-updatable",
                Tenant.COMUNE_DI_MILANO,
                agreement.getId()
        );
    }

    @TestFactory
    Stream<DynamicTest> shouldSeeNoBannerWhenDescriptorInArchivingStateAndEserviceInArchivingStateAndAgreementIsNonUpdatable() {
        Agreement agreement = interopJourney
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
                        eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVING
                                || eservice.getDescriptors().get(0).getState() == EServiceDescriptorState.ARCHIVED
                )
                .get(Agreement.class);

        return assertNoBannerIsVisible(
                "Should see no banner when descriptor and e-service are in archiving state and the agreement is non-updatable",
                Tenant.COMUNE_DI_MILANO,
                agreement.getId()
        );
    }

    private Stream<DynamicTest> assertBannerIsVisible(
            final String webScenarioMessage,
            final String expectedMessage,
            final Tenant consumer,
            final UUID agreementId
    ) {
        return agreementScenario(
                webScenarioMessage,
                consumer,
                agreementId,
                page -> {
                    List<String> alertMessages = page.alerts().stream()
                            .map(alert -> alert.message().read())
                            .toList();

                    Assertions.assertThat(alertMessages).contains(expectedMessage);
                }
        );
    }

    private Stream<DynamicTest> assertNoBannerIsVisible(
            final String webScenarioMessage,
            final Tenant consumer,
            final UUID agreementId
    ) {
        return agreementScenario(
                webScenarioMessage,
                consumer,
                agreementId,
                page -> {
                    List<String> alertMessages = page.alerts().stream()
                            .map(alert -> alert.message().read())
                            .toList();

                    Assertions.assertThat(alertMessages)
                            .doesNotContain(DESIRED_MESSAGE_BANNER_1, DESIRED_MESSAGE_BANNER_2);
                }
        );
    }

    private Stream<DynamicTest> agreementScenario(
            final String webScenarioMessage,
            final Tenant consumer,
            final UUID agreementId,
            final Consumer<AgreementPage> assertions
    ) {
        WebScenario<AgreementPage> scenario = new WebScenario<>(
                webScenarioMessage,
                page -> {
                },
                assertions
        );

        return webContractValidator
                .as(User.getTenantAdmin(consumer), consumer)
                .on(AgreementPage.class, agreementId.toString())
                .tests(Stream.of(scenario));
    }
}