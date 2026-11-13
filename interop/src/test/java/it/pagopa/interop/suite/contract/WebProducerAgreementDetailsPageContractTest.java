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
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerAgreementDetailsPage;
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
        properties = {"spring.profiles.include=junit", "channel.web.browser=chrome", "channel.web.headless=false"}
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerAgreementDetailsPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the producer agreement details page shows the expected
     * title, description, filter labels and table of elements for the tenant administrator.
     */
    @TestFactory
    Stream<DynamicTest> producerAgreementDetailsPageContract() {
        Agreement agreement = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .get(Agreement.class);
        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(ProducerAgreementDetailsPage.class, agreement.getId().toString())
                .tests(contractScenarios());
    }

    private Stream<WebScenario<ProducerAgreementDetailsPage>> contractScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Producer agreement details page shows the expected content and filters",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                                    softly.assertThat(page.backToRequestsButton().read())
                                            .as("Back to request button")
                                            .isEqualTo("Torna alle richieste");
                                    softly.assertThat(page.statusChip().text().read())
                                            .as("Status chip")
                                            .isEqualTo("attivo");
                                    softly.assertThat(page.suspendButton().read())
                                            .as("Suspend button")
                                            .isEqualTo("Sospendi");

                                    softly.assertThat(page.generalInfoSection()).as("La sezione general info non dev'essere null").isNotNull();
                                    softly.assertThat(page.generalInfoSection().sectionTitle().read())
                                            .as("General Info section title")
                                            .isEqualTo("Informazioni generali");
                                    softly.assertThat(page.generalInfoSection().eServiceLabel().read())
                                            .as("General Info e-service label")
                                            .isEqualTo("Richiesta per l’e-service");
                                    softly.assertThat(page.generalInfoSection().eServiceLink().getHref())
                                            .as("E-service link should not be blank")
                                            .isNotBlank();
                                    softly.assertThat(page.generalInfoSection().consumerLabel().read())
                                            .as("General Info consumer label")
                                            .isEqualTo("Fruitore");
                                    softly.assertThat(page.generalInfoSection().consumerValue().read())
                                            .as("General Info consumer value")
                                            .isEqualTo("PagoPA S.p.A.");
                                    softly.assertThat(page.generalInfoSection().downloadAgreementButton().read())
                                            .as("Download agreement button")
                                            .isEqualTo("Scarica richiesta di fruizione");
                                    softly.assertThat(page.generalInfoSection().contactsButton().read())
                                            .as("View consumer contacts button")
                                            .isEqualTo("Visualizza contatti fruitore");

                                    softly.assertThat(page.certifiedAttributesSection())
                                            .as("La sezione attributi certificati non dev'essere null")
                                            .isNotNull();
                                    softly.assertThat(page.certifiedAttributesSection().title().read())
                                            .as("Attributi certificati title")
                                            .isEqualTo("Attributi Certificati");
                                    softly.assertThat(page.certifiedAttributesSection().description().read())
                                            .as("Attributi certificati description")
                                            .isEqualTo("Sono attributi certificati da una fonte autoritativa riconosciuta.");
                                    softly.assertThat(page.certifiedAttributesSection().infoCard().read())
                                            .as("Attributi certificati info")
                                            .isEqualTo("Il tuo ente non richiede attributi certificati");

                                    softly.assertThat(page.verifiedAttributesSection())
                                            .as("La sezione attributi verificati non dev'essere null")
                                            .isNotNull();
                                    softly.assertThat(page.verifiedAttributesSection().title().read())
                                            .as("Attributi verificati title")
                                            .isEqualTo("Attributi Verificati");
                                    softly.assertThat(page.verifiedAttributesSection().description().read())
                                            .as("Attributi verificati description")
                                            .isEqualTo("Sono attributi verificati da altre organizzazioni, con la possibilità di richiedere una verifica dall’erogatore.");
                                    softly.assertThat(page.verifiedAttributesSection().infoCard().read())
                                            .as("Attributi verificati info")
                                            .isEqualTo("Il tuo ente non richiede attributi verificati");

                                    softly.assertThat(page.declaredAttributesSection())
                                            .as("La sezione attributi dichiarati non dev'essere null")
                                            .isNotNull();
                                    softly.assertThat(page.declaredAttributesSection().title().read())
                                            .as("Attributi dichiarati title")
                                            .isEqualTo("Attributi Dichiarati");
                                    softly.assertThat(page.declaredAttributesSection().description().read())
                                            .as("Attributi dichiarati description")
                                            .isEqualTo("Sono attributi dichiarati dal fruitore stesso durante la richiesta di fruizione. Non è necessaria una verifica dell’erogatore.");
                                    softly.assertThat(page.declaredAttributesSection().infoCard().read())
                                            .as("Attributi dichiarati info")
                                            .isEqualTo("Il tuo ente non richiede attributi dichiarati");
                                }
                        )));
    }
}