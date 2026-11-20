package it.pagopa.interop.suite.contract;

import it.frontend.e2e.framework.core.capability.core.Gettable;
import it.frontend.e2e.framework.core.capability.core.Readable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.domain.AgreementState;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerPurposeSummaryPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RiskAnalysisCompleteCard;
import it.pagopa.interop.web.riskanalysis.infrastructure.component.RiskAnalysisGeneralInfoCard;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
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
public class WebConsumerPurposeSummaryContractTest {

    private static final String EXPECTED_BACK_LINK = "Torna alle finalità";
    private static final List<String> EXPECTED_BREADCRUMBS =
            List.of("Fruizione", "Finalità inoltrate", "Crea finalità");
    private static final String EXPECTED_CONSUMER_HREF = "/ui/it/fruizione";
    private static final String EXPECTED_PURPOSES_HREF = "/ui/it/fruizione/finalita";

    private static final String EXPECTED_PAGE_TITLE = "Riepilogo";
    private static final String EXPECTED_STATUS_CHIP = "in bozza";

    private static final String INFO_PURPOSE_NAME = "Nome della finalità";
    private static final String INFO_PROVIDER = "Erogatore";
    private static final String INFO_ESTIMATES_TITLE = "Stima e soglie di chiamate API";

    private static final String EXPECTED_ASSIGNMENT_STEP = "2";
    private static final String EXPECTED_ASSIGNMENT_TITLE = "Assegnazione";
    private static final String EXPECTED_MODE_LABEL = "Modalità";

    private static final String EXPECTED_RISK_TITLE = "Analisi del rischio";
    private static final String Q_PURPOSE = "Indicare per quale finalità si intende accedere ai dati messi a disposizione con la fruizione del presente E-service";
    private static final String Q_SPECIFIC_PURPOSE = "Specificare il fine perseguito per fini istituzionali che non richiedano prestazioni di elaborazioni aggiuntive";
    private static final String Q_PERSONAL_DATA = "Indicare se si accede a dati personali";
    private static final String Q_THIRD_PARTY_DATA = "Indicare se si accede a dati non personali di terzi (quindi a dati non personali che non sono riferibili al Fruitore stesso)";

    private static final String EXPECTED_DELETE_DRAFT_BUTTON = "Elimina bozza";
    private static final String EXPECTED_EDIT_DRAFT_BUTTON = "Modifica bozza";
    private static final String EXPECTED_PUBLISH_BUTTON = "Pubblica";

    private static final String ARIA_EXPANDED = "aria-expanded";
    private static final String EXPANDED_TRUE = "true";
    private static final String EXPANDED_FALSE = "false";

    private static final String EXPECTED_TOCOMPILE_ALERT_TEXT = "Quando il valutatore avrà compilato e approvato l’analisi del rischio, potrai pubblicare la finalità." ;
    private static final String EXPECTED_TOAPPROVE_ALERT_TEXT = "Quando il valutatore avrà approvato l’analisi del rischio, potrai pubblicare la finalità." ;


    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;

    @TestFactory
    Stream<DynamicTest> verifyConsumerPurposeSummaryPageContractForToCompileRAWithReviewer() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_to_compile");
        User consumer = User.getTenantAdmin(Tenant.PAGO_PA);
        String riskAnalysisId = interopJourney
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(eServiceName, Tenant.COMUNE_DI_MILANO, Tenant.PAGO_PA, User.getTenantUser(Tenant.PAGO_PA, UserRole.REVIEWER))
                .get(Purpose.class).getId().toString();

