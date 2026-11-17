package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EServiceMode;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplateDetailsPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = {
                "spring.profiles.include=junit",
                "channel.web.browser=chrome",
"channel.web.headless=true"
        }
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class WebProducerEServiceTemplateDetailsPageContractTest {

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    /**
     * Verifies that the producer e-service template details page shows the expected
     * header, actions, tabs, general/technical/thresholds-attributes sections,
     * and empty-state tenants tab for a published template.
     */
    @TestFactory
    Stream<DynamicTest> producerEServiceTemplateDetailsPageContract() {
        EServiceTemplate template = interopJourney
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(EServiceTemplateVersionState.PUBLISHED)
                .waitUntilEServiceTemplate(t ->
                        t.getLatestVersion().getState() == EServiceTemplateVersionState.PUBLISHED)
                .get(EServiceTemplate.class);
        EServiceTemplateVersion version = template.getLastPublishedVersion();

        return webContractValidator
                .as(
                        User.getTenantAdmin(Tenant.COMUNE_DI_MILANO),
                        Tenant.COMUNE_DI_MILANO
                )
                .on(
                        ProducerEServiceTemplateDetailsPage.class,
                        template.getId().toString(),
                        version.getId().toString()
                )
                .tests(contractScenarios(template, version));
    }

    private Stream<WebScenario<ProducerEServiceTemplateDetailsPage>> contractScenarios(
            EServiceTemplate template,
            EServiceTemplateVersion version
    ) {
        return Stream.of(
                new WebScenario<>(
                        "Producer e-service template details page shows expected content in Details tab",
                        page -> {
                        },
                        page -> assertSoftly(softly -> {
                            softly.assertThat(page.backToTemplatesButton().read())
                                    .as("Back to templates button")
                                    .isEqualTo("Torna ai template");
                            softly.assertThat(page.pageTitle().read())
                                    .as("Page title")
                                    .isEqualTo(template.getName());
                            softly.assertThat(page.statusChip().text().read())
                                    .as("Status chip")
                                    .isEqualTo("attivo");
                            softly.assertThat(page.createNewDraftButton().read())
                                    .as("Create new draft button")
                                    .isEqualTo("Crea bozza da template");
                            softly.assertThat(page.suspendButton().read())
                                    .as("Suspend button")
                                    .isEqualTo("Sospendi");

                            softly.assertThat(page.detailsTabButton().isSelected())
                                    .as("Details tab should be selected")
                                    .isTrue();
                            softly.assertThat(page.detailsTabButton().read())
                                    .as("Details tab title")
                                    .isEqualTo("Dettagli template");
                            softly.assertThat(page.tenantsTabButton().isSelected())
                                    .as("Tenants tab should not be selected")
                                    .isFalse();
                            softly.assertThat(page.tenantsTabButton().read())
                                    .as("Tenants tab title")
                                    .isEqualTo("Enti aderenti");
                            softly.assertThat(page.tenantsTabPanel().get().isPresent())
                                    .as("Tenants tab panel should not be visible")
                                    .isFalse();

                            var generalInfo = page.detailsTabPanel().generalInfoSection();
                            softly.assertThat(generalInfo).isNotNull();
                            softly.assertThat(generalInfo.sectionTitle().read())
                                    .as("General info section title")
                                    .isEqualTo("Informazioni template");
                            softly.assertThat(generalInfo.versionLabel().read())
                                    .as("Version label")
                                    .isEqualTo("Stai vedendo la versione");
                            softly.assertThat(generalInfo.versionValue().read())
                                    .as("Version value")
                                    .isEqualTo(version.getVersion());
                            softly.assertThat(generalInfo.personalDataLabel().read())
                                    .as("Personal data label")
                                    .isEqualTo(expectedPersonalDataLabel(template.getMode()));
                            softly.assertThat(generalInfo.personalDataValue().read())
                                    .as("Personal data value")
                                    .isEqualTo(expectedPersonalDataValue(template.getPersonalData()));
                            softly.assertThat(generalInfo.nameHeading().read())
                                    .as("Template name heading")
                                    .isEqualTo("Nome del template di e-service");
                            softly.assertThat(generalInfo.nameValue().read())
                                    .as("Template name value")
                                    .isEqualTo(template.getName());
                            softly.assertThat(generalInfo.editNameButton().read())
                                    .as("Edit name button")
                                    .isEqualTo("Modifica");
                            softly.assertThat(generalInfo.intendedTargetHeading().read())
                                    .as("Intended target heading")
                                    .isEqualTo("A chi è rivolto il template");
                            softly.assertThat(generalInfo.intendedTargetValue().read())
                                    .as("Intended target value")
                                    .isEqualTo(template.getIntendedTarget());
                            softly.assertThat(generalInfo.editIntendedTargetButton().read())
                                    .as("Edit intended target button")
                                    .isEqualTo("Modifica");
                            softly.assertThat(generalInfo.descriptionHeading().read())
                                    .as("Description heading")
                                    .isEqualTo("Descrizione dell’e-service");
                            softly.assertThat(generalInfo.descriptionValue().read())
                                    .as("Description value")
                                    .isEqualTo(template.getDescription());
                            softly.assertThat(generalInfo.editDescriptionButton().read())
                                    .as("Edit description button")
                                    .isEqualTo("Modifica");
                            softly.assertThat(generalInfo.versionDescriptionHeading().read())
                                    .as("Version description heading")
                                    .isEqualTo("Descrizione della versione");
                            softly.assertThat(generalInfo.versionDescriptionValue().read())
                                    .as("Version description value")
                                    .isNotBlank();

                            var technicalInfo = page.detailsTabPanel().technicalInfoSection();
                            softly.assertThat(technicalInfo).isNotNull();
                            softly.assertThat(technicalInfo.sectionTitle().read())
                                    .as("Technical info section title")
                                    .isEqualTo("Specifiche tecniche e-service");
                            softly.assertThat(technicalInfo.sectionDescription().read())
                                    .as("Technical info section description")
                                    .isEqualTo("In questa sezione potrai verificare le specifiche dell’e-service ed apportare modifiche.");
                            softly.assertThat(technicalInfo.technologyLabel().read())
                                    .as("Technology label")
                                    .isEqualTo("Tecnologia");
                            softly.assertThat(technicalInfo.technologyValue().read())
                                    .as("Technology value")
                                    .isEqualTo(template.getTechnology().name());
                            softly.assertThat(technicalInfo.modeLabel().read())
                                    .as("Mode label")
                                    .isEqualTo("Tipologia e-service");
                            softly.assertThat(technicalInfo.modeValue().read())
                                    .as("Mode value")
                                    .isEqualTo(expectedModeValue(template.getMode()));
                            softly.assertThat(technicalInfo.voucherHeading().read())
                                    .as("Voucher heading")
                                    .isEqualTo("Voucher");
                            softly.assertThat(technicalInfo.editVoucherButton().read())
                                    .as("Edit voucher button")
                                    .isEqualTo("Modifica");
                            softly.assertThat(technicalInfo.voucherLifespanLabel().read())
                                    .as("Voucher lifespan label")
                                    .isEqualTo("Durata del voucher");
                            softly.assertThat(technicalInfo.voucherLifespanValue().read())
                                    .as("Voucher lifespan value in minutes")
                                    .isEqualTo(String.valueOf(version.getVoucherLifespan() / 60));
                            softly.assertThat(technicalInfo.documentationHeading().read())
                                    .as("Documentation heading")
                                    .isEqualTo("Documentazione");
                            softly.assertThat(technicalInfo.editDocumentationButton().read())
                                    .as("Edit documentation button")
                                    .isEqualTo("Modifica");
                            softly.assertThat(technicalInfo.documentsListLabel().read())
                                    .as("Documents list label")
                                    .isEqualTo("Elenco documenti");
//                            softly.assertThat(technicalInfo.apiSpecLink().read())
//                                    .as("API specification link")
//                                    .isEqualTo("Specifica API");
                            softly.assertThat(technicalInfo.usefulLinksHeading().read())
                                    .as("Useful links heading")
                                    .isEqualTo("Link utili");
                            softly.assertThat(technicalInfo.implementationGuideLink().read())
                                    .as("Implementation guide link text")
                                    .isEqualTo("Come implemento e gestisco un template di e-service?");
                            softly.assertThat(technicalInfo.implementationGuideLink().getHref())
                                    .as("Implementation guide link href")
                                    .isNotBlank();
                            softly.assertThat(technicalInfo.updateInstanceGuideLink().read())
                                    .as("Update instance guide link text")
                                    .isEqualTo("Come faccio ad aggiornare una istanza di e-service?");
                            softly.assertThat(technicalInfo.updateInstanceGuideLink().getHref())
                                    .as("Update instance guide link href")
                                    .isNotBlank();
                            softly.assertThat(technicalInfo.wellKnownLink().read())
                                    .as("Well-known link text")
                                    .isEqualTo("URL .well-known");
                            softly.assertThat(technicalInfo.wellKnownLink().getHref())
                                    .as("Well-known link href")
                                    .isNotBlank();

                            var thresholdsAndAttributes = page.detailsTabPanel().thresholdsAndAttributesSection();
                            softly.assertThat(thresholdsAndAttributes).isNotNull();
                            softly.assertThat(thresholdsAndAttributes.sectionTitle().read())
                                    .as("Thresholds and attributes section title")
                                    .isEqualTo("Soglie e attributi");
                            softly.assertThat(thresholdsAndAttributes.sectionDescription().read())
                                    .as("Thresholds and attributes section description")
                                    .isEqualTo("Qui trovi le soglie e gli attributi che sono richiesti per la fruizione di questo e-service.");
                            softly.assertThat(thresholdsAndAttributes.thresholdsHeading().read())
                                    .as("Thresholds heading")
                                    .isEqualTo("Soglie di chiamate API");
                            softly.assertThat(thresholdsAndAttributes.editThresholdsButton().read())
                                    .as("Edit thresholds button")
                                    .isEqualTo("Modifica");

                            if (version.getDailyCallsPerConsumer() != null && version.getDailyCallsTotal() != null) {
                                softly.assertThat(thresholdsAndAttributes.dailyCallsPerConsumerLabel().read())
                                        .as("Daily calls per consumer label")
                                        .isEqualTo("Soglia giornaliera per fruitore");
                                softly.assertThat(thresholdsAndAttributes.dailyCallsPerConsumerValue().read())
                                        .as("Daily calls per consumer value")
                                        .isEqualTo(String.valueOf(version.getDailyCallsPerConsumer()));
                                softly.assertThat(thresholdsAndAttributes.dailyCallsTotalLabel().read())
                                        .as("Daily calls total label")
                                        .isEqualTo("Soglia giornaliera totale");
                                softly.assertThat(thresholdsAndAttributes.dailyCallsTotalValue().read())
                                        .as("Daily calls total value")
                                        .isEqualTo(NumberFormat.getNumberInstance(Locale.ITALY)
                                                .format(version.getDailyCallsTotal()));
                            } else {
                                softly.assertThat(thresholdsAndAttributes.emptyThresholdsCard().read())
                                        .as("Empty thresholds card")
                                        .isEqualTo("Questo template di e-service non ha soglie suggerite");
                            }

                            softly.assertThat(thresholdsAndAttributes.attributesHeading().read())
                                    .as("Attributes heading")
                                    .isEqualTo("Attributi");
                            softly.assertThat(thresholdsAndAttributes.emptyAttributesCard().read())
                                    .as("Empty attributes card")
                                    .isEqualTo("Questo template di e-service non richiede attributi");
                        })
                ),
                new WebScenario<>(
                        "Switching to Enti aderenti tab shows empty tenants alert",
                        page -> page.tenantsTabButton().click(),
                        page -> assertSoftly(softly -> {
                            softly.assertThat(page.tenantsTabButton().isSelected())
                                    .as("Tenants tab should be selected")
                                    .isTrue();
                            softly.assertThat(page.detailsTabButton().isSelected())
                                    .as("Details tab should not be selected")
                                    .isFalse();
                            softly.assertThat(page.detailsTabPanel().get().isPresent())
                                    .as("Details tab panel should not be visible")
                                    .isFalse();
                            softly.assertThat(page.tenantsTabPanel().emptyStateAlert().message().read())
                                    .as("Empty tenants alert message")
                                    .isEqualTo("Questo template non è stato ancora utilizzato dagli enti.");
                        })
                )
        );
    }

    private static String expectedPersonalDataLabel(EServiceMode mode) {
        return mode == EServiceMode.RECEIVE ? "Riceve dati personali" : "Eroga dati personali";
    }

    private static String expectedPersonalDataValue(Boolean personalData) {
        if (personalData == null) {
            return "Non indicato";
        }
        return personalData ? "Sì" : "No";
    }

    private static String expectedModeValue(EServiceMode mode) {
        return mode == EServiceMode.RECEIVE ? "Riceve" : "Eroga";
    }
}

