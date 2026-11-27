package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplate;
import it.pagopa.interop.common.purpose_template.domain.PurposeTemplateState;
import it.pagopa.interop.web.purpose_template.infrastructure.page.ConsumerPurposeCreateWithoutTemplatePage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.openqa.selenium.Keys;
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
public class WebConsumerPurposeCreateWithoutTemplatePageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the consumer purpose create page shows the expected elements for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> consumerPurposeCreatePageContract() {
        interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurposeTemplate(Tenant.COMUNE_DI_MILANO, PurposeTemplateState.PUBLISHED)
                .get(PurposeTemplate.class);
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerPurposeCreateWithoutTemplatePage.class)
                .tests(contractScenarios());
    }

    private Stream<WebScenario<ConsumerPurposeCreateWithoutTemplatePage>> contractScenarios() {
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

                                    softly.assertThat(page.preliminaryInformationSection().sectionTitle().read())
                                            .as("Section title")
                                            .isEqualTo("Informazioni preliminari");
                                    softly.assertThat(page.preliminaryInformationSection().consumerLabel().read())
                                            .as("Consumer label")
                                            .startsWith("Fruitore");
                                    softly.assertThat(page.preliminaryInformationSection().consumerInput().read())
                                            .as("Consumer input value")
                                            .isEqualTo("Comune di Milano");
                                    softly.assertThat(page.preliminaryInformationSection().consumerHelpText().read())
                                            .as("Consumer help text")
                                            .isEqualTo("Tra gli aderenti, puoi selezionare solo il tuo ente o gli aderenti per i quali hai una delega in fruizione attiva");
                                    softly.assertThat(page.preliminaryInformationSection().eServiceLabel().read())
                                            .as("E-service help text")
                                            .startsWith("E-service da associare");
                                    softly.assertThat(page.preliminaryInformationSection().eServiceInput().read())
                                            .as("E-service initial input value")
                                            .isBlank();
                                    softly.assertThat(page.preliminaryInformationSection().eServiceHelpText().read())
                                            .as("E-service help text")
                                            .isEqualTo("Puoi selezionare solo gli e-service per i quali il fruitore selezionato ha una richiesta di fruizione attiva");

                                    softly.assertThat(page.purposeTemplateSection().get())
                                            .as("Purpose template section gets only loaded when e-service input is populated")
                                            .isNotPresent();
                                    page.preliminaryInformationSection().eServiceInput().write("eservice" + Keys.ARROW_DOWN + Keys.ENTER);
                                    softly.assertThat(page.purposeTemplateSection().get())
                                            .as("Purpose template section should be loaded when e-service input is populated")
                                            .isPresent();

                                    softly.assertThat(page.purposeTemplateSection().sectionTitle().read())
                                            .as("Purpose template section title")
                                            .isEqualTo("Compilazione agevolata della finalità");
                                    softly.assertThat(page.purposeTemplateSection().sectionDescription().read())
                                            .as("Purpose template section description")
                                            .isEqualTo("La funzionalità di compilazione agevolata mette a disposizione annotazioni, documenti di supporto e alcune risposte precompilate per facilitare la formulazione dell’analisi del rischio. Il modello è reso disponibile per il riuso da altri enti aderenti e può essere utilizzato senza vincoli.");
                                    softly.assertThat(page.purposeTemplateSection().usePurposeTemplateLabel().read())
                                            .as("Use purpose template label")
                                            .isEqualTo("Utilizza la funzionalità di compilazione agevolata");

                                    page.purposeTemplateSection().usePurposeTemplateSwitch().click();

                                    softly.assertThat(page.purposeTemplateSection().usePurposeTemplateSwitch().isChecked())
                                            .as("Use purpose template switch")
                                            .isTrue();
                                    softly.assertThat(page.purposeTemplateSection().showOnlySuggestedTemplatesLabel().read())
                                            .as("Show only suggested templates label")
                                            .isEqualTo("Mostra solo template suggeriti");
                                    softly.assertThat(page.purposeTemplateSection().showOnlySuggestedTemplatesSwitch().isChecked())
                                            .as("Show only suggested templates switch")
                                            .isFalse();
                                    softly.assertThat(page.purposeTemplateSection().purposeTemplateLabel().read())
                                            .as("Purpose template input label")
                                            .startsWith("Template per la compilazione agevolata della finalità");
                                    softly.assertThat(page.purposeTemplateSection().purposeTemplateInput().read())
                                            .as("Purpose template input value")
                                            .isNotBlank();
                                    softly.assertThat(page.purposeTemplateSection().acknowledgeResponsibilityLabel().read())
                                            .as("Acknowledge responsibility label")
                                            .isEqualTo("Dichiaro di aver compreso che, anche in presenza di suggerimenti precompilati, è mia responsabilità verificare e validare tutte le informazioni prima della pubblicazione.");
                                    softly.assertThat(page.purposeTemplateSection().acknowledgeResponsibilitySwitch().isChecked())
                                            .as("Acknowledge responsibility switch")
                                            .isFalse();
                                    softly.assertThat(page.purposeTemplateSection().warningAlert().message().read())
                                            .as("Warning alert message")
                                            .isEqualTo("Se scegli la compilazione agevolata, non potrai assegnare la compilazione o l’approvazione dell’analisi del rischio al valutatore.");
                                    softly.assertThat(page.createDraftButton().read())
                                            .as("Create draft button")
                                            .isEqualTo("Crea bozza");
                                }
                        )));
    }
}