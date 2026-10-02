package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.adapter.model.FindPolicy;
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
            boolean customThresholdToCertifiedAttribute,
            boolean shouldSeeCustomThresholds
    ) {}

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdForYourTenant1() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                true,
                true,
                true
        );
        return shouldSeeOrNotCustomThresholdForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdForYourTenant2() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                true,
                true,
                true
        );
        return shouldSeeOrNotCustomThresholdForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdForYourTenant3() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                false,
                false,
                false
        );
        return shouldSeeOrNotCustomThresholdForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdForYourTenant4() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                false,
                false,
                false
        );
        return shouldSeeOrNotCustomThresholdForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdForYourTenant5() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                false,
                true,
                false,
                false
        );
        return shouldSeeOrNotCustomThresholdForYourTenant(params);
    }

    @TestFactory
    Stream<DynamicTest> checkCustomThresholdForYourTenant6() {
        CustomThresholdTestParams params = new CustomThresholdTestParams(
                true,
                true,
                true,
                false
        );
        return shouldSeeOrNotCustomThresholdForYourTenant(params);
    }

    private Stream<DynamicTest> shouldSeeOrNotCustomThresholdForYourTenant(CustomThresholdTestParams params) {
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

        if (params.customThresholdToCertifiedAttribute) {
            DescriptorAttributeSeed certifiedItem = new DescriptorAttributeSeed();
            Attribute attribute = entityStore.getLastOrThrow(Attribute.class);
            certifiedItem.setId(attribute.getId());
            certifiedItem.setDailyCallsPerConsumer(customThresholdForYourTenant);
            certifiedItem.setExplicitAttributeVerification(false);
            attributesSeed.addCertifiedItem(List.of(certifiedItem));
        }
        updateCommand.attributes(attributesSeed);

        interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(eServiceCommand, EServiceDescriptorState.DRAFT)
                .updateDescriptor(updateCommand, EServiceDescriptorState.PUBLISHED);

        if (params.certifiedAttributeToConsumer)
            interopJourney.assignCertifiedAttribute(consumerTenantRef);

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

    private void assertLabelEqualsTo(
            Label label,
            String expectedValue
    ) {
        Assertions.assertThat(label.read())
                .as("Label value")
                .contains(expectedValue);
    }
}
