package it.pagopa.interop.suite.contract;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.application.EServiceDescriptorUseCase;
import it.pagopa.interop.common.agreement.domain.AgreementApprovalPolicy;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.eservice.domain.EServiceTechnology;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceSummaryPage;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.EServiceSummaryCard.Field;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.GeneralInfoCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.TechnicalSpecsCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.AdditionalInfoCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.ThresholdsAttributesCard;
import it.pagopa.interop.web.eservice.infrastructure.suit.component.eservice_modify_summary.ThresholdsAttributesCard.AttributeSection;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.stream.Stream;
import java.util.List;

import static it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceSummaryPage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@SpringBootTest(
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = ALL)
@RequiredArgsConstructor
@DisplayName("Web E-Service Summary Contract Test")
public class WebEServiceSummaryContractTest {

    private static final String ESERVICE_DESCRIPTION = "Descrizione e-service per il riepilogo";
    private static final String VERSION_DESCRIPTION = "Versione per il test contrattuale del riepilogo";
    private static final EServiceTechnology ESERVICE_TECHNOLOGY = EServiceTechnology.REST;
    private static final boolean ASYNC_EXCHANGE = false;
    private static final boolean PERSONAL_DATA = false;
    private static final boolean CONSUMER_DELEGABLE = true;
    private static final boolean CLIENT_ACCESS_DELEGABLE = true;
    private static final boolean SIGNAL_HUB_ENABLED = false;
    private static final int DAILY_CALLS_PER_CONSUMER = 1;
    private static final int DAILY_CALLS_TOTAL = 2;
    private static final String AUDIENCE = "QA";
    private static final AgreementApprovalPolicy APPROVAL_POLICY = AgreementApprovalPolicy.AUTOMATIC;

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final EServiceDescriptorUseCase eServiceDescriptorUseCase;

