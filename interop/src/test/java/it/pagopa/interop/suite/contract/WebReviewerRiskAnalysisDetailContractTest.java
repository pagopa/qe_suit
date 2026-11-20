package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Gettable;
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
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component.APIEstimateCard;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component.PurposeRiskAnalysisGeneralInfoCard;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.component.RiskAnalysisCard;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.suit.page.RiskAnalysisDetailPage;
import it.pagopa.utils.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
                TestBootApp.class,
                JunitContextConfig.class,
                WebJUnitSuitConfig.class
        },
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Risk Analysis Detail Web Contract Tests")
public class WebReviewerRiskAnalysisDetailContractTest {

    private static final String RISK_ANALYSIS_REJECTION_REASON = "Risk analysis rejected by contract test";

    private final InteropJourney interopJourney;
    private final WebBrowserContractValidator webContractValidator;

    private final Tenant consumer = Tenant.SOGECAP;
    private final List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);
    private final User reviewer = reviewers.get(0);
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;

    private static final Iterable<String> EXPECTED_BREADCRUMB_LABELS = List.of("Analisi del rischio", "Finalità");
    private static final String EXPECTED_RA_BREADCRUMB_HREF = "/ui/it/analisi-del-rischio";


    private static final String STATUS_REJECTED = "rifiutata";
    private static final String STATUS_APPROVED = "approvata";

    private static final String DRAWER_TITLE = "Rifiuto analisi del rischio";

    private static final String EXPECTED_SECTION_TITLE = "Analisi del rischio";
    private static final String EXPECTED_PERSONAL_DATA_TREATMENT = "Trattamento dati personali";

    private static final String LABEL_ESERVICE = "E-service da associare";
    private static final String LABEL_PROVIDER = "Erogatore";
    private static final String LABEL_PURPOSE = "Nome della finalità";
    private static final String LABEL_DESC = "Descrizione finalità";
    private static final String LABEL_ACCESS = "Come metterai a disposizione l'accesso ai dati di questo e-service?";
    private static final String LABEL_FREE_REASON = "Motivazione titolo gratuito";

    private static final String LABEL_API_ESTIMATE = "Stima numero chiamate API/giorno";

    private static final String LABEL_ASSIGNMENT_DATE = "Data assegnazione";
    private static final String LABEL_ASSIGNED_TO = "Assegnata a";
    private static final String LABEL_APPROVED_BY = "Approvata da";
    private static final String LABEL_APPROVED_DATE = "Data approvazione";
    private static final String LABEL_Q_PURPOSE = "Indicare per quale finalità si intende accedere ai dati messi a disposizione con la fruizione del presente E-service";
    private static final String LABEL_SPECIFIC_PURPOSE = "Specificare il fine perseguito per fini istituzionali che non richiedano prestazioni di elaborazioni aggiuntive";
    private static final String LABEL_PERSONAL_DATA = "Indicare se si accede a dati personali";
    private static final String LABEL_THIRD_PARTY_DATA = "Indicare se si accede a dati non personali di terzi (quindi a dati non personali che non sono riferibili al Fruitore stesso)";

    /**
     * Prepares an analysis awaiting compilation and checks its rendered content
     * without starting compilation.
     *
     * @return dynamic UI contract tests for the assigned reviewer
     */
    @TestFactory
    Stream<DynamicTest> testVerifyApprovedRiskAnalysisDetailPageContent() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_approved");

        Purpose purpose = createAndSignRiskAnalysis(reviewers, eServiceName);
        String approvedRiskAnalysisId = purpose.getId().toString();

        return webContractValidator
                .as(reviewer, Tenant.SOGECAP)
                .on(RiskAnalysisDetailPage.class, approvedRiskAnalysisId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Verify the correct rendering of the Purpose Information UI by a REVIEWER",
                                page -> {
                                },
                                page -> {
                                    page.pageTitle().readAndAssert(purpose.getTitle());
                                    verifyHeaderAndBreadcrumb(page);
                                    verifyChip(page, STATUS_APPROVED);
                                    verifyTabs(page);

                                    verifyGeneralInfoCard(page.generalInfoCard());
                                    verifyApiEstimateCard(page.apiEstimateCard());
                                    verifyExtendedAssignmentCard(page.assignmentCard());

                                    page.riskAnalysisTab().click();
                                    page.riskAnalysisInfoCard().subtitle().readAndAssert("Non puoi più modificare l’analisi del rischio perché è stata approvata.");
                                    verifyRiskAnalysisTab(page);

                                }
                        )
                ));
    }

    @TestFactory
    Stream<DynamicTest> testVerifyRejectedRiskAnalysisDetailPageContent() {
        String eServiceRejectedName = RandomUtils.randomAlphanumericName("e-service_RA_rejected");

        Purpose rejectedPurpose = createAndRejectRiskAnalysis(reviewers, eServiceRejectedName);
        String rejectedRiskAnalysisId = rejectedPurpose.getId().toString();


        return webContractValidator
                .as(reviewer, Tenant.SOGECAP)
                .on(RiskAnalysisDetailPage.class, rejectedRiskAnalysisId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Verify the correct rendering of the Purpose Information UI by a REVIEWER",
                                page -> {
                                },
                                page -> {
                                    page.pageTitle().readAndAssert(rejectedPurpose.getTitle());
                                    verifyHeaderAndBreadcrumb(page);
                                    verifyChip(page, STATUS_REJECTED);

                                    verifyAlertAndDrawer(page);

                                    verifyTabs(page);

                                    verifyGeneralInfoCard(page.generalInfoCard());
                                    verifyApiEstimateCard(page.apiEstimateCard());
                                    verifyAssignmentCard(page.assignmentCard());

                                    page.riskAnalysisTab().click();
                                    verifyRiskAnalysisTab(page);

                                }
                        )
                ));
    }

    private void verifyAlertAndDrawer(RiskAnalysisDetailPage page) {
        assertThat(page.rejectionAlert().get().isPresent())
                .as("Rejection alert should be present").isTrue();
        page.rejectionAlert().message().readAndAssert("L’analisi del rischio di questa finalità è stata rifiutata.");

        assertThat(page.readRejectionReasonButton().isDisabled())
                .as("Read reason button should be enabled").isFalse();
        page.readRejectionReasonButton().click();

        assertThat(page.rejectionReasonDrawer().get().isPresent())
                .as("Drawer should be opened").isTrue();
        page.rejectionReasonDrawer().drawerTitle().readAndAssert(DRAWER_TITLE);
        page.rejectionReasonDrawer().drawerDescription().readAndAssert("Un valutatore ha rifiutato l’analisi del rischio con questa motivazione.");

        assertNotBlank(page.rejectionReasonDrawer().rejectionReason().read(), "The rejection reason text must be present");

        page.rejectionReasonDrawer().closeButton().click();
    }

    private static void verifyTabs(RiskAnalysisDetailPage page) {
        assertThat(page.tabs().get().isPresent())
                .as("The Tabs component must be present").isTrue();
        assertThat(page.purposeDetailsTab().isDisabled())
                .as("The 'Dettagli finalità' tab must be enabled").isFalse();
        assertThat(page.riskAnalysisTab().isDisabled())
                .as("The 'Analisi del rischio' tab must be enabled").isFalse();
    }

    private void verifyRiskAnalysisTab(RiskAnalysisDetailPage page) {
        assertThat(page.riskAnalysisInfoCard().get().isPresent())
                .as("The risk analysis info card must be present").isTrue();
        page.riskAnalysisInfoCard().sectionTitle().readAndAssert(EXPECTED_SECTION_TITLE);
        page.riskAnalysisInfoCard().personalDataTreatment().readAndAssert(EXPECTED_PERSONAL_DATA_TREATMENT);
        assertNotBlank(page.riskAnalysisInfoCard().personalDataTreatmentValue().read(), EXPECTED_PERSONAL_DATA_TREATMENT);

        RiskAnalysisCard card = page.riskAnalysisCard();
        assertThat(card.get().isPresent())
                .as("The risk analysis card must be present").isTrue();

        card.purposeQuestionLabel().readAndAssert(LABEL_Q_PURPOSE);
        assertNotBlank(card.purposeAnswerLabel().read(), "Purpose Answer");

        card.specificPurposeQuestionLabel().readAndAssert(LABEL_SPECIFIC_PURPOSE);
        assertNotBlank(card.specificPurposeAnswerLabel().read(), "Purpose Answer");
        assertNotBlank(card.purposeNoteLabel().read(), "Purpose Note");

        card.personalDataQuestionLabel().readAndAssert(LABEL_PERSONAL_DATA);
        assertNotBlank(card.personalDataAnswerLabel().read(), "Personal Data Answer");
        assertNotBlank(card.personalDataNoteLabel().read(), "Personal Data Note");

        card.thirdPartyDataQuestionLabel().readAndAssert(LABEL_THIRD_PARTY_DATA);
        assertNotBlank(card.thirdPartyDataAnswerLabel().read(), "Third Party Data Answer");
    }

    private void verifyHeaderAndBreadcrumb(RiskAnalysisDetailPage page) {
        assertThat(page.backButton().isDisabled())
                .as("Back button must be enabled").isFalse();

        assertThat(page.breadcrumbs().get().isPresent())
                .as("Breadcrumbs must be present on the page")
                .isTrue();

        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMB_LABELS);

        assertThat(page.breadcrumbs().links()).hasSize(1);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Breadcrumb href")
                .isEqualTo(EXPECTED_RA_BREADCRUMB_HREF);
    }

    private void verifyChip(RiskAnalysisDetailPage page, String expectedStatus) {
        page.statusChip().readAndAssert(expectedStatus);
    }

    private void verifyGeneralInfoCard(PurposeRiskAnalysisGeneralInfoCard card) {
        card.title().readAndAssert("Informazioni generali");

        card.eserviceLabel().readAndAssert(LABEL_ESERVICE);
        assertNotBlank(card.eserviceValue().read(), LABEL_ESERVICE);
        assertNotBlank(card.eserviceValue().getHref(), "Href e-service");
        assertThat(card.eserviceValue().get(Gettable.FindPolicy.CLICKABLE))
                .as("E-service link must be visible and clickable")
                .isPresent();

        card.providerLabel().readAndAssert(LABEL_PROVIDER);
        assertNotBlank(card.providerValue().read(), LABEL_PROVIDER);

        card.purposeNameLabel().readAndAssert(LABEL_PURPOSE);
        assertNotBlank(card.purposeNameValue().read(), LABEL_PURPOSE);

        card.purposeDescLabel().readAndAssert(LABEL_DESC);
        assertNotBlank(card.purposeDescValue().read(), LABEL_DESC);

        card.accessLabel().readAndAssert(LABEL_ACCESS);
        assertNotBlank(card.accessValue().read(), LABEL_ACCESS);

        card.freeReasonLabel().readAndAssert(LABEL_FREE_REASON);
        assertNotBlank(card.freeReasonValue().read(), LABEL_FREE_REASON);
    }

    private void verifyApiEstimateCard(APIEstimateCard card) {
        card.title().readAndAssert("Stima chiamate API");

        card.apiEstimateLabel().readAndAssert(LABEL_API_ESTIMATE);
        assertNotBlank(card.apiEstimateValue().read(), LABEL_API_ESTIMATE);
    }

    private void verifyExtendedAssignmentCard(RiskAnalysisDetailPage.AssignmentExtendedCard card) {
        assertTitleAndDateForAssignmentCard(card);

        card.approvedDateLabel().readAndAssert(LABEL_APPROVED_DATE);
        assertNotBlank(card.approvedDateValue().read(), LABEL_APPROVED_DATE);

        card.approvedByLabel().readAndAssert(LABEL_APPROVED_BY);
        assertNotBlank(card.approvedByValue().read(), LABEL_APPROVED_BY);
    }

    private static void assertTitleAndDateForAssignmentCard(RiskAnalysisDetailPage.AssignmentExtendedCard card) {
        card.title().readAndAssert("Assegnazione");

        card.assignmentDateLabel().readAndAssert(LABEL_ASSIGNMENT_DATE);
        assertNotBlank(card.assignmentDateValue().read(), LABEL_ASSIGNMENT_DATE);
    }

    private void verifyAssignmentCard(RiskAnalysisDetailPage.AssignmentExtendedCard card) {
        assertTitleAndDateForAssignmentCard(card);

        card.assignedToLabel().readAndAssert(LABEL_ASSIGNED_TO);
        assertNotBlank(card.assignedToValue().read(), LABEL_ASSIGNED_TO);
    }


    private static void assertNotBlank(String value, String description) {
        assertThat(value).as("%s deve essere valorizzato", description).isNotBlank();
    }

    private Purpose createAndSignRiskAnalysis(List<User> reviewers, String eServiceName) {
        return interopJourney.withConsumer(consumer, UserRole.ADMIN).setUpRiskAnalysisToApprove(
                eServiceName,
                producer,
                consumer,
                reviewers
        ).withConsumer(consumer, reviewer).signRiskAnalysis().get(Purpose.class);
    }

    private Purpose createAndRejectRiskAnalysis(List<User> reviewers, String eServiceName) {
        return interopJourney.withConsumer(consumer, UserRole.ADMIN).setUpRiskAnalysisToApprove(
                eServiceName,
                producer,
                consumer,
                reviewers
        ).withConsumer(consumer, reviewer).rejectRiskAnalysis(RISK_ANALYSIS_REJECTION_REASON).get(Purpose.class);
    }
}