        return webContractValidator
                .as(consumer, Tenant.PAGO_PA)
                .on(ConsumerPurposeSummaryPage.class, riskAnalysisId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Test the correct rendering of Purpose Summary page for a Risk Analysis to be compiled with multiple reviewers",
                                page -> {
                                },
                                page -> {
                                    assertHeaderAndNavigation(page);
                                    verifyAlert(page, EXPECTED_TOCOMPILE_ALERT_TEXT);
                                    assertGeneralInfoSection(page.generalInfoCard());
                                    assertAssignmentSection(page.assignmentCard());
                                    page.assignmentCard().assignedToLabel().readAndAssert("Valutatore");

                                    assertToCompileRiskAnalysisSection(page.riskAnalysisCard());
                                    assertFooterActions(page);
                                    assertThat(page.publishButton().isDisabled())
                                            .as("Publish button should be disabled pending reviewer approval")
                                            .isTrue();
                                })
                ));
    }

    @TestFactory
    Stream<DynamicTest> verifyConsumerPurposeSummaryPageContractForToApproveRAWithReviewers() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_to_approve");
        User consumer = User.getTenantAdmin(Tenant.PAGO_PA);
        String riskAnalysisId = interopJourney
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(eServiceName, Tenant.COMUNE_DI_MILANO, Tenant.PAGO_PA, User.getTenantUsers(Tenant.PAGO_PA, UserRole.REVIEWER))
                .get(Purpose.class).getId().toString();

        return webContractValidator
                .as(consumer, Tenant.PAGO_PA)
                .on(ConsumerPurposeSummaryPage.class, riskAnalysisId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Test the correct rendering of Purpose Summary page for a ready to be approved Risk Analysis with multiple reviewers",
                                page -> {
                                },
                                page -> {
                                    assertHeaderAndNavigation(page);
                                    verifyAlert(page, EXPECTED_TOAPPROVE_ALERT_TEXT);
                                    assertGeneralInfoSection(page.generalInfoCard());
                                    assertAssignmentSection(page.assignmentCard());
                                    page.assignmentCard().assignedToLabel().readAndAssert("Valutatori");
                                    assertRiskAnalysisSection(page.riskAnalysisCard());
                                    assertThat(page.riskAnalysisCard().statusChip().get(Gettable.FindPolicy.PRESENT).isPresent())
                                            .as("Status chip should be present").isTrue();
                                    page.riskAnalysisCard().statusChip().text().readAndAssert("In attesa di approvazione");

                                    assertFooterActions(page);
                                    assertThat(page.publishButton().isDisabled())
                                            .as("Publish button should be disabled pending reviewer approval")
                                            .isTrue();
                                })
                ));
    }

    @TestFactory
    Stream<DynamicTest> verifyConsumerPurposeSummaryPageContractForApprovedRAWithReviewers() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_approved");
        User consumer = User.getTenantAdmin(Tenant.PAGO_PA);
        List<User> reviewers = User.getTenantUsers(Tenant.PAGO_PA, UserRole.REVIEWER);
        String riskAnalysisId = interopJourney
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(
                        eServiceName, Tenant.COMUNE_DI_MILANO, Tenant.PAGO_PA, reviewers)
                .withConsumer(Tenant.PAGO_PA, reviewers.get(0))
                .signRiskAnalysis()
                .get(Purpose.class).getId().toString();

        return webContractValidator
                .as(consumer, Tenant.PAGO_PA)
                .on(ConsumerPurposeSummaryPage.class, riskAnalysisId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Test the correct rendering of Purpose Summary page for an approved Risk Analysis with multiple reviewers",
                                page -> {
                                },
                                page -> {
                                    assertHeaderAndNavigation(page);
                                    assertThat(page.pageAlert().get(Gettable.FindPolicy.PRESENT).isPresent())
                                            .as("Alert should not be present").isFalse();

                                    assertGeneralInfoSection(page.generalInfoCard());
                                    assertAssignmentSection(page.assignmentCard());
                                    page.assignmentCard().assignedToLabel().readAndAssert("Valutatori");

                                    assertRiskAnalysisSection(page.riskAnalysisCard());
                                    assertThat(page.riskAnalysisCard().statusChip().get(Gettable.FindPolicy.PRESENT).isPresent())
                                            .as("Status chip should be present").isTrue();
                                    page.riskAnalysisCard().statusChip().text().readAndAssert("Approvata");


                                    assertFooterActions(page);
                                    assertThat(page.publishButton().isDisabled())
                                            .as("Publish button should be enabled")
                                            .isFalse();
                                })
                ));
    }

    @TestFactory
    Stream<DynamicTest> verifyConsumerPurposeSummaryPageContractForRejectedRA() {
        User reviewer = User.getTenantUser(Tenant.PAGO_PA, UserRole.REVIEWER);
        String rejectionReason = "Risk analysis rejected by consumer purpose summary contract test";
        String purposeId = interopJourney
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(
                        RandomUtils.randomAlphanumericName("e-service_RA_rejected"),
                        Tenant.COMUNE_DI_MILANO, Tenant.PAGO_PA, reviewer)
                .withConsumer(Tenant.PAGO_PA, reviewer)
                .rejectRiskAnalysis(rejectionReason)
                .get(Purpose.class).getId().toString();

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA)
                .on(ConsumerPurposeSummaryPage.class, purposeId)
                .tests(Stream.of(new WebScenario<>(
                        "Rejected risk analysis with rejection reason drawer and disabled publication",
                        page -> {
                        },
                        page -> {
                            assertHeaderAndNavigation(page);
                            assertGeneralInfoSection(page.generalInfoCard());
                            assertAssignmentSection(page.assignmentCard());
                            page.assignmentCard().assignedToLabel().readAndAssert("Valutatore");
                            assertRiskAnalysisSection(page.riskAnalysisCard());
                            page.riskAnalysisCard().statusChip().text().readAndAssert("Rifiutata");

                            assertRejectionAlertAndDrawer(page, rejectionReason);

                            assertFooterActions(page);
                            assertThat(page.publishButton().isDisabled())
                                    .as("Publish button should be disabled after rejection").isTrue();
                        }
                )));
    }

    @TestFactory
    Stream<DynamicTest> verifyConsumerPurposeSummaryPageContractForApprovalToRequestRA() {
        User reviewer = User.getTenantUser(Tenant.PAGO_PA, UserRole.REVIEWER);
        Purpose purpose = interopJourney
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(
                        RandomUtils.randomAlphanumericName("e-service_RA_approval_to_request"),
                        Tenant.COMUNE_DI_MILANO, Tenant.PAGO_PA, reviewer)
                .assignRiskAnalysisReviewers(
                        RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS, List.of(reviewer.getUserId()))
                .get(Purpose.class);

        return webContractValidator
                .as(User.getTenantAdmin(Tenant.PAGO_PA), Tenant.PAGO_PA)
                .on(ConsumerPurposeSummaryPage.class, purpose.getId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Risk analysis awaiting approval request with disabled publication",
                        page -> {
                        },
                        page -> {
                            assertHeaderAndNavigation(page);
                            assertGeneralInfoSection(page.generalInfoCard());
                            assertAssignmentSection(page.assignmentCard());
                            page.assignmentCard().assignedToLabel().readAndAssert("Valutatore");
                            page.assignmentCard().modeValue().readAndAssert(
                                    "Compilazione in autonomia e approvazione del valutatore");
                            assertThat(page.riskAnalysisCard().get(Gettable.FindPolicy.VISIBLE))
                                    .as("Risk-analysis card should be visible before requesting approval")
                                    .isPresent();
                            page.riskAnalysisCard().accordionTitle().readAndAssert(EXPECTED_RISK_TITLE);
                            page.riskAnalysisCard().statusChip().text().readAndAssert("Approvazione da richiedere");
                            assertFooterActions(page);
                            assertThat(page.publishButton().isDisabled())
                                    .as("Publish button should be disabled until approval is requested and granted")
                                    .isTrue();
                        }
                )));
    }

    @TestFactory
    Stream<DynamicTest> verifyConsumerPurposeSummaryPageContractForAdminWritesAdminSigns() {
        String eServiceName = RandomUtils.randomAlphanumericName("e-service_RA_admin");
        User consumer = User.getTenantAdmin(Tenant.PAGO_PA);
        String purposeId = interopJourney
                .withConsumer(Tenant.COMUNE_DI_MILANO, UserRole.ADMIN)
                .createEService(command -> command.name(eServiceName), EServiceDescriptorState.PUBLISHED)
                .withConsumer(Tenant.PAGO_PA, UserRole.ADMIN)
                .linkAgreement(AgreementState.ACTIVE)
                .createPurpose(PurposeVersionState.DRAFT)
                .get(Purpose.class).getId().toString();

        return webContractValidator
                .as(consumer, Tenant.PAGO_PA)
                .on(ConsumerPurposeSummaryPage.class, purposeId)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Purpose Summary for ADMIN_WRITES_ADMIN_SIGNS without reviewers",
                                page -> {
                                },
                                page -> {
                                    assertHeaderAndNavigation(page);
                                    assertGeneralInfoSection(page.generalInfoCard());
                                    assertAssignmentSection(page.assignmentCard(), false);
                                    assertRiskAnalysisSection(page.riskAnalysisCard());
                                    assertThat(page.riskAnalysisCard().statusChip().get(Gettable.FindPolicy.PRESENT))
                                            .as("Risk-analysis status chip should be absent without reviewers")
                                            .isNotPresent();
                                    assertFooterActions(page);
                                })
                ));
    }

    private static void assertRejectionAlertAndDrawer(ConsumerPurposeSummaryPage page, String rejectionReason) {
        assertThat(page.rejectionAlert().get(Gettable.FindPolicy.VISIBLE))
                .as("Rejection error alert should be visible").isPresent();
        page.rejectionAlert().message().readAndAssert(
                "Un valutatore ha rifiutato l’analisi del rischio. Leggi la motivazione, modifica l’analisi e inviala di nuovo in approvazione.");
        assertThat(page.readRejectionReasonButton().get(Gettable.FindPolicy.VISIBLE))
                .as("Read-reason button should be visible").isPresent();
        assertThat(page.readRejectionReasonButton().isDisabled())
                .as("Read-reason button should be enabled").isFalse();
        page.readRejectionReasonButton().readAndAssert("Leggi motivazione");
        page.readRejectionReasonButton().click();

        var drawer = page.rejectionReasonDrawer();
        assertThat(drawer.get(Gettable.FindPolicy.VISIBLE))
                .as("Rejection reason drawer should open").isPresent();
        drawer.drawerTitle().readAndAssert("Rifiuto analisi del rischio");
        drawer.drawerDescription().readAndAssert(
                "Un valutatore ha rifiutato l’analisi del rischio con questa motivazione.");
        drawer.rejectionReason().readAndAssert(rejectionReason);
        drawer.closeButton().click();
        PollingUtils.pollUntil(
                () -> !drawer.exists(Gettable.FindPolicy.VISIBLE),
                Boolean.TRUE::equals,
                "Rejection reason drawer did not close");
    }

    private void verifyAlert(ConsumerPurposeSummaryPage page, String expectedAlertText) {
        assertThat(page.pageAlert().get(Gettable.FindPolicy.PRESENT).isPresent())
                .as("Alert should be present").isTrue();
        page.pageAlert().message().readAndAssert(expectedAlertText);
    }

    private void assertHeaderAndNavigation(ConsumerPurposeSummaryPage page) {
        assertThat(page.backToPurposesButton().isDisabled())
                .as("Button enabled").isFalse();
        page.backToPurposesButton().readAndAssert(EXPECTED_BACK_LINK);

        assertThat(page.breadcrumbs().items().stream().map(Readable::read).toList())
                .as("Breadcrumb labels in order")
                .containsExactlyElementsOf(EXPECTED_BREADCRUMBS);
        assertThat(page.breadcrumbs().links()).hasSize(2);
        assertThat(page.breadcrumbs().links().get(0).getHref())
                .as("Breadcrumb href")
                .isEqualTo(EXPECTED_CONSUMER_HREF);
        assertThat(page.breadcrumbs().links().get(1).getHref())
                .as("Breadcrumb href")
                .isEqualTo(EXPECTED_PURPOSES_HREF);

        page.pageTitle().readAndAssert(EXPECTED_PAGE_TITLE);

        page.statusChip().text().readAndAssert(EXPECTED_STATUS_CHIP);

    }

    private void assertGeneralInfoSection(RiskAnalysisGeneralInfoCard card) {
        assertThat(card.get().isPresent())
                .as("First accordion card (General Info) should be present")
                .isTrue();

        assertThat(card.accordionHeader().get().orElseThrow().getAttributes())
                .as("General Info accordion should be expanded initially")
                .containsEntry(ARIA_EXPANDED, EXPANDED_TRUE);

        card.accordionTitle().readAndAssert("Informazioni generali");

        assertThat(card.get().isPresent()).as("Card Informazioni Generali not found").isTrue();

        card.purposeNameLabel().readAndAssert(INFO_PURPOSE_NAME);
        assertNotBlank(card.purposeNameValue().read(), INFO_PURPOSE_NAME);

        card.descriptionLabel().readAndAssert("Descrizione");
        assertNotBlank(card.descriptionValue().read(), "Descrizione");

        card.eServiceLabel().readAndAssert("E-service associato");
        assertNotBlank(card.eServiceLink().read(), "Nome e-service");
        assertNotBlank(card.eServiceLink().getHref(), "Href e-service");
        assertThat(card.eServiceLink().get(Gettable.FindPolicy.CLICKABLE))
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
    }

    private void assertAssignmentSection(ConsumerPurposeSummaryPage.PurposeAssignmentCard card) {
        assertAssignmentSection(card, true);
    }

    private void assertAssignmentSection(
            ConsumerPurposeSummaryPage.PurposeAssignmentCard card, boolean expectsReviewers
    ) {
        assertThat(card.header().get(Gettable.FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException(
                        "Header della card Assegnazione non trovato"))
                .getAttributes())
                .as("Assignment accordion should be collapsed initially")
                .containsEntry(ARIA_EXPANDED, EXPANDED_FALSE);


        if (card.header().get(Gettable.FindPolicy.PRESENT).isPresent()) {
            card.header().click();
            PollingUtils.pollUntil(
                    () -> card.header().get(Gettable.FindPolicy.PRESENT)
                            .orElseThrow(() -> new IllegalStateException(
                                    "Header della card Assegnazione non trovato"))
                            .getAttributes().get(ARIA_EXPANDED),
                    EXPANDED_TRUE::equals,
                    "La card Assegnazione non si è aperta"
            );
        }
        assertThat(card.get().isPresent())
                .as("Assignment accordion card should be present")
                .isTrue();
        assertThat(card.header().get().orElseThrow().getAttributes())
                .as("Card should be expanded opened after clicking the header")
                .containsEntry(ARIA_EXPANDED, EXPANDED_TRUE);

        card.stepNumber().readAndAssert(EXPECTED_ASSIGNMENT_STEP);
        card.title().readAndAssert(EXPECTED_ASSIGNMENT_TITLE);
        card.modeLabel().readAndAssert(EXPECTED_MODE_LABEL);

        assertNotBlank(card.modeValue().read(), "Mode value must be present and non-blank");
        if (expectsReviewers) {
            assertNotBlank(card.assignedToValue().read(), "Assigned to value must be present and non-blank");
        } else {
            assertThat(card.assignedToLabel().get(Gettable.FindPolicy.PRESENT))
                    .as("Reviewer label should be absent in ADMIN_WRITES_ADMIN_SIGNS mode")
                    .isNotPresent();
            assertThat(card.assignedToValue().get(Gettable.FindPolicy.PRESENT))
                    .as("Reviewer value should be absent in ADMIN_WRITES_ADMIN_SIGNS mode")
                    .isNotPresent();
        }

    }

    private void assertRiskAnalysisSection(RiskAnalysisCompleteCard card) {
        assertThat(card.header().get(Gettable.FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException(
                        "Header della card Analisi del Rischio non trovato"))
                .getAttributes())
                .as("Assignment accordion should be collapsed initially")
                .containsEntry(ARIA_EXPANDED, EXPANDED_FALSE);


        if (card.header().get(Gettable.FindPolicy.PRESENT).isPresent()) {
            card.header().click();
            PollingUtils.pollUntil(
                    () -> card.header().get(Gettable.FindPolicy.PRESENT)
                            .orElseThrow(() -> new IllegalStateException(
                                    "Header della card Analisi del Rischio non trovato"))
                            .getAttributes().get(ARIA_EXPANDED),
                    EXPANDED_TRUE::equals,
                    "La card Analisi del Rischio non si è aperta"
            );
        }

        card.accordionTitle().readAndAssert(EXPECTED_RISK_TITLE);

        assertThat(card.get().isPresent()).as("Card Analisi del Rischio not found").isTrue();
        card.title().readAndAssert(EXPECTED_RISK_TITLE);

        card.purposeQuestionLabel().readAndAssert(Q_PURPOSE);
        assertNotBlank(card.purposeAnswerLabel().read(), "Purpose Answer");

        card.specificPurposeQuestionLabel().readAndAssert(Q_SPECIFIC_PURPOSE);
        assertNotBlank(card.purposeAnswerLabel().read(), "Specific Purpose Answer");
        assertNotBlank(card.purposeNoteLabel().read(), "Specific Purpose Note");

        card.personalDataQuestionLabel().readAndAssert(Q_PERSONAL_DATA);
        assertNotBlank(card.personalDataAnswerLabel().read(), "Personal Data Answer");
        assertNotBlank(card.personalDataNoteLabel().read(), "Personal Data Note");

        card.thirdPartyDataQuestionLabel().readAndAssert(Q_THIRD_PARTY_DATA);
        assertNotBlank(card.thirdPartyDataAnswerLabel().read(), "Third Party Data Answer");
    }

    private void assertToCompileRiskAnalysisSection(RiskAnalysisCompleteCard card) {
        assertThat(card.header().get(Gettable.FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException(
                        "Header della card Analisi del Rischio non trovato"))
                .getAttributes())
                .as("Assignment accordion should be collapsed initially")
                .containsEntry(ARIA_EXPANDED, EXPANDED_FALSE);

        card.accordionTitle().readAndAssert(EXPECTED_RISK_TITLE);

        assertThat(card.get().isPresent()).as("Card Analisi del Rischio not found").isTrue();
       assertThat(card.statusChip().get(Gettable.FindPolicy.PRESENT).isPresent())
                .as("Status chip should be present").isTrue();
        card.statusChip().text().readAndAssert("In attesa di compilazione");
    }


    private void assertFooterActions(ConsumerPurposeSummaryPage page) {
        assertThat(page.deleteDraftButton().isDisabled())
                .as("Button enabled").isFalse();
        page.deleteDraftButton().readAndAssert(EXPECTED_DELETE_DRAFT_BUTTON);

        assertThat(page.editDraftButton().isDisabled())
                .as("Button enabled").isFalse();
        page.editDraftButton().readAndAssert(EXPECTED_EDIT_DRAFT_BUTTON);
        page.publishButton().readAndAssert(EXPECTED_PUBLISH_BUTTON);
    }

    private static void assertNotBlank(String value, String description) {
        assertThat(value).as("%s deve essere valorizzato", description).isNotBlank();
    }
}
