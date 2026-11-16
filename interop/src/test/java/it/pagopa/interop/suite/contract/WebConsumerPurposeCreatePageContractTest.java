package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerPurposeCreatePage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerPurposeCreatePageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the consumer purpose create page shows the expected elements for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> consumerAgreementDetailsPageContract() {
        Agreement agreement = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .get(Agreement.class);
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerPurposeCreatePage.class, agreement.getId().toString())
                .tests(contractScenarios());
    }

    private Stream<WebScenario<ConsumerPurposeCreatePage>> contractScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Consumer purpose create page shows the expected content",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                                    softly.assertThat(page.backToPurposesButton().read())
                                            .as("Back to purpose button")
                                            .isEqualTo("Torna alle finalità");
                                    softly.assertThat(page.mandatoryFieldsLabel().read())
                                            .as("Mandatory fields label")
                                            .isEqualTo("*Campi obbligatori");
                                    softly.assertThat(page.sectionTitle().read())
                                            .as("Section title")
                                            .isEqualTo("Informazioni preliminari");
                                    softly.assertThat(page.consumerLabel().read())
                                            .as("Consumer label")
                                            .startsWith("Fruitore");
                                    softly.assertThat(page.consumerInput().read())
                                            .as("Consumer input value")
                                            .isEqualTo("Comune di Milano");
                                    softly.assertThat(page.consumerHelpText().read())
                                            .as("Consumer help text")
                                            .isEqualTo("Tra gli aderenti, puoi selezionare solo il tuo ente o gli aderenti per i quali hai una delega in fruizione attiva");
                                    softly.assertThat(page.eServiceLabel().read())
                                            .as("E-service help text")
                                            .startsWith("E-service da associare");
                                    softly.assertThat(page.eServiceInput().read())
                                            .as("E-service initial input value")
                                            .isBlank();
                                    softly.assertThat(page.eServiceHelpText().read())
                                            .as("E-service help text")
                                            .isEqualTo("Puoi selezionare solo gli e-service per i quali il fruitore selezionato ha una richiesta di fruizione attiva");
                                    softly.assertThat(page.createDraftButton().read())
                                            .as("Create draft button")
                                            .isEqualTo("Crea bozza");
                                }
                        )));
    }
}