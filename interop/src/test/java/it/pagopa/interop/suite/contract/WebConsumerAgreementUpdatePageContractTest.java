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
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerAgreementUpdatePage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {"channel.web.browser=chrome", "channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebConsumerAgreementUpdatePageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the consumer agreement update (draft) page shows the expected
     * header, general info, verified/declared attribute sections, certified attributes drawer
     * and footer action buttons for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> consumerAgreementUpdatePageContract() {
        EService eservice = interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(Tenant.PAGO_PA, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .get(EService.class);
        UUID eserviceId = eservice.getId();
        UUID descriptorId = eservice.getActiveDescriptor().getId();

        Agreement agreement = interopJourney
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .linkAgreement(AgreementState.DRAFT)
                .get(Agreement.class);
        UUID agreementId = agreement.getId();

        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ConsumerAgreementUpdatePage.class, agreementId.toString())
                .tests(contractScenarios(eserviceId, descriptorId));
    }

    private Stream<WebScenario<ConsumerAgreementUpdatePage>> contractScenarios(
            UUID eserviceId,
            UUID descriptorId
    ) {
        return Stream.of(
                new WebScenario<>(
                        "Consumer agreement update page shows the expected content for a draft agreement",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                            softly.assertThat(page.pageTitle().read())
                                    .as("Page title")
                                    .isEqualTo("Gestisci richiesta di fruizione");
                            softly.assertThat(page.backToRequestsButton().read())
                                    .as("Back to requests button")
                                    .isEqualTo("Torna alle richieste");
                            softly.assertThat(page.statusChip().text().read())
                                    .as("Status chip")
                                    .isEqualTo("in bozza");

                            softly.assertThat(page.generalInfoSection())
                                    .as("General info section should not be null")
                                    .isNotNull();
                            softly.assertThat(page.generalInfoSection().sectionTitle().read())
                                    .as("General info section title")
                                    .isEqualTo("Informazioni generali");
                            softly.assertThat(page.generalInfoSection().eServiceLabel().read())
                                    .as("General info e-service label")
                                    .isEqualTo("Richiesta relativa a");
                            softly.assertThat(page.generalInfoSection().eServiceLink().getHref())
                                    .as("E-service link href should contain eserviceId and descriptorId")
                                    .contains(eserviceId.toString())
                                    .contains(descriptorId.toString());
                            softly.assertThat(page.generalInfoSection().eServiceLink().read())
                                    .as("E-service link text should contain versione")
                                    .contains("versione");
                            softly.assertThat(page.generalInfoSection().providerLabel().read())
                                    .as("General info provider label")
                                    .isEqualTo("Erogatore");
                            softly.assertThat(page.generalInfoSection().providerValue().read())
                                    .as("General info provider value")
                                    .isNotBlank();
                            softly.assertThat(page.generalInfoSection().consumerLabel().read())
                                    .as("General info consumer label")
                                    .isEqualTo("Fruitore");
                            softly.assertThat(page.generalInfoSection().consumerValue().read())
                                    .as("General info consumer value")
                                    .isNotBlank();
                            softly.assertThat(page.generalInfoSection().certifiedAttributesButton().read())
                                    .as("View certified attributes button")
                                    .isEqualTo("Visualizza attributi certificati");

                            softly.assertThat(page.verifiedAttributesSection())
                                    .as("Verified attributes section should not be null")
                                    .isNotNull();
                            softly.assertThat(page.verifiedAttributesSection().title().read())
                                    .as("Verified attributes title")
                                    .isEqualTo("Attributi Verificati");
                            softly.assertThat(page.verifiedAttributesSection().description().read())
                                    .as("Verified attributes description")
                                    .isEqualTo("Sono attributi verificati da altre organizzazioni, con la possibilità di richiedere una verifica dall’erogatore.");
                            softly.assertThat(page.verifiedAttributesSection().infoCard().read())
                                    .as("Verified attributes info card")
                                    .isEqualTo("L'erogatore non richiede attributi verificati");

                            softly.assertThat(page.declaredAttributesSection())
                                    .as("Declared attributes section should not be null")
                                    .isNotNull();
                            softly.assertThat(page.declaredAttributesSection().title().read())
                                    .as("Declared attributes title")
                                    .isEqualTo("Attributi Dichiarati");
                            softly.assertThat(page.declaredAttributesSection().description().read())
                                    .as("Declared attributes description")
                                    .isEqualTo("Sono attributi dichiarati dal fruitore stesso durante la richiesta di fruizione. Non è necessaria una verifica dell’erogatore.");
                            softly.assertThat(page.declaredAttributesSection().infoCard().read())
                                    .as("Declared attributes info card")
                                    .isEqualTo("L'erogatore non richiede attributi dichiarati");

                            softly.assertThat(page.deleteDraftButton().read())
                                    .as("Delete draft button")
                                    .isEqualTo("Elimina bozza");
                            softly.assertThat(page.deleteDraftButton().isDisabled())
                                    .as("Delete draft button should be enabled")
                                    .isFalse();
                            softly.assertThat(page.saveDraftButton().read())
                                    .as("Save draft button")
                                    .isEqualTo("Salva bozza");
                            softly.assertThat(page.saveDraftButton().isDisabled())
                                    .as("Save draft button should be enabled")
                                    .isFalse();
                            softly.assertThat(page.submitRequestButton().read())
                                    .as("Submit request button")
                                    .isEqualTo("Inoltra richiesta");

                            page.generalInfoSection().certifiedAttributesButton().click();
                            softly.assertThat(page.certifiedAttributesDrawer().title().read())
                                    .as("Certified attributes drawer title")
                                    .isEqualTo("Attributi certificati richiesti");
                            softly.assertThat(page.certifiedAttributesDrawer().infoCard().read())
                                    .as("Certified attributes drawer info card")
                                    .isEqualTo("L'erogatore non richiede attributi certificati");
                            page.certifiedAttributesDrawer().close();
                        })
                )
        );
    }
}

