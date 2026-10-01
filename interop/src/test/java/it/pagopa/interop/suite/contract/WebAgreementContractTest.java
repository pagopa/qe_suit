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
import it.pagopa.interop.web.agreement.infrastructure.page.AgreementListPage;
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
    Stream<DynamicTest> shouldLoadAgreementList() {
        Tenant tenant = Tenant.COMUNE_DI_MILANO;

        return Stream.of(UserRole.API, UserRole.ADMIN)
                .flatMap(role -> webContractValidator
                        .as(User.getTenantUser(tenant, role), tenant)
                        .on(AgreementListPage.class)
                        .tests(Stream.of(new WebScenario<>(
                                "richieste di fruizione ricevute accessibili all'utente " + role.name() + " del Comune di Milano",
                                page -> {},
                                page -> {}
                        ))));
    }

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

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(AgreementPage.class, agreement.getId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Should see banner for agreement update to newer version",
                        page -> {},
                        page -> assertBannerIsVisible(page, DESIRED_MESSAGE_BANNER_1)
                )));
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

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(AgreementPage.class, agreement.getId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Should see obsolete version banner when e-service is archiving",
                        page -> {},
                        page -> assertBannerIsVisible(page, DESIRED_MESSAGE_BANNER_2)
                )));
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

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(AgreementPage.class, agreement.getId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Should see no banner when e-service is archiving and agreement uses latest version",
                        page -> {},
                        this::assertNoBannerIsVisible
                )));
    }

    @TestFactory
    Stream<DynamicTest> shouldSeeBanner2WhenDescriptorInArchivingStateAndEserviceInArchivingStateAndAgreementIsNonUpdatable() {
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

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(AgreementPage.class, agreement.getId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Should see obsolete version banner when descriptor and e-service are archiving",
                        page -> {},
                        page -> assertBannerIsVisible(page, DESIRED_MESSAGE_BANNER_2)
                )));
    }

    private void assertBannerIsVisible(
            AgreementPage page,
            String expectedMessage
    ) {
        Assertions.assertThat(readAlertMessages(page))
                .as("Agreement alert messages")
                .contains(expectedMessage);
    }

    private void assertNoBannerIsVisible(AgreementPage page) {
        Assertions.assertThat(readAlertMessages(page))
                .as("Agreement alert messages")
                .doesNotContain(
                        DESIRED_MESSAGE_BANNER_1,
                        DESIRED_MESSAGE_BANNER_2
                );
    }

    private List<String> readAlertMessages(AgreementPage page) {
        return page.alerts().stream()
                .map(alert -> alert.message().read())
                .toList();
    }
}
