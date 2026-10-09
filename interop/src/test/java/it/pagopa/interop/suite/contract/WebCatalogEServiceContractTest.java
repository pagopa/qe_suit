package it.pagopa.interop.suite.contract;

import it.pagopa.application.context.EntityStore;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Label;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.bff.eservice.application.BffEServiceCreationCommand;
import it.pagopa.interop.bff.eservice.application.BffUpdateEServiceDescriptorCommand;
import it.pagopa.interop.common.attribute.domain.Attribute;
import it.pagopa.interop.common.delegation.domain.Delegation;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.Instant;
import java.util.ArrayList;
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

    @Getter
    @Setter
    @AllArgsConstructor
    public class CustomThresholdTestParams {
        private boolean eServiceAsyncExchange;
        private boolean certifiedAttributeToConsumer;
        private boolean certifiedAttributeToDelegator;
        private boolean customThresholdToCertifiedAttributeForConsumer;
        private boolean customThresholdToCertifiedAttributeForDelegator;
        private boolean shouldSeeCustomThresholds;
        private boolean isProducerThePortalUser;
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog1() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                true,
                false,
                true,
                false,
                true,
                false
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
                true,
                false
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
                false,
                false,
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
                true,
                false
        );
        return shouldSeeOrNotCustomThresholdsForOtherTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog8() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                true,
                true,
                true,
                true,
                true,
                false
        );
        return shouldSeeOrNotCustomThresholdsForOtherTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog9() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                true,
                false,
                true,
                false,
                true,
                false
        );
        return shouldSeeOrNotCustomThresholdsForOtherTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog10() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                true,
                false,
                true,
                false,
                true,
                false
        );
        return shouldSeeOrNotCustomThresholdsForOtherTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog11() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                true,
                false,
                true,
                false,
                false,
                true
        );
        return shouldSeeOrNotCustomThresholdsForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdsOnCatalog12() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                true,
                false,
                true,
                false,
                false,
                true
        );
        return shouldSeeOrNotCustomThresholdsForYourTenant(params);
    }

    private Stream<DynamicTest> shouldSeeOrNotCustomThresholdsForYourTenant(CustomThresholdTestParams params) {
        int consumerThreshold = 20;
        int totalThreshold = 40;
        int customThresholdForYourTenant = 30;
        Tenant producer = Tenant.COMUNE_DI_MILANO;
        Tenant consumer = Tenant.COMUNE_DI_POZZALLO;
        Tenant portalUser = (params.isProducerThePortalUser) ? producer : consumer;

        TenantRef consumerTenantRef = TenantRef.of(consumer.getOrganizationId());
        if (params.certifiedAttributeToConsumer) { interopJourney
                .withProducer(producer, UserRole.ADMIN)
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
        List<DescriptorAttributeSeed> requirement = new ArrayList<>();
        if (params.customThresholdToCertifiedAttributeForConsumer) // Fruitore
            addNthAttributeWithCustomThresholdToAttributeRequirement(-1, customThresholdForYourTenant, requirement);
        attributesSeed.addCertifiedItem(requirement);
        updateCommand.attributes(attributesSeed);

        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);

        if (params.certifiedAttributeToConsumer) interopJourney.assignCertifiedAttribute(consumerTenantRef);

        EService eService = entityStore.getLastOrThrow(EService.class);
        return webContractValidator
                .as(User.getTenantAdmin(portalUser), portalUser)
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
        Tenant producer = Tenant.COMUNE_DI_MILANO;
        Tenant delegator = Tenant.COMUNE_DI_COMUN_NUOVO; // Fruitore delegante
        Tenant delegatee = Tenant.COMUNE_DI_POZZALLO;    // Fruitore delegato
        Tenant portalUser = (params.isProducerThePortalUser) ? producer : delegatee;

        TenantRef delegatorTenantRef = TenantRef.of(delegator.getOrganizationId());
        TenantRef delegateeTenantRef = TenantRef.of(delegatee.getOrganizationId());

        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createCertifiedAttribute()  // Per delegante
                .createCertifiedAttribute(); // Per delegato

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
        List<DescriptorAttributeSeed> requirement = new ArrayList<>();
        if (params.customThresholdToCertifiedAttributeForDelegator) // Fruitore delegante
            addNthAttributeWithCustomThresholdToAttributeRequirement(1, customThresholdForDelegator, requirement);
        if (params.customThresholdToCertifiedAttributeForConsumer)  // Fruitore delegato
            addNthAttributeWithCustomThresholdToAttributeRequirement(-1, customThresholdForDelegatee, requirement);
        attributesSeed.addCertifiedItem(requirement);
        updateCommand.attributes(attributesSeed);

        interopJourney
                .withProducer(producer, UserRole.ADMIN)
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);

        if (params.certifiedAttributeToDelegator)
            interopJourney.assignCertifiedAttribute(delegatorTenantRef, 1);
        if (params.certifiedAttributeToConsumer)
            interopJourney.assignCertifiedAttribute(delegateeTenantRef, -1);

        EService eService = entityStore.getLastOrThrow(EService.class);
        interopJourney
                .withConsumer(delegator, UserRole.ADMIN)
                .createConsumerDelegation(delegateeTenantRef, eService.getRef())
                .withConsumer(delegatee, UserRole.ADMIN)
                .approveConsumerDelegation(entityStore.getLastOrThrow(Delegation.class).getRef());

        return webContractValidator
                .as(User.getTenantAdmin(portalUser), portalUser)
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
                                if (params.certifiedAttributeToDelegator)
                                    assertLabelEqualsTo(page.otherTenantDailyThreshold(), String.valueOf(customThresholdForDelegator));
                            } else {
                                Assertions.assertThat(page.customApiCallsThresholdTitle().get().isPresent()).isFalse();
                                Assertions.assertThat(page.yourTenantDailyThreshold().get().isPresent()).isFalse();
                                Assertions.assertThat(page.otherTenantDailyThreshold().get().isPresent()).isFalse();
                            }
                        }
                )));
    }

    private void addNthAttributeWithCustomThresholdToAttributeRequirement(int nthAttribute, int customThreshold, List<DescriptorAttributeSeed> requirement) {
        DescriptorAttributeSeed attributeSeed = new DescriptorAttributeSeed();
        Attribute attribute;
        if (nthAttribute == 1) {
            attribute = entityStore.getFirstOrThrow(Attribute.class);
        } else if (nthAttribute == -1) {
            attribute = entityStore.getLastOrThrow(Attribute.class);
        } else {
            throw new IllegalArgumentException("Non supported Nth attribute value: " + nthAttribute);
        }
        attributeSeed.setId(attribute.getId());
        attributeSeed.setDailyCallsPerConsumer(customThreshold);
        attributeSeed.setExplicitAttributeVerification(false);
        requirement.add(attributeSeed);
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
