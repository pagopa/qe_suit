package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice_template.application.EServiceTemplateVersionUseCase;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplate;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersion;
import it.pagopa.interop.common.eservice_template.domain.EServiceTemplateVersionState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.infrastructure.config.JunitEnvironmentProfiles;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.*;
import it.pagopa.interop.web.eservice.infrastructure.page.ProducerEServiceTemplateSummaryPage;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card.AdditionalInfoTemplateEServiceCard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card.GeneralInfoTemplateEServiceCard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card.TechnicalSpecsTemplateEServiceCard;
import it.pagopa.interop.web.eservice.infrastructure.page.component.template_eservice_summary_card.ThresholdsAttributesTemplateEServiceCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.provider_eservice_summary.EServiceSummaryCard;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

import static it.pagopa.interop.web.eservice.infrastructure.suit.page.ProducerEServiceSummaryPage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@ActiveProfiles(resolver = JunitEnvironmentProfiles.class)
@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class}
)
@TestConstructor(autowireMode = ALL)
@RequiredArgsConstructor
@DisplayName("Web E-Service Template Summary Contract Test")
public class WebProducerEServiceTemplateSummaryContractTest {

    private static final List<String> EXPECTED_BREADCRUMB_LABELS = List.of(
            "Erogazione", "I miei template e-service", "Modifica Template E-Service", "Riepilogo E-Service template"
    );
    private static final String EXPECTED_EROGAZIONE_BREADCRUMB_HREF = "/ui/it/erogazione";
    private static final String EXPECTED_MY_TESERVICES_BREADCRUMB_HREF = "/ui/it/erogazione/template-eservice";

    private static final String CARD_GENERAL_INFO = "Informazioni generali";
    private static final String CARD_THRESHOLDS_ATTRIBUTES = "Soglie e attributi";
    private static final String CARD_TECH_SPECS = "Specifiche tecniche";
    private static final String CARD_ADDITIONAL_INFO = "Informazioni aggiuntive";

    private static final String BTN_DELETE_DRAFT = "Elimina bozza";
    private static final String BTN_EDIT_DRAFT = "Modifica bozza";
    private static final String BTN_PUBLISH = "Pubblica";
    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    private final EServiceTemplateVersionUseCase templateVersionUseCase;

    @TestFactory
    Stream<DynamicTest> summaryPageContractTests() {
        EServiceTemplate template = interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEServiceTemplate(
                        EServiceTemplateVersionState.PUBLISHED
                )
                .waitUntilEServiceTemplate(t ->
                        t.getLatestVersion().getState() == EServiceTemplateVersionState.PUBLISHED)
                .addVersion(EServiceTemplateVersionState.DRAFT)
                .waitUntilEServiceTemplate(t -> t.getVersions().stream()
                        .anyMatch(v -> v.getState() == EServiceTemplateVersionState.DRAFT))
                .get(EServiceTemplate.class);

        EServiceTemplateVersion version = templateVersionUseCase.uploadInterface(
                template,
                template.getLastDraftVersion(),
                "assets/origin-interface.yaml"
        );

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(ProducerEServiceTemplateSummaryPage.class, template.getId().toString(), version.getId().toString())
                .tests(Stream.of(
                        new WebScenario<>("Verifica riepilogo e dettagli delle quattro card",
                                page -> {
                                },
                                page -> {
                                    assertHeaderAndBreadcrumbs(page);
                                    assertSummaryCards(page);
                                    assertFooterActions(page);
                                    assertGeneralInfoTemplateEServiceCard(page);
                                    assertThresholdsAttributesTemplateEServiceCard(page);
                                    assertTechnicalSpecsTemplateEServiceCard(page);
                                    assertAdditionalInfoTemplateEServiceCard(page);
                                })
                ));
    }

