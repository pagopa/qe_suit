package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Stepper;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerPurposeFromTemplateEditPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerPurposeFromTemplateEditPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the consumer purpose from template edit page shows the expected elements
     */
    @TestFactory
    Stream<DynamicTest> consumerPurposeFromTemplateEditPageContract() {
        Purpose purpose = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(PurposeVersionState.ACTIVE)
                .get(Purpose.class);
        PurposeTemplate purposeTemplate = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(PurposeVersionState.ACTIVE)
                .createPurposeTemplate(Tenant.COMUNE_DI_MILANO, PurposeTemplateState.PUBLISHED)
                .get(PurposeTemplate.class);

        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerPurposeFromTemplateEditPage.class, purpose.getId().toString(), purposeTemplate.getId().toString())
                .tests(contractScenarios(purposeTemplate));
    }

    private Stream<WebScenario<ConsumerPurposeFromTemplateEditPage>> contractScenarios(PurposeTemplate purposeTemplate) {
        return Stream.of(
                new WebScenario<>(
                        "Consumer purpose from template edit page shows the expected content",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                            softly.assertThat(page.backToPurposesButton().read())
                                    .as("Back to purpose button")
                                    .isEqualTo("Torna alle finalità");
                            softly.assertThat(page.mandatoryFieldsLabel().read())
                                    .as("Mandatory fields label")
                                    .isEqualTo("*Campi obbligatori");
//                            softly.assertThat(page.stepper().steps()).as("Stepper steps must be populated").isNotEmpty();
//                            Stepper.Step firstStep = page.stepper().steps().get(0);
//                            softly.assertThat(firstStep.label().read()).as("Step label").isEqualTo("TODO");
//                            softly.assertThat(firstStep.number().read()).as("Step number").isEqualTo("1");
//                            softly.assertThat(firstStep.isActive()).as("").isTrue();

                            softly.assertThat(page.generalStepPanel().get().isPresent())
                                    .as("General step panel must be present")
                                    .isTrue();
                            softly.assertThat(page.generalStepPanel().sectionTitle().read())
                                    .as("General step panel title")
                                    .isEqualTo("Informazioni sulla finalità");
                            softly.assertThat(page.generalStepPanel().sectionDescription().read())
                                    .as("General step panel description")
                                    .isEqualTo("Queste informazioni sono state precompilate da chi ha creato il template di finalità agevolata e non sono modificabili.");
                            softly.assertThat(page.generalStepPanel().descriptionSectionTitle().read())
                                    .as("")
                                    .isEqualTo("Descrizione");
                            softly.assertThat(page.generalStepPanel().purposeNameLabel().read())
                                    .as("Purpose template name label")
                                    .isEqualTo("Nome della finalità");
                            softly.assertThat(page.generalStepPanel().purposeNameValue().read())
                                    .as("Purpose template name value")
                                    .isEqualTo(purposeTemplate.getTitle());
                            softly.assertThat(page.generalStepPanel().purposeDescriptionLabel().read())
                                    .as("Purpose template description label")
                                    .isEqualTo("Descrizione della finalità");
                            softly.assertThat(page.generalStepPanel().purposeDescriptionValue().read())
                                    .as("Purpose template description value")
                                    .isEqualTo(purposeTemplate.getDescription());
                            softly.assertThat(page.generalStepPanel().freeAccessLabel().read())
                                    .as("Purpose template free access label")
                                    .isEqualTo("Accesso a titolo gratuito");
                            softly.assertThat(page.generalStepPanel().freeAccessReasonValue().read())
                                    .as("Purpose template free access value")
                                    .isNotBlank();
                            if (page.generalStepPanel().freeAccessValue().read().equals("Sì")) {
                                softly.assertThat(page.generalStepPanel().freeAccessReasonLabel().read())
                                        .as("Purpose template free access reason label")
                                        .isEqualTo("Motivazione del titolo gratuito");
                                softly.assertThat(page.generalStepPanel().freeAccessReasonValue().read())
                                        .as("Purpose template free access reason value")
                                        .isNotBlank();
                            }
                            softly.assertThat(page.generalStepPanel().technicalInformationSectionTitle().read())
                                    .as("Technical information section title")
                                    .isEqualTo("Informazioni tecniche");
                            softly.assertThat(page.generalStepPanel().instanceNameLabel().read())
                                    .as("Instance name label")
                                    .startsWith("Nome dell'istanza");
                            softly.assertThat(page.generalStepPanel().instanceNameInput().read())
                                    .as("Modifiable instance name input value")
                                    .isEqualTo(purposeTemplate.getTitle());
                        })
                )
        );
    }
}