    @TestFactory
    Stream<DynamicTest> summaryPageContractTests() {
        EService eService = prepareDraft();
        String[] pathParams = {eService.getId().toString(), eService.getLastDraftDescriptor().getId().toString()};
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceSummaryPage.class, pathParams)
                .tests(Stream.of(
                        new WebScenario<>("Verifica riepilogo e dettagli delle quattro card",
                                page -> {},
                                page -> {
                                    assertHeaderAndBreadcrumbs(page);
                                    assertSummaryCards(page);
                                    assertFooterActions(page);
                                    assertGeneralInfoCard(page);
                                    assertThresholdsAttributesCard(page);
                                    assertTechnicalSpecsCard(page, eService);
                                    assertAdditionalInfoCard(page);
                                })
                        ));
    }

    private EService prepareDraft() {
        interopJourney
                .switchChannel(Channel.BFF)
                .withProducer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN);
        var creationCommand = eServiceDescriptorUseCase.getDefaultEServiceCreationCommand(ASYNC_EXCHANGE)
                .description(ESERVICE_DESCRIPTION)
                .technology(ESERVICE_TECHNOLOGY)
                .handlePersonalData(PERSONAL_DATA)
                .isConsumerDelegable(CONSUMER_DELEGABLE)
                .isClientAccessDelegable(CLIENT_ACCESS_DELEGABLE)
                .isSignalHubEnabled(SIGNAL_HUB_ENABLED);
        EService eService = interopJourney
                .createEService(creationCommand, EServiceDescriptorState.DRAFT)
                .get(EService.class);

        var descriptor = eService.getLastDraftDescriptor();
        var command = eServiceDescriptorUseCase.getUpdateEServiceDescriptorCommand(
                        DAILY_CALLS_PER_CONSUMER, DAILY_CALLS_TOTAL)
                .description(VERSION_DESCRIPTION)
                .audience(List.of(AUDIENCE))
                .voucherLifespan(60)
                .agreementApprovalPolicy(APPROVAL_POLICY);
        var preparedDescriptor = eServiceDescriptorUseCase.prepareDescriptorForPublication(
                eService, descriptor, command);
        eServiceDescriptorUseCase.linkOpenApiInterface(
                eService, preparedDescriptor, "assets/origin-interface.yaml");
        return eService;
    }


    private void assertHeaderAndBreadcrumbs(EServiceSummaryPage page) {
        // ES-SUMM-01 - Verify that the button "Torna agli e-service" is enabled
        assertThat(page.backToEServicesButton().isDisabled())
                .as("Torna agli e-service abilitato").isFalse();
        page.backToEServicesButton().readAndAssert(BTN_BACK_ESERVICE);

        // ES-SUMM-02...ES-SUMM-05 - Verify that the breadcrumbs are present and correct
        assertThat(page.erogazioneBreadcrumb().get().isPresent())
                .as("Breadcrumb Erogazione presente").isTrue();
        assertThat(page.erogazioneBreadcrumb().get().orElseThrow().getText())
                .as("Copy breadcrumb Erogazione").isEqualTo(BREADCRUMB_EROGAZIONE);
        assertThat(page.erogazioneBreadcrumb().getHref()).contains("/erogazione");

        assertThat(page.myEServicesBreadcrumb().get().isPresent())
                .as("Breadcrumb I miei e-service presente").isTrue();
        assertThat(page.myEServicesBreadcrumb().get().orElseThrow().getText())
                .as("Copy breadcrumb I miei e-service").isEqualTo(BREADCRUMB_MY_ESERVICES);
        assertThat(page.myEServicesBreadcrumb().getHref()).contains("/erogazione/e-service");

        assertThat(page.editEServiceBreadcrumb().get().isPresent())
                .as("Breadcrumb Modifica e-service presente").isTrue();
        assertThat(page.editEServiceBreadcrumb().get().orElseThrow().getText())
                .as("Copy breadcrumb Modifica e-service").isEqualTo(BREADCRUMB_EDIT_ESERVICE);
        assertThat(page.editEServiceBreadcrumb().getHref())
                .contains("/erogazione/e-service/", "/modifica");

        assertThat(page.currentBreadcrumb().get().isPresent())
                .as("Breadcrumb Riepilogo presente").isTrue();
        page.currentBreadcrumb().readAndAssert(BREADCRUMB_SUMMARY);

        // ES-SUMM-06 - Verify that the page title is present and populated
        assertThat(page.pageTitle().get().isPresent()).as("Titolo e-service presente").isTrue();
        assertThat(page.pageTitle().read()).as("Titolo e-service valorizzato").isNotBlank();

        // ES-SUMM-07 - Verify that the draft status is present and populated
        assertThat(page.statusChip().get().isPresent()).as("Stato bozza presente").isTrue();
        page.statusChip().readAndAssert(STATUS_DRAFT);
    }

    private void assertSummaryCards(EServiceSummaryPage page) {
        // ES-SUMM-08...ES-SUMM-11 - Verify that the cards are present, in the correct order and have the correct title and subtitle
        assertThat(page.summaryCards())
                .extracting(card -> card.header().title().read())
                .containsExactly(
                        CARD_GENERAL_INFO,
                        CARD_THRESHOLDS_ATTRIBUTES,
                        CARD_TECH_SPECS,
                        CARD_ADDITIONAL_INFO
                );
        assertThat(page.summaryCards())
                .extracting(card -> card.header().subtitle().read())
                .containsExactly(
                        "1","2","3","4"
                );
        //Verify that only the first card is initially expanded and the others are closed
        assertThat(page.summaryCards())
                .as("Stato iniziale delle card: solo Informazioni generali espansa")
                .extracting(card -> card.header().get()
                        .orElseThrow(() -> new AssertionError("Intestazione della card non presente"))
                        .getAttributes().get("aria-expanded"))
                .containsExactly("true", "false", "false", "false");
    }

    private void assertGeneralInfoCard(EServiceSummaryPage page) {
        //Verify that the General Info card has the correct content and values
        GeneralInfoCard card = page.generalInfoCard();
        var content = card.content();
        assertField(content.description(), GeneralInfoCard.DESCRIPTION, ESERVICE_DESCRIPTION);
        assertField(content.dataExchange(), GeneralInfoCard.DATA_EXCHANGE, ASYNC_EXCHANGE ? "Asincrono" : "Sincrono");
        assertField(content.apiTechnology(), GeneralInfoCard.API_TECHNOLOGY, ESERVICE_TECHNOLOGY.getValue());
        assertField(content.personalData(), GeneralInfoCard.PERSONAL_DATA, yesNo(PERSONAL_DATA));
        assertField(content.consumerDelegable(), GeneralInfoCard.CONSUMER_DELEGABLE, yesNo(CONSUMER_DELEGABLE));
        assertField(content.clientAccessDelegable(), GeneralInfoCard.CLIENT_ACCESS_DELEGABLE, yesNo(CLIENT_ACCESS_DELEGABLE));
        assertField(content.signalHub(), GeneralInfoCard.SIGNAL_HUB, "No, non è attivo");
    }

    private void assertThresholdsAttributesCard(EServiceSummaryPage page) {
        //Verify that the Thresholds and Attributes card has the correct content and values
        ThresholdsAttributesCard card = page.thresholdsAttributesCard();
        card.expand();
        assertThat(card.header().get()
                .orElseThrow(() -> new AssertionError("Intestazione Soglie e attributi non presente"))
                .getAttributes())
                .as("Card Soglie e attributi espansa dopo l'apertura")
                .containsEntry("aria-expanded", "true");

        var content = card.content();
        content.apiCallThresholdsTitle().readAndAssert(ThresholdsAttributesCard.API_CALL_THRESHOLDS);
        assertField(content.dailyCallsPerConsumer(), ThresholdsAttributesCard.PER_CONSUMER,
                DAILY_CALLS_PER_CONSUMER + " chiamate API/giorno");
        assertField(content.dailyCallsTotal(), ThresholdsAttributesCard.TOTAL,
                DAILY_CALLS_TOTAL + " chiamate API/giorno");
        assertAttributeSection(content.certifiedAttributes(), ThresholdsAttributesCard.CERTIFIED_ATTRIBUTES,
                "Sono attributi certificati da una fonte autoritativa riconosciuta.",
                "Questo e-service non richiede attributi certificati");
        assertAttributeSection(content.verifiedAttributes(), ThresholdsAttributesCard.VERIFIED_ATTRIBUTES,
                "Sono attributi verificati da altre organizzazioni, con la possibilità di richiedere una verifica dall’erogatore.",
                "Questo e-service non richiede attributi verificati");
        assertAttributeSection(content.declaredAttributes(), ThresholdsAttributesCard.DECLARED_ATTRIBUTES,
                "Sono attributi dichiarati dal fruitore stesso durante la richiesta di fruizione. Non è necessaria una verifica dell’erogatore.",
                "Questo e-service non richiede attributi dichiarati");
    }

    private void assertAttributeSection(AttributeSection section, String title, String description, String emptyMessage) {
        section.title().readAndAssert(title);
        section.description().readAndAssert(description);
        section.emptyStateMessage().readAndAssert(emptyMessage);
    }

    private void assertTechnicalSpecsCard(EServiceSummaryPage page, EService eService) {
        //Verify that the Technical Specs card has the correct content and values
        TechnicalSpecsCard card = page.technicalSpecsCard();
        card.expand();
        assertThat(card.header().get()
                .orElseThrow(() -> new AssertionError("Intestazione Specifiche tecniche non presente"))
                .getAttributes())
                .as("Card Specifiche tecniche espansa dopo l'apertura")
                .containsEntry("aria-expanded", "true");

        var content = card.content();
        content.apiInterface().label().readAndAssert(TechnicalSpecsCard.INTERFACE);
        content.apiInterface().attachmentButton().readAndAssert(
                "Specifica API_" + eService.getName() + "_Comune di Milano_v"
                        + eService.getLastDraftDescriptor().getVersion());
        assertThat(content.apiInterface().attachmentButton().isDisabled())
                .as("Pulsante interfaccia abilitato").isFalse();
        assertField(content.audience(), TechnicalSpecsCard.AUDIENCE, AUDIENCE);
        assertField(content.voucherLifespan(), TechnicalSpecsCard.VOUCHER_LIFESPAN, "1 minuto");
    }

    private void assertAdditionalInfoCard(EServiceSummaryPage page) {
        //Verify that the Additional Info card has the correct content and values
        AdditionalInfoCard card = page.additionalInfoCard();
        card.expand();
        assertThat(card.header().get()
                .orElseThrow(() -> new AssertionError("Intestazione Informazioni aggiuntive non presente"))
                .getAttributes())
                .as("Card Informazioni aggiuntive espansa dopo l'apertura")
                .containsEntry("aria-expanded", "true");

        var content = card.content();
        assertField(content.versionDescription(), AdditionalInfoCard.VERSION_DESCRIPTION, VERSION_DESCRIPTION);
        assertField(content.manualApproval(), AdditionalInfoCard.MANUAL_APPROVAL,
                yesNo(APPROVAL_POLICY == AgreementApprovalPolicy.MANUAL));
    }

    private void assertFooterActions(EServiceSummaryPage page) {
        // ES-SUMM-12...ES-SUMM-14 - Verify that the action buttons are present and enabled
        assertThat(page.deleteDraftButton().isDisabled()).as("Elimina bozza abilitato").isFalse();
        page.deleteDraftButton().readAndAssert(BTN_DELETE_DRAFT);

        assertThat(page.editDraftButton().isDisabled()).as("Modifica bozza abilitato").isFalse();
        page.editDraftButton().readAndAssert(BTN_EDIT_DRAFT);

        assertThat(page.publishButton().isDisabled()).as("Pubblica abilitato").isFalse();
        page.publishButton().readAndAssert(BTN_PUBLISH);
    }


    private static String yesNo(boolean value) {
        return value ? "Sì" : "No";
    }

    private void assertField(Field field, String expectedLabel, String expectedValue) {
        field.label().readAndAssert(expectedLabel);
        field.value().readAndAssert(expectedValue);
    }
}
