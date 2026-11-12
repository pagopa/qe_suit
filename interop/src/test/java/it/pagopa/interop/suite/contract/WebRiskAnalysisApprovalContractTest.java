package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.RiskAnalysisApprovalPage;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.TestConstructor.AutowireMode.ALL;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = ALL)
@RequiredArgsConstructor
@DisplayName("Risk Analysis Approval Contract Tests")
class WebRiskAnalysisApprovalContractTest {

    private static final Iterable<String> EXPECTED_BREADCRUMB_LABELS = List.of("Analisi del rischio", "Approvazione analisi del rischio");
    private static final String EXPECTED_RA_BREADCRUMB_HREF = "/ui/it/analisi-del-rischio";

    private static final String TITLE = "Approvazione analisi del rischio";
    private static final String ALERT = "Quando approvi l’analisi del rischio, non potrai più modificarla e l’amministratore potrà pubblicare la finalità.";
    public static final String BTN_APPROVE = "Approva analisi del rischio";
    public static final String BTN_REJECT = "Rifiuta con motivazione";

    // Informazioni Generali
    private static final String INFO_PURPOSE_NAME = "Nome della finalità";
    private static final String INFO_PROVIDER = "Erogatore";
    private static final String INFO_ESTIMATES_TITLE = "Stima e soglie di chiamate API";
    private static final String INFO_ASSIGNMENT_TITLE = "Assegnazione";
    private static final String INFO_ASSIGNMENT_DATE = "Data assegnazione";
    private static final String INFO_ASSIGNED_TO = "Assegnata a";

    // Analisi del rischio
    private static final String RISK_TITLE = "Analisi del rischio";
    private static final String Q_PURPOSE = "Indicare per quale finalità si intende accedere ai dati messi a disposizione con la fruizione del presente E-service";
    private static final String Q_SPECIFIC_PURPOSE = "Specificare il fine perseguito per fini istituzionali che non richiedano prestazioni di elaborazioni aggiuntive";
    private static final String Q_PERSONAL_DATA = "Indicare se si accede a dati personali";
    private static final String Q_THIRD_PARTY_DATA = "Indicare se si accede a dati non personali di terzi (quindi a dati non personali che non sono riferibili al Fruitore stesso)";

    private final InteropJourney interopJourney;
    private final WebBrowserContractValidator webContractValidator;

    @TestFactory
    Stream<DynamicTest> verifyRiskAnalysisApprovalPageContent() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_to_approve");
        String riskAnalysisId = interopJourney
                .withConsumer(Tenant.SOGECAP, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(eServiceName, Tenant.COMUNE_DI_MILANO, Tenant.SOGECAP,
                        User.getTenantUser(Tenant.SOGECAP, UserRole.REVIEWER))
                .get(Purpose.class).getId().toString();

        return webContractValidator
                .as(User.getTenantUser(Tenant.SOGECAP, UserRole.REVIEWER), Tenant.SOGECAP)
                .on(RiskAnalysisApprovalPage.class, riskAnalysisId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Tests layout, breadcrumb, cards and buttons for RiskAnalysis Approval Page",
                                page -> {
                                },
                                page -> {
                                    assertHeaderAndBreadcrumbs(page);
                                    page.pageTitle().readAndAssert(TITLE);

                                    assertInformazioniGenerali(page.informazioniGeneraliCard());
                                    assertAnalisiRischio(page.analisiRischioCard());

                                    // Alert Message
                                    page.alertMessage().readAndAssert(ALERT);

                                    assertFooterAction(page);


                                }
                        )
                ));
    }

    private static void assertFooterAction(RiskAnalysisApprovalPage page) {
        // Action button
        assertThat(page.rejectButton().isDisabled()).as("Reject button must be enabled").isFalse();
        page.approveButton().readAndAssert(BTN_APPROVE);

        assertThat(page.approveButton().isDisabled()).as("Approve button must be enabled").isFalse();
        page.rejectButton().readAndAssert(BTN_REJECT);
    }

    private static void assertHeaderAndBreadcrumbs(RiskAnalysisApprovalPage page) {
        // Header e Navigazione
        assertThat(page.backToEServicesButton().isDisabled())
                .as("Button enabled").isFalse();
        page.backToEServicesButton().readAndAssert("Torna alle analisi del rischio");

        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);

        assertThat(page.breadcrumbs().links()).hasSize(1);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Breadcrumb href")
                .isEqualTo(EXPECTED_RA_BREADCRUMB_HREF);
    }

    private void assertInformazioniGenerali(RiskAnalysisApprovalPage.GeneralInfoCard card) {
        card.accordionTitle().readAndAssert("Informazioni generali");

        assertThat(card.get().isPresent()).as("Card Informazioni Generali not found").isTrue();

        card.purposeNameLabel().readAndAssert(INFO_PURPOSE_NAME);
        assertNotBlank(card.purposeNameValue().read(), INFO_PURPOSE_NAME);

        card.descriptionLabel().readAndAssert("Descrizione");
        assertNotBlank(card.descriptionValue().read(), "Descrizione");

        card.eServiceLabel().readAndAssert("E-service associato");
        assertNotBlank(card.eServiceLink().read(), "Nome e-service");
        assertNotBlank(card.eServiceLink().getHref(), "Href e-service");
        assertThat(card.eServiceLink().get(FindPolicy.CLICKABLE))
                .as("E-service link must be visible and clickable")
                .isPresent();

        card.providerLabel().readAndAssert(INFO_PROVIDER);
        assertNotBlank(card.providerValue().read(), INFO_PROVIDER);

        card.thresholdSubtitle().readAndAssert(INFO_ESTIMATES_TITLE);

        card.estimatedRequestLabel().readAndAssert("Stima richiesta dal fruitore");
        assertNotBlank(card.estimatedRequestValue().read(), "Stima richiesta dal fruitore");

        card.thresholdLabel().readAndAssert("Soglia per fruitore prevista dall'erogatore");
        assertNotBlank(card.thresholdValue().read(), "Soglia per fruitore");

        card.totalThresholdLabel().readAndAssert("Soglia totale prevista dall'erogatore");
        assertNotBlank(card.totalThresholdValue().read(), "Soglia totale");

        card.assignmentSubtitle().readAndAssert(INFO_ASSIGNMENT_TITLE);
        card.assignmentDateLabel().readAndAssert(INFO_ASSIGNMENT_DATE);
        assertNotBlank(card.assignmentDateValue().read(), INFO_ASSIGNMENT_DATE);

        card.assignedToLabel().readAndAssert(INFO_ASSIGNED_TO);
        assertNotBlank(card.assignedToValue().read(), INFO_ASSIGNED_TO);
    }

    private void assertAnalisiRischio(RiskAnalysisApprovalPage.RiskAnalysisCard card) {
        card.accordionTitle().readAndAssert(RISK_TITLE);

        assertThat(card.get().isPresent()).as("Card Analisi del Rischio not found").isTrue();
        card.title().readAndAssert(RISK_TITLE);

        card.purposeQuestionLabel().readAndAssert(Q_PURPOSE);
        assertNotBlank(card.purposeAnswerLabel().read(), "Purpose Answer");

        card.specificPurposeQuestionLabel().readAndAssert(Q_SPECIFIC_PURPOSE);
        assertNotBlank(card.specificPurposeAnswerLabel().read(), "Specific Purpose Answer");
        assertNotBlank(card.purposeNoteLabel().read(), "Purpose Note");

        card.personalDataQuestionLabel().readAndAssert(Q_PERSONAL_DATA);
        assertNotBlank(card.personalDataAnswerLabel().read(), "Personal Data Answer");
        assertNotBlank(card.personalDataNoteLabel().read(), "Personal Data Note");

        card.thirdPartyDataQuestionLabel().readAndAssert(Q_THIRD_PARTY_DATA);
        assertNotBlank(card.thirdPartyDataAnswerLabel().read(), "Third Party Data Answer");

    }


    private static void assertNotBlank(String value, String description) {
        assertThat(value).as("%s deve essere valorizzato", description).isNotBlank();
    }

}