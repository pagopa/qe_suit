package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EService;
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
import it.pagopa.interop.web.eservice.infrastructure.page.PurposeProducerDetailsPage;
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
        properties = {
                "channel.web.browser=chrome"
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebPurposeProducerDetailsPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the producer purpose details page shows the expected header, actions,
     * general information, technical specifications and load estimate sections for the producer tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> purposeProducerDetailsPageContract() {
        EService eservice = interopJourney
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);
        Agreement agreement = interopJourney
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .get(Agreement.class);

        Purpose purpose = interopJourney
                .createPurpose(PurposeVersionState.ACTIVE)
                .get(Purpose.class);
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.PAGO_PA),
                        Tenant.PAGO_PA
                )
                .on(PurposeProducerDetailsPage.class, purpose.getId().toString())
                .tests(contractScenarios(eservice, agreement, purpose));
    }

    private Stream<WebScenario<PurposeProducerDetailsPage>> contractScenarios(EService eService, Agreement agreement, Purpose purpose) {
        return Stream.of(
                new WebScenario<>(
                        "Producer purpose details page shows the expected content",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                            softly.assertThat(page.backToPurposesButton().read())
                                    .as("Back to purposes button")
                                    .isEqualTo("Torna alle finalità");
                            softly.assertThat(page.purposeTitle().read())
                                    .as("Purpose title")
                                    .isEqualTo(purpose.getTitle());
                            softly.assertThat(page.statusChip().text().read())
                                    .as("Status chip")
                                    .isEqualTo("attivo");
                            softly.assertThat(page.suspendButton().read())
                                    .as("Suspend button")
                                    .isEqualTo("Sospendi");
                            softly.assertThat(page.alerts())
                                    .as("Alerts should be empty for ACTIVE purpose")
                                    .isEmpty();

                            softly.assertThat(page.generalInfoSection())
                                    .as("General info section should not be null")
                                    .isNotNull();
                            softly.assertThat(page.generalInfoSection().sectionTitle().read())
                                    .as("General info section title")
                                    .isEqualTo("Informazioni generali");
                            softly.assertThat(page.generalInfoSection().eServiceLabel().read())
                                    .as("General info e-service label")
                                    .isEqualTo("E-service associato");
                            softly.assertThat(page.generalInfoSection().eServiceLink().read())
                                    .as("General info e-service link text should not be blank")
                                    .isNotBlank();
                            softly.assertThat(page.generalInfoSection().eServiceLink().getHref())
                                    .as("General info e-service link href should not be blank")
                                    .isNotBlank();
                            String actualHref = "/ui/it/erogazione/e-service/" + eService.getId() + "/" + eService.getActiveDescriptor().getId();
                            softly.assertThat(page.generalInfoSection().eServiceLink().getHref())
                                    .as("General info e-service link href should not be blank")
                                    .isEqualTo(actualHref);
                            softly.assertThat(page.generalInfoSection().consumerLabel().read())
                                    .as("General info consumer label")
                                    .isEqualTo("Fruitore");
                            softly.assertThat(page.generalInfoSection().consumerValue().read())
                                    .as("General info consumer value")
                                    .isEqualTo("Comune di Milano");
                            softly.assertThat(page.generalInfoSection().purposeDescriptionLabel().read())
                                    .as("General info purpose description label")
                                    .isEqualTo("Descrizione della finalità");
                            softly.assertThat(page.generalInfoSection().purposeDescriptionValue().read())
                                    .as("General info purpose description value")
                                    .isEqualTo(purpose.getDescription());
                            softly.assertThat(page.generalInfoSection().goToAgreementLink().read())
                                    .as("General info agreement link text")
                                    .isEqualTo("Vai a richiesta di fruizione");
                            softly.assertThat(page.generalInfoSection().goToAgreementLink().getHref())
                                    .as("General info agreement link href should not be blank")
                                    .isNotBlank();
                            String actualHrefVaiARichiestaFruizione = "/ui/it/erogazione/richieste/" + agreement.getId();
                            softly.assertThat(page.generalInfoSection().goToAgreementLink().getHref())
                                    .as("General info agreement link href should not be blank")
                                    .isEqualTo(actualHrefVaiARichiestaFruizione);

                            softly.assertThat(page.technicalInfoSection())
                                    .as("Technical info section should not be null")
                                    .isNotNull();
                            softly.assertThat(page.technicalInfoSection().sectionTitle().read())
                                    .as("Technical info section title")
                                    .isEqualTo("Specifiche tecniche finalità");
                            softly.assertThat(page.technicalInfoSection().sectionDescription().read())
                                    .as("Technical info section description")
                                    .isEqualTo("In questa sezione potrai verificare le specifiche della finalità.");
                            softly.assertThat(page.technicalInfoSection().purposeIdLabel().read())
                                    .as("Technical info purpose ID label")
                                    .isEqualTo("ID finalità");
                            softly.assertThat(page.technicalInfoSection().purposeIdValue().read())
                                    .as("Technical info purpose ID value")
                                    .isEqualTo(purpose.getId().toString());
                            softly.assertThat(page.technicalInfoSection().copyPurposeIdButton().get().isPresent())
                                    .as("Copy purpose ID button should be present")
                                    .isTrue();
                            softly.assertThat(page.technicalInfoSection().consumerIdLabel().read())
                                    .as("Technical info consumer ID label")
                                    .isEqualTo("ID fruitore");
                            softly.assertThat(page.technicalInfoSection().consumerIdValue().read())
                                    .as("Technical info consumer ID value")
                                    .isEqualTo(purpose.getConsumerId().toString());
                            softly.assertThat(page.technicalInfoSection().copyConsumerIdButton().get().isPresent())
                                    .as("Copy consumer ID button should be present")
                                    .isTrue();

                            softly.assertThat(page.loadEstimateSection())
                                    .as("Load estimate section should not be null")
                                    .isNotNull();
                            softly.assertThat(page.loadEstimateSection().sectionTitle().read())
                                    .as("Load estimate section title")
                                    .isEqualTo("Stima di chiamate API");
                            softly.assertThat(page.loadEstimateSection().sectionDescription().read())
                                    .as("Load estimate section description")
                                    .isEqualTo("Il numero di chiamate previste ogni giorno verso l'e-service");
                            softly.assertThat(page.loadEstimateSection().currentEstimateTitle().read())
                                    .as("Current estimate card title")
                                    .isEqualTo("Stima attuale del fruitore");
                            softly.assertThat(page.loadEstimateSection().currentEstimateUnitLabel().read())
                                    .as("Current estimate unit label")
                                    .isEqualTo("Chiamate API/giorno");
                            softly.assertThat(page.loadEstimateSection().currentEstimateValue().read())
                                    .as("Current estimate value should not be blank")
                                    .isNotBlank();
                            softly.assertThat(page.loadEstimateSection().thresholdsTitle().read())
                                    .as("Thresholds card title")
                                    .isEqualTo("Soglie definite");
                            softly.assertThat(page.loadEstimateSection().thresholdsUnitLabel().read())
                                    .as("Thresholds unit label")
                                    .isEqualTo("Chiamate API/giorno");
                            softly.assertThat(page.loadEstimateSection().consumerThresholdLabel().read())
                                    .as("Consumer threshold label")
                                    .isEqualTo("per fruitore");
                            softly.assertThat(page.loadEstimateSection().consumerThresholdValue().read())
                                    .as("Consumer threshold value should not be blank")
                                    .isNotBlank();
                            softly.assertThat(page.loadEstimateSection().consumerThresholdValue().read())
                                    .as("Consumer threshold value should not be blank")
                                    .isEqualTo(eService.getActiveDescriptor().getDailyCallsPerConsumer().toString());
                            softly.assertThat(page.loadEstimateSection().totalThresholdLabel().read())
                                    .as("Total threshold label")
                                    .isEqualTo("totali");
                            softly.assertThat(page.loadEstimateSection().totalThresholdValue().read())
                                    .as("Total threshold value should not be blank")
                                    .isNotBlank();
                            softly.assertThat(page.loadEstimateSection().totalThresholdValue().read())
                                    .as("Consumer threshold value should not be blank")
                                    .isEqualTo(eService.getActiveDescriptor().getDailyCallsTotal().toString());
                        })
                )
        );
    }
}

