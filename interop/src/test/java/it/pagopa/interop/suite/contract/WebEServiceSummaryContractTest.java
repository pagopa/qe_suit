package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.web.WebPresentationGateway;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.kernel.context.CurrentUserSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.web.eservice.application.WebEServiceGeneralData;
import it.pagopa.interop.web.eservice.application.WebEServiceTechnicalData;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceCreatePage;
import it.pagopa.interop.web.eservice.infrastructure.suit.page.EServiceSummaryPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.net.URI;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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

    private static final Pattern SUMMARY_PATH =
            Pattern.compile("/erogazione/e-service/([^/]+)/([^/]+)/modifica/riepilogo/?$");

    private final WebBrowserContractValidator webContractValidator;
    private final ObjectProvider<WebPresentationGateway> junitWebPresentationGateway;
    private final CurrentUserSession currentUserSession;

    /**
     * Creates one complete, unpublished draft through the wizard, then checks its
     * summary in a fresh authenticated browser for each read-only scenario.
     */
    @TestFactory
    Stream<DynamicTest> summaryPageContractTests() {
        String[] pathParams = prepareDraft();
        return webContractValidator
                .as(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO)
                .on(EServiceSummaryPage.class, pathParams)
                .tests(Stream.of(
                        new WebScenario<>("Verifica Top Bar, Breadcrumb e Intestazione",
                                page -> {},
                                page -> {
                                    assertHeaderAndBreadcrumbs(page);
                                    assertSummaryCards(page);
                                    assertFooterActions(page);
                                })
                        ));
    }

    private String[] prepareDraft() {
        currentUserSession.set(User.getTenantAdmin(Tenant.COMUNE_DI_MILANO), Tenant.COMUNE_DI_MILANO);
        WebPresentationGateway gateway = junitWebPresentationGateway.getObject();
        try {
            EServiceCreatePage page = gateway.bind(EServiceCreatePage.class);
            page.navigateTo();
            page.assertLoaded();

            var data = WebEServiceGeneralData.buildDefault().eservice();
            page.generalDataForm()
                    .setEServiceName(data.getName())
                    .setEServiceDescription(data.getDescription())
                    .setMode(data.getMode())
                    .setTechnology(data.getTechnology())
                    .setAsyncExchange(data.getAsyncExchange())
                    .setUsePersonalData(Boolean.TRUE.equals(data.getPersonalData()))
                    .saveDraft();

            var thresholds = page.thresholdAndAttributeForm();
            thresholds.assertLoaded();
            thresholds.apiCallThresholdSection().dailyCallsPerConsumerInput().fill("1");
            thresholds.apiCallThresholdSection().dailyCallsTotalInput().fill("2");
            thresholds.saveDraftButton().click();

            var technicalData = WebEServiceTechnicalData.buildDefault();
            var technicalSpecs = page.technicalSpecsForm();
            technicalSpecs.assertLoaded();
            technicalSpecs.uploadInterface(technicalData.interfaceAttachmentPath());
            technicalSpecs.audienceInput().fill(technicalData.aud());
            technicalSpecs.voucherLifespanInput().fill(technicalData.voucherLifespan());
            technicalSpecs.saveDraftButton().click();

            var additionalInfo = page.additionalInfoForm();
            additionalInfo.assertLoaded();
            additionalInfo.descriptionInput().fill("Versione per il test contrattuale del riepilogo");
            additionalInfo.goToSummaryButton().click();

            gateway.bind(EServiceSummaryPage.class).assertLoaded();
            var matcher = SUMMARY_PATH.matcher(URI.create(gateway.getLocation().getUrl()).getPath());
            if (!matcher.find()) {
                throw new IllegalStateException("Il wizard non ha raggiunto la pagina di riepilogo");
            }
            return new String[]{matcher.group(1), matcher.group(2)};
        } finally {
            gateway.close();
        }
    }

    private void assertHeaderAndBreadcrumbs(EServiceSummaryPage page) {
        // ES-SUMM-01 - Verifica che il pulsante "Torna agli e-service" sia abilitato
        assertThat(page.backToEServicesButton().isDisabled())
                .as("Torna agli e-service abilitato").isFalse();
        page.backToEServicesButton().readAndAssert(BTN_BACK_ESERVICE);

        // ES-SUMM-02...ES-SUMM-05 - Verifica che i breadcrumb siano presenti e corretti
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

        // ES-SUMM-06 - Verifica che il titolo della pagina sia presente e valorizzato
        assertThat(page.pageTitle().get().isPresent()).as("Titolo e-service presente").isTrue();
        assertThat(page.pageTitle().read()).as("Titolo e-service valorizzato").isNotBlank();

        // ES-SUMM-07 - Verifica che lo stato della bozza sia presente e valorizzato
        assertThat(page.statusChip().get().isPresent()).as("Stato bozza presente").isTrue();
        page.statusChip().readAndAssert(STATUS_DRAFT);
    }

    private void assertSummaryCards(EServiceSummaryPage page) {
        // ES-SUMM-08...ES-SUMM-11 - Verifica che le card siano presenti e valorizzate
        assertThat(page.generalInfoCardHeader().get().isPresent())
                .as("Card Informazioni generali presente").isTrue();
        page.generalInfoCardHeader().readAndAssert(CARD_GENERAL_INFO);

        assertThat(page.thresholdsAttributesCardHeader().get().isPresent())
                .as("Card Soglie e attributi presente").isTrue();
        page.thresholdsAttributesCardHeader().readAndAssert(CARD_THRESHOLDS_ATTRIBUTES);

        assertThat(page.techSpecsCardHeader().get().isPresent())
                .as("Card Specifiche tecniche presente").isTrue();
        page.techSpecsCardHeader().readAndAssert(CARD_TECH_SPECS);

        assertThat(page.additionalInfoCardHeader().get().isPresent())
                .as("Card Informazioni aggiuntive presente").isTrue();
        page.additionalInfoCardHeader().readAndAssert(CARD_ADDITIONAL_INFO);
    }

    private void assertFooterActions(EServiceSummaryPage page) {
        // ES-SUMM-12...ES-SUMM-14 - Verifica che i pulsanti di azione siano presenti e abilitati
        assertThat(page.deleteDraftButton().isDisabled()).as("Elimina bozza abilitato").isFalse();
        page.deleteDraftButton().readAndAssert(BTN_DELETE_DRAFT);

        assertThat(page.editDraftButton().isDisabled()).as("Modifica bozza abilitato").isFalse();
        page.editDraftButton().readAndAssert(BTN_EDIT_DRAFT);

        assertThat(page.publishButton().isDisabled()).as("Pubblica abilitato").isFalse();
        page.publishButton().readAndAssert(BTN_PUBLISH);
    }
}