    private void assertHeaderAndBreadcrumbs(ProducerEServiceTemplateSummaryPage page) {
        // ES-SUMM-01 - Verify that the button "Torna agli e-service" is enabled
        assertThat(page.backToTemplatesButton().isDisabled())
                .as("Torna ai template abilitato").isFalse();
        page.backToTemplatesButton().readAndAssert("Torna ai template");

        // ES-SUMM-02...ES-SUMM-05 - Verify that the breadcrumbs are present and correct
        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("E-service template creation breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);
        assertThat(page.breadcrumbs().links()).hasSize(3);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Erogazione breadcrumb href")
                .isEqualTo(EXPECTED_EROGAZIONE_BREADCRUMB_HREF);
        assertThat(page.breadcrumbs().links().get(1).getHref())
                .as("I miei template e-service breadcrumb href")
                .isEqualTo(EXPECTED_MY_TESERVICES_BREADCRUMB_HREF);

        // ES-SUMM-06 - Verify that the page title is present and populated
        assertThat(page.pageTitle().get().isPresent()).as("Titolo e-service template presente").isTrue();
        assertThat(page.pageTitle().read()).as("Titolo e-service template valorizzato").isNotBlank();

        // ES-SUMM-07 - Verify that the draft status is present and populated
        assertThat(page.statusChip().get().isPresent()).as("Stato bozza presente").isTrue();
        page.statusChip().readAndAssert(STATUS_DRAFT);
    }

    private void assertSummaryCards(ProducerEServiceTemplateSummaryPage page) {
        // ES-SUMM-08...ES-SUMM-11 - Verify that the cards are present, in the correct order and have the correct title and subtitle
        assertThat(page.summaryCards())
                .extracting(card -> card.header().title().read())
                .containsExactly(
                        CARD_GENERAL_INFO, CARD_THRESHOLDS_ATTRIBUTES, CARD_TECH_SPECS, CARD_ADDITIONAL_INFO
                );
        assertThat(page.summaryCards())
                .extracting(card -> card.header().subtitle().read())
                .containsExactly(
                        "1", "2", "3", "4"
                );
        //Verify that only the first card is initially expanded and the others are closed
        assertThat(page.summaryCards())
                .as("Stato iniziale delle card: solo Informazioni generali espansa")
                .extracting(card -> card.header().get()
                        .orElseThrow(() -> new AssertionError("Intestazione della card non presente"))
                        .getAttributes().get("aria-expanded"))
                .containsExactly("true", "false", "false", "false");
    }

    private void assertGeneralInfoTemplateEServiceCard(ProducerEServiceTemplateSummaryPage page) {
        //Verify that the General Info card has the correct content and values
        GeneralInfoTemplateEServiceCard card = page.generalInfoTemplateEServiceCard();

        card.title().readAndAssert("Informazioni generali");
        card.eserviceTemplateNameLabel().readAndAssert("Nome del template di e-service");
        assertNotBlank(card.eserviceTemplateNameValue().read(), "Value for:Nome del template di e-service");

        card.templateScopeLabel().readAndAssert("A chi è rivolto questo template?");
        assertNotBlank(card.templateScopeValue().read(), "Value for: A chi è rivolto questo template?");

        card.templateUseLabel().readAndAssert("Cosa permette di fare questo template?");
        assertNotBlank(card.templateUseValue().read(), "Value for: Cosa permette di fare questo template?");

        card.exchangeDataLabel().readAndAssert("Scambio dati");
        assertNotBlank(card.exchangeDataValue().read(), "Value for: Scambio dati");

        card.technologyLabel().readAndAssert("Tecnologia API");
        assertNotBlank(card.technologyValue().read(), "Value for: Tecnologia API");

        card.personalDataLabel().readAndAssert("Eroga dati personali");
        assertNotBlank(card.personalDataValue().read(), "Value for: Eroga dati personali");

        card.signalHubUseLabel().readAndAssert("Hai suggerito l'uso di Signal Hub?");
        assertNotBlank(card.signalHubUseValue().read(), "Value for: Hai suggerito l'uso di Signal Hub?");

    }

    private void assertThresholdsAttributesTemplateEServiceCard(ProducerEServiceTemplateSummaryPage page) {
        //Verify that the Thresholds and Attributes card has the correct content and values
        ThresholdsAttributesTemplateEServiceCard card = page.thresholdsAttributesTemplateEServiceCard();
        card.expand();

        assertThat(card.header().get()
                .orElseThrow(() -> new AssertionError("Intestazione Soglie e attributi non presente"))
                .getAttributes())
                .as("Card Soglie e attributi espansa dopo l'apertura")
                .containsEntry("aria-expanded", "true");

        var content = card.content();
        content.apiCallThresholdsTitle().readAndAssert("Soglie suggerite di chiamate API");
        assertField(content.dailyCallsPerConsumer(), "Per fruitore");
        assertField(content.dailyCallsTotal(), "Totale");
        assertAttributeSection(content.certifiedAttributes(), "Attributi Certificati",
                "Sono attributi certificati da una fonte autoritativa riconosciuta.",
                "Questo e-service non richiede attributi certificati");
        assertAttributeSection(content.verifiedAttributes(), "Attributi Verificati",
                "Sono attributi verificati da altre organizzazioni, con la possibilità di richiedere una verifica dall'erogatore.",
                "Questo e-service non richiede attributi verificati");
        assertAttributeSection(content.declaredAttributes(), "Attributi Dichiarati",
                "Sono attributi dichiarati dal fruitore stesso durante la richiesta di fruizione. Non è necessaria una verifica dell'erogatore.",
                "Questo e-service non richiede attributi dichiarati");
    }

    private void assertAttributeSection(ThresholdsAttributesTemplateEServiceCard.AttributeSection section, String title, String description, String emptyMessage) {
        section.title().readAndAssert(title);
        section.description().readAndAssert(description);
        section.emptyStateMessage().readAndAssert(emptyMessage);
    }

        private void assertTechnicalSpecsTemplateEServiceCard(ProducerEServiceTemplateSummaryPage page) {
        //Verify that the Technical Specs card has the correct content and values
        TechnicalSpecsTemplateEServiceCard card = page.technicalSpecsTemplateEServiceCard();
        card.expand();
        assertThat(card.header().get()
                .orElseThrow(() -> new AssertionError("Intestazione Specifiche tecniche non presente"))
                .getAttributes())
                .as("Card Specifiche tecniche espansa dopo l'apertura")
                .containsEntry("aria-expanded", "true");

        var content = card.content();
        content.apiInterface().label().readAndAssert("Interfaccia");
        assertThat(content.apiInterface().attachmentButton().isDisabled())
                .as("Pulsante interfaccia abilitato").isFalse();
        assertField(content.voucherLifespan(), "Durata del voucher");
    }

    private void assertAdditionalInfoTemplateEServiceCard(ProducerEServiceTemplateSummaryPage page) {
        //Verify that the Additional Info card has the correct content and values
        AdditionalInfoTemplateEServiceCard card = page.additionalInfoTemplateEServiceCard();
        card.expand();
        assertThat(card.header().get()
                .orElseThrow(() -> new AssertionError("Intestazione Informazioni aggiuntive non presente"))
                .getAttributes())
                .as("Card Informazioni aggiuntive espansa dopo l'apertura")
                .containsEntry("aria-expanded", "true");

        assertField(card.versionDescription(), "Descrizione versione");
        card.documentationLabel().readAndAssert("Documentazione");
        assertThat(card.documentationValue().get(Gettable.FindPolicy.PRESENT))
                .as("Value of documentation")
                .isPresent();
        assertField(card.manualApproval(), "Hai suggerito di approvare manualmente le richieste di fruizione?");
    }

    private void assertFooterActions(ProducerEServiceTemplateSummaryPage page) {
        // ES-SUMM-12...ES-SUMM-14 - Verify that the action buttons are present and enabled
        assertThat(page.deleteDraftButton().isDisabled()).as("Elimina bozza abilitato").isFalse();
        page.deleteDraftButton().readAndAssert(BTN_DELETE_DRAFT);

        assertThat(page.editDraftButton().isDisabled()).as("Modifica bozza abilitato").isFalse();
        page.editDraftButton().readAndAssert(BTN_EDIT_DRAFT);

        assertThat(page.publishButton().isDisabled()).as("Pubblica abilitato").isFalse();
        page.publishButton().readAndAssert(BTN_PUBLISH);
    }

    private void assertField(EServiceSummaryCard.Field field, String expectedLabel) {
        field.label().readAndAssert(expectedLabel);
        assertNotBlank(field.value().read(), expectedLabel);
    }

    private static void assertNotBlank(String value, String description) {
        assertThat(value).as("%s deve essere valorizzato", description).isNotBlank();

    }

}