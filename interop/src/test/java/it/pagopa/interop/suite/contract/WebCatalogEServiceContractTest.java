package it.pagopa.interop.suite.contract;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.eservice.application.command.EServiceCreationCommand;
import it.pagopa.interop.common.eservice.application.command.UpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.eservice.domain.*;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.TenantRef;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.generated.openapi.clients.bff.model.AgreementApprovalPolicy;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributeSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.DescriptorAttributesSeed;
import it.pagopa.interop.web.eservice.infrastructure.page.EServiceDetailPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebCatalogEServiceContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final EntityStore entityStore;

    public record CustomThresholdTestParams(
            boolean eServiceAsyncExchange,
            boolean certifiedAttributeToConsumer,
            boolean certifiedAttributeToDelegator,
            boolean customThresholdToCertifiedAttributeForConsumer,
            boolean customThresholdToCertifiedAttributeForDelegator,
            boolean shouldSeeCustomThresholds
    ) {}

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog1() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                true,
                false,
                true,
                false,
                true
        );
        return shouldSeeOrNotCustomThresholdsForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog2() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                true,
                false,
                true,
                false,
                true
        );
        return shouldSeeOrNotCustomThresholdsForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog3() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                false,
                false,
                false,
                false,
                false
        );
        return shouldSeeOrNotCustomThresholdsForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog4() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                false,
                false,
                false,
                false,
                false
        );
        return shouldSeeOrNotCustomThresholdsForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog5() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                true,
                false,
                false,
                false,
                false
        );
        return shouldSeeOrNotCustomThresholdsForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog6() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                true,
                false,
                true,
                false,
                false
        );
        return shouldSeeOrNotCustomThresholdsForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog7() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                true,
                true,
                true,
                true,
                true
        );
        return shouldSeeOrNotCustomThresholdsForOtherTenant(params);
    }

    private Stream<DynamicTest> shouldSeeOrNotCustomThresholdsForYourTenant(CustomThresholdTestParams params) {
        int consumerThreshold = 20;
        int totalThreshold = 40;
        int customThresholdForYourTenant = 30;
        Tenant consumer = Tenant.COMUNE_DI_POZZALLO;

        TenantRef consumerTenantRef = TenantRef.of(consumer.getOrganizationId());
        if (params.certifiedAttributeToConsumer) { interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createCertifiedAttribute();
        }

        EServiceCreationCommand eServiceCommand = new BffEServiceCreationCommand()
                .name("e-service-" + Instant.now().getEpochSecond())
                .description("Primo descrittore")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isAsync(params.eServiceAsyncExchange)
                .handlePersonalData(false)
                .isConsumerDelegable(true);

        UpdateEServiceDescriptorCommand updateCommand = new BffUpdateEServiceDescriptorCommand()
                .dailyCallsPerConsumer(consumerThreshold)
                .dailyCallsTotal(totalThreshold)
                .voucherLifespan(60)
                .audience(List.of("Audience"))
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed();
        if (params.customThresholdToCertifiedAttributeForConsumer) // Fruitore
            addNthAttributeWithCustomThresholdToAttributesSeed(-1, customThresholdForYourTenant, attributesSeed);
        updateCommand.attributes(attributesSeed);

        interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);

        if (params.certifiedAttributeToConsumer) interopJourney.assignCertifiedAttribute(consumerTenantRef);

        EService eService = entityStore.getLastOrThrow(EService.class);
        return webContractValidator
                .as(User.getTenantAdmin(consumer), consumer)
                .on(EServiceDetailPage.class, eService.getId().toString(), eService.getActiveDescriptor().getId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Should see correct values for thresholds and custom thresholds",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.thresholdsAndAttributesTitle().read()).as("Section title").isNotBlank();
                            Assertions.assertThat(page.apiCallsThresholdTitle().read()).as("Subsection title").isNotBlank();
                            assertLabelEqualsTo(page.consumerDailyThreshold(), String.valueOf(consumerThreshold));
                            assertLabelEqualsTo(page.totalDailyThreshold(), String.valueOf(totalThreshold));
                            if (params.shouldSeeCustomThresholds) {
                                Assertions.assertThat(page.customApiCallsThresholdTitle().read()).as("Subsection title").isNotBlank();
                                assertLabelEqualsTo(page.yourTenantDailyThreshold(), String.valueOf(customThresholdForYourTenant));
                            } else {
                                Assertions.assertThat(page.customApiCallsThresholdTitle().get().isPresent()).isFalse();
                                Assertions.assertThat(page.yourTenantDailyThreshold().get().isPresent()).isFalse();
                                Assertions.assertThat(page.otherTenantDailyThreshold().get().isPresent()).isFalse();
                            }
                        }
                )));
    }

    private Stream<DynamicTest> shouldSeeOrNotCustomThresholdsForOtherTenant(CustomThresholdTestParams params) {
        int consumerThreshold = 20;
        int totalThreshold = 40;
        int customThresholdForDelegator = 35;
        int customThresholdForDelegatee = 30;
        Tenant delegator = Tenant.COMUNE_DI_COMUN_NUOVO;
        Tenant delegatee = Tenant.COMUNE_DI_POZZALLO; // Consumer

        TenantRef delegatorTenantRef = TenantRef.of(delegator.getOrganizationId());
        TenantRef delegateeTenantRef = TenantRef.of(delegatee.getOrganizationId());

        if (params.certifiedAttributeToConsumer) { interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createCertifiedAttribute()  // Per delegante
                .createCertifiedAttribute(); // Per delegato
        }

        EServiceCreationCommand eServiceCommand = new BffEServiceCreationCommand()
                .name("e-service-" + Instant.now().getEpochSecond())
                .description("Primo descrittore")
                .technology(EServiceTechnology.REST)
                .mode(EServiceMode.DELIVER)
                .isAsync(params.eServiceAsyncExchange)
                .handlePersonalData(false)
                .isConsumerDelegable(true);

        UpdateEServiceDescriptorCommand updateCommand = new BffUpdateEServiceDescriptorCommand()
                .dailyCallsPerConsumer(consumerThreshold)
                .dailyCallsTotal(totalThreshold)
                .voucherLifespan(60)
                .audience(List.of("Audience"))
                .agreementApprovalPolicy(AgreementApprovalPolicy.AUTOMATIC);

        DescriptorAttributesSeed attributesSeed = new DescriptorAttributesSeed();
        if (params.customThresholdToCertifiedAttributeForDelegator) // Fruitore delegante
            addNthAttributeWithCustomThresholdToAttributesSeed(1, customThresholdForDelegator, attributesSeed);
        if (params.customThresholdToCertifiedAttributeForConsumer)  // Fruitore delegato
            addNthAttributeWithCustomThresholdToAttributesSeed(-1, customThresholdForDelegatee, attributesSeed);
        updateCommand.attributes(attributesSeed);

        interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);

        if (params.certifiedAttributeToDelegator) interopJourney.assignCertifiedAttribute(delegatorTenantRef);
        if (params.certifiedAttributeToConsumer) interopJourney.assignCertifiedAttribute(delegateeTenantRef);

        EService eService = entityStore.getLastOrThrow(EService.class);
        return webContractValidator
                .as(User.getTenantAdmin(delegatee), delegatee)
                .on(EServiceDetailPage.class, eService.getId().toString(), eService.getActiveDescriptor().getId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Should see correct values for thresholds and custom thresholds",
                        page -> {},
                        page -> {
                            Assertions.assertThat(page.thresholdsAndAttributesTitle().read()).as("Section title").isNotBlank();
                            Assertions.assertThat(page.apiCallsThresholdTitle().read()).as("Subsection title").isNotBlank();
                            assertLabelEqualsTo(page.consumerDailyThreshold(), String.valueOf(consumerThreshold));
                            assertLabelEqualsTo(page.totalDailyThreshold(), String.valueOf(totalThreshold));
                            if (params.shouldSeeCustomThresholds) {
                                Assertions.assertThat(page.customApiCallsThresholdTitle().read()).as("Subsection title").isNotBlank();
                                assertLabelEqualsTo(page.yourTenantDailyThreshold(), String.valueOf(customThresholdForDelegatee));
                            } else {
                                Assertions.assertThat(page.customApiCallsThresholdTitle().get().isPresent()).isFalse();
                                Assertions.assertThat(page.yourTenantDailyThreshold().get().isPresent()).isFalse();
                                Assertions.assertThat(page.otherTenantDailyThreshold().get().isPresent()).isFalse();
                            }
                        }
                )));
    }

    private void addNthAttributeWithCustomThresholdToAttributesSeed(int nthAttribute, int customThreshold, DescriptorAttributesSeed seed) {
        if (customThreshold > 0) {
            DescriptorAttributeSeed certifiedItem = new DescriptorAttributeSeed();
            Attribute attribute;
            if (nthAttribute == 1) {
                attribute = entityStore.getFirstOrThrow(Attribute.class);
            } else if (nthAttribute == -1) {
                attribute = entityStore.getLastOrThrow(Attribute.class);
            } else {
                throw new IllegalArgumentException("Non supported Nth attribute value: " + nthAttribute);
            }
            certifiedItem.setId(attribute.getId());
            certifiedItem.setDailyCallsPerConsumer(customThreshold);
            certifiedItem.setExplicitAttributeVerification(false);
            seed.addCertifiedItem(List.of(certifiedItem));
        }
    }

    private void assertLabelEqualsTo(
            Label label,
            String expectedValue
    ) {
        Assertions.assertThat(label.read())
                .as("Label value")
                .contains(expectedValue);
    }
}
