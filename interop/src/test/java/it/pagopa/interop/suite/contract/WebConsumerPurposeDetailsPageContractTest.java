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
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerPurposeDetailsPage;
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
        properties = {
                "spring.profiles.include=junit",
                "channel.web.browser=chrome"
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerPurposeDetailsPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the consumer purpose details page shows the expected
     * title, description, filter labels and table of elements for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> consumerPurposeDetailsPageContract() {
        Purpose purpose = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(PurposeVersionState.ACTIVE)
                .get(Purpose.class);
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerPurposeDetailsPage.class, purpose.getId().toString())
                .tests(contractScenarios(purpose));
    }

    private Stream<WebScenario<ConsumerPurposeDetailsPage>> contractScenarios(Purpose purpose) {
        return Stream.of(
                new WebScenario<>(
                        "Consumer purpose details page shows the expected content and filters",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                                    softly.assertThat(page.backToPurposesButton().read())
                                            .as("Back to purposes button")
                                            .isEqualTo("Torna alle finalità");

                                    softly.assertThat(page.purposeTitle().read())
                                            .as("Purpose title")
                                            .isEqualTo(purpose.getTitle());
                                    softly.assertThat(page.purposeDescription().read())
                                            .as("Purpose description")
                                            .isEqualTo(purpose.getDescription());
                                    softly.assertThat(page.statusChip().text().read())
                                            .as("Status chip")
                                            .isEqualTo("attivo");
                                    softly.assertThat(page.archiveButton().read())
                                            .as("Archive button")
                                            .isEqualTo("Archivia");
                                    softly.assertThat(page.duplicateButton().read())
                                            .as("Duplicate button")
                                            .isEqualTo("Duplica");
                                    softly.assertThat(page.suspendButton().read())
                                            .as("Suspend button")
                                            .isEqualTo("Sospendi");
                                    softly.assertThat(page.alerts()).as("La sezione degli alert non dev'essere vuota").isNotEmpty();
                                    softly.assertThat(page.alerts().get(0).message().read()).isEqualTo("Questa finalità è attiva ma non ha client associati. Associa il tuo primo client per accedere ai dati dell’e-service.");

                                    softly.assertThat(page.detailsTabButton().isSelected()).as("Purpose Details tab should be selected").isTrue();
                                    softly.assertThat(page.clientsTabButton().isSelected()).as("Associated Clients tab should not be selected").isFalse();
                                    softly.assertThat(page.clientsTabPanel().get().isPresent()).as("Clients Tab Panel should not be visible now").isFalse();

                                    softly.assertThat(page.detailsTabButton().read()).as("Purpose Details tab title").isEqualTo("Dettagli finalità");
                                    softly.assertThat(page.clientsTabButton().read()).as("Associated Clients tab title").isEqualTo("Client associati");

                                    softly.assertThat(page.detailsTabPanel().riskAnalysisSection()).isNotNull();
                                    softly.assertThat(page.detailsTabPanel().riskAnalysisSection().sectionTitle().read())
                                            .as("Risk Analysis section title")
                                            .isEqualTo("Assegnazione analisi del rischio");
                                    softly.assertThat(page.detailsTabPanel().riskAnalysisSection().modeLabel().read())
                                            .as("Risk Analysis mode label")
                                            .isEqualTo("Modalità");
                                    softly.assertThat(page.detailsTabPanel().riskAnalysisSection().modeValue().read())
                                            .as("Risk Analysis mode value should not be blank")
                                            .isNotBlank();

                                    softly.assertThat(page.detailsTabPanel().generalInfoSection()).isNotNull();
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().sectionTitle().read())
                                            .as("General Info title")
                                            .isEqualTo("Informazioni generali");
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().eServiceLabel().read())
                                            .as("General Info E-service label")
                                            .isEqualTo("E-service associato");
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().eServiceLink().getHref())
                                            .as("General Info E-service link should not be blank")
                                            .isNotBlank();
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().providerLabel().read())
                                            .as("General Info Provider label")
                                            .isEqualTo("Erogatore");
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().providerValue().read())
                                            .as("General Info Provider value")
                                            .isEqualTo("PagoPA S.p.A.");
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().purposeDescriptionLabel().read())
                                            .as("General Info Purpose description label")
                                            .isEqualTo("Descrizione della finalità");
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().purposeDescriptionValue().read())
                                            .as("General Info Purpose description value")
                                            .isNotBlank();
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().downloadRiskAnalysisButton().read())
                                            .as("General Info Risk Analysis download button")
                                            .isEqualTo("Scarica analisi del rischio");
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().goToAgreementLink().read())
                                            .as("General Info Agreement link text")
                                            .isEqualTo("Vai a richiesta di fruizione");
                                    softly.assertThat(page.detailsTabPanel().generalInfoSection().goToAgreementLink().getHref())
                                            .as("General Info Agreement link href")
                                            .isNotBlank();

                                    softly.assertThat(page.detailsTabPanel().apiCallsSection()).isNotNull();
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().sectionTitle().read())
                                            .as("Api Calls title")
                                            .isEqualTo("Stima di chiamate API");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().sectionDescription().read())
                                            .as("Api Calls description")
                                            .isEqualTo("Il numero di chiamate previste ogni giorno verso l'e-service");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().currentEstimateLabel().read())
                                            .as("Api Calls estimate label")
                                            .isEqualTo("Stima attuale");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().currentEstimateUnitLabel().read())
                                            .as("Api Calls estimate unit")
                                            .isEqualTo("Chiamate API/giorno");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().currentEstimateValue().read())
                                            .as("Api Calls estimate value")
                                            .isNotBlank();
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().requestEstimateChangeButton().read())
                                            .as("Api Calls estimate change button")
                                            .isEqualTo("Richiedi cambio stima");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().providerThresholdsLabel().read())
                                            .as("Api Calls provider threshold label")
                                            .isEqualTo("Soglie definite dall’erogatore");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().providerThresholdsUnitLabel().read())
                                            .as("Api Calls provider threshold unit")
                                            .isEqualTo("Chiamate API/giorno");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().consumerThresholdLabel().read())
                                            .as("Api Calls provider threshold for consumer label")
                                            .isEqualTo("per fruitore");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().consumerThresholdValue().read())
                                            .as("Api Calls provider threshold for consumer value")
                                            .isNotBlank();
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().totalThresholdLabel().read())
                                            .as("Api Calls provider total threshold label")
                                            .isEqualTo("totali");
                                    softly.assertThat(page.detailsTabPanel().apiCallsSection().totalThresholdValue().read())
                                            .as("Api Calls provider total threshold value")
                                            .isNotBlank();

                                    page.clientsTabButton().click();

                                    softly.assertThat(page.detailsTabButton().isSelected()).as("Purpose Details tab should not be selected").isFalse();
                                    softly.assertThat(page.clientsTabButton().isSelected()).as("Associated Clients tab should be selected").isTrue();
                                    softly.assertThat(page.detailsTabPanel().get().isPresent()).as("Details Tab Panel should not be visible now").isFalse();

                                    softly.assertThat(page.clientsTabPanel().addButton().read())
                                            .as("Add button")
                                            .isEqualTo("Aggiungi");
                                    softly.assertThat(page.clientsTabPanel().clientsTable().tHead().rows().get(0).cells().get(0).read())
                                            .as("Client Table header")
                                            .isEqualTo("Client");
                                    softly.assertThat(page.clientsTabPanel().clientsTable().tBody().rows().get(0).cells().get(0).read())
                                            .as("Client Table empty row")
                                            .isEqualTo("Non ci sono client associati a questa finalità");
                                }
                        )));
    }
}