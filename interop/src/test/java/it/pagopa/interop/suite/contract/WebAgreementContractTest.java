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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
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


    /**
     * Verifies that the consumer sees the "obsolete, new version available" banner on an agreement
     * bound to an archiving descriptor when a newer version of the e-service exists.
     * <p>
     * Setup: v1 published with an active agreement, v2 published (v1 becomes deprecated),
     * then v1 is archived with a 60-day grace period.
     */
    @TestFactory
    Stream<DynamicTest> agreementOnArchivingDescriptorWithNewerVersionMustShowUpdateAvailableBanner() {
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
                        page -> {
                        },
                        page -> assertBannerIsVisible(page, DESIRED_MESSAGE_BANNER_1)
                )));
    }

    /**
     * Verifies that the consumer sees the "obsolete" banner, without the new version notice,
     * when the whole e-service is archiving and the agreement is still on v1.
     * <p>
     * Setup: v1 published with an active agreement, v2 published, then the e-service is archived
     * with a 60-day grace period.
     */
    @TestFactory
    Stream<DynamicTest> agreementOnOlderVersionOfArchivingEServiceMustShowObsoleteBanner() {
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
                        page -> {
                        },
                        page -> assertBannerIsVisible(page, DESIRED_MESSAGE_BANNER_2)
                )));
    }

    /**
     * Verifies that no obsolete banner is shown when the e-service is archiving but the agreement
     * is already on the latest version, so there is nothing to update.
     * <p>
     * Setup: v1 and v2 published, agreement created afterwards, then the e-service is archived.
     */
    @TestFactory
    Stream<DynamicTest> agreementOnLatestVersionOfArchivingEServiceMustNotShowAnyBanner() {
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
                        page -> {
                        },
                        this::assertNoBannerIsVisible
                )));
    }

    /**
     * Verifies that the "obsolete" banner is shown when the descriptor linked to the agreement
     * is archiving and the whole e-service is archiving as well.
     * <p>
     * Setup: v1 published with an active agreement, v2 published, v1 archived, then the whole
     * e-service archived, all with a 60-day grace period.
     */
    @TestFactory
    Stream<DynamicTest> agreementOnArchivingDescriptorOfArchivingEServiceMustShowObsoleteBanner() {
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
                        page -> {
                        },
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