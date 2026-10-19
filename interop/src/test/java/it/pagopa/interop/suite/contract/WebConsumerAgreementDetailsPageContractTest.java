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
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerAgreementDetailsPage;
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
public class WebConsumerAgreementDetailsPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the consumer agreement details page shows the expected
     * title, description, filter labels and table of elements for the tenant administrator.
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
                .on(ConsumerAgreementDetailsPage.class, agreement.getId().toString())
                .tests(contractScenarios());
    }

    private Stream<WebScenario<ConsumerAgreementDetailsPage>> contractScenarios() {
        return Stream.of(
                new WebScenario<>(
                        "Consumer agreement details page shows the expected content and filters",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                                    softly.assertThat(page.backToRequestsButton().read())
                                            .as("Back to request button")
                                            .isEqualTo("Torna alle richieste");
                                    softly.assertThat(page.statusChip().read())
                                            .as("Status chip")
                                            .isEqualTo("attivo");
                                    softly.assertThat(page.archiveButton().read())
                                            .as("Archive button")
                                            .isEqualTo("Archivia");
                                    softly.assertThat(page.suspendButton().read())
                                            .as("Suspend button")
                                            .isEqualTo("Sospendi");
                                    softly.assertThat(page.alerts()).as("La sezione degli alert non dev'essere vuota").isNotEmpty();
                                    softly.assertThat(page.alerts().get(0).read()).isEqualTo("Per accedere ai dati crea la tua prima finalità.");

                                    softly.assertThat(page.generalInfoSection()).as("La sezione general info non dev'essere null").isNotNull();
                                    softly.assertThat(page.generalInfoSection().sectionTitle().read())
                                            .as("General Info section title")
                                            .isEqualTo("Informazioni generali");
                                    softly.assertThat(page.generalInfoSection().eServiceLabel().read())
                                            .as("General Info e-service label")
                                            .isEqualTo("Richiesta per l’e-service");
                                    softly.assertThat(page.generalInfoSection().eServiceLink().read())
                                            .as("E-service link should not be blank")
                                            .isNotBlank();
                                    softly.assertThat(page.generalInfoSection().providerLabel().read())
                                            .as("General Info provider label")
                                            .isEqualTo("Erogatore");
                                    softly.assertThat(page.generalInfoSection().providerValue().read())
                                            .as("General Info provider value")
                                            .isEqualTo("PagoPA S.p.A.");
                                    softly.assertThat(page.generalInfoSection().consumerLabel().read())
                                            .as("General Info consumer label")
                                            .isEqualTo("Fruitore");
                                    softly.assertThat(page.generalInfoSection().consumerValue().read())
                                            .as("General Info consumer value")
                                            .isEqualTo("Comune di Milano");
                                    softly.assertThat(page.generalInfoSection().downloadAgreementButtonLabel().read())
                                            .as("Download agreement button label")
                                            .isEqualTo("Scarica richiesta di fruizione");
                                    softly.assertThat(page.generalInfoSection().certifiedAttributesButton().read())
                                            .as("View certified attributes button")
                                            .isEqualTo("Visualizza attributi certificati");
                                    softly.assertThat(page.generalInfoSection().providerContactsButton().read())
                                            .as("View provider contacts button")
                                            .isEqualTo("Visualizza contatti erogatore");

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
                                            .isEqualTo("L'erogatore non richiede attributi certificati");

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
                                            .isEqualTo("L'erogatore non richiede attributi verificati");

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
                                            .isEqualTo("L'erogatore non richiede attributi dichiarati");
                                }
                        )));
    }
}