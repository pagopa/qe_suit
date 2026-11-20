package it.pagopa.interop.suite.integration;

import it.frontend.e2e.framework.core.capability.core.Gettable.FindPolicy;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.web.eservice.infrastructure.page.ConsumerPurposeSummaryPage;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
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
        classes = {TestBootApp.class, JunitContextConfig.class, WebJUnitSuitConfig.class},
        properties = "spring.profiles.include=junit"
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Consumer Purpose Summary Web Integration Tests")
public class WebIntegrationConsumerPurposeSummaryTest {
    private static final String ARIA_EXPANDED = "aria-expanded";
    private static final String REVIEWER_COMPILATION_MODE = "Compilazione e approvazione del valutatore";
    private static final String ADMIN_COMPILATION_MODE = "Compilazione in autonomia e approvazione del valutatore";
    private static final String TO_COMPILE = "In attesa di compilazione";
    private static final String TO_APPROVE = "In attesa di approvazione";
    private static final String UNAUTHORIZED_TITLE = "Non autorizzato";
    private static final String UNAUTHORIZED_SUBTITLE =
            "Non possiedi le autorizzazioni necessarie per visualizzare la pagina.";

    private final WebBrowserContractValidator webContractValidator;
    private final InteropJourney interopJourney;
    private final PurposeUseCase purposeUseCase;
    private final Tenant consumer = Tenant.PAGO_PA;
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;

    @TestFactory
    @DisplayName("Consumer sees exact reviewer assignments and updated review mode")
    Stream<DynamicTest> authorizedUserTests() {
        List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);
        assertThat(reviewers).as("At least three consumer reviewers must be configured")
                .hasSizeGreaterThanOrEqualTo(3);
        List<User> initial = List.of(reviewers.get(0));
        List<User> expanded = List.of(reviewers.get(0), reviewers.get(1));
        List<User> replacement = List.of(reviewers.get(1), reviewers.get(2));
        Purpose purpose = createToCompileRiskAnalysis(initial.get(0));
        String purposeId = purpose.getId().toString();

        return webContractValidator.as(User.getTenantAdmin(consumer), consumer)
                .on(ConsumerPurposeSummaryPage.class, purposeId)
                .tests(Stream.of(new WebScenario<>(
                        "Reviewer addition, removal and replacement with simultaneous review-mode change",
                        page -> {
                        },
                        page -> {
                            verifySummaryState(page, initial, REVIEWER_COMPILATION_MODE, TO_COMPILE);

                            updateAssignment(purpose, expanded, RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS, false);
                            reload(page, purposeId);
                            verifySummaryState(page, expanded, REVIEWER_COMPILATION_MODE, TO_COMPILE);

                            updateAssignment(purpose, initial, RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS, false);
                            reload(page, purposeId);
                            verifySummaryState(page, initial, REVIEWER_COMPILATION_MODE, TO_COMPILE);

                            updateAssignment(purpose, replacement, RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS, true);
                            reload(page, purposeId);
                            verifySummaryState(page, replacement, ADMIN_COMPILATION_MODE, TO_APPROVE);
                        }
                )));
    }

    @TestFactory
    @DisplayName("Assigned reviewer cannot access the consumer purpose summary")
    Stream<DynamicTest> unauthorizedUserTests() {
        User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);
        Purpose purpose = createToCompileRiskAnalysis(reviewer);
        return webContractValidator.as(reviewer, consumer)
                .on(ConsumerPurposeSummaryPage.class, purpose.getId().toString())
                .tests(Stream.of(new WebScenario<>(
                        "Reviewer sees unauthorized view instead of consumer summary",
                        page -> {
                        },
                        page -> {
                            page.unauthorizedTitle().readAndAssert(UNAUTHORIZED_TITLE);
                            page.unauthorizedSubtitle().readAndAssert(UNAUTHORIZED_SUBTITLE);
                            assertThat(page.backToHomeBtn().get(FindPolicy.VISIBLE))
                                    .as("Back-to-home button should be visible").isPresent();
                            page.backToHomeBtn().readAndAssert("Torna alla home");
                            assertThat(page.backToHomeBtn().isDisabled())
                                    .as("Back-to-home button should be enabled").isFalse();
                            assertThat(page.generalInfoCard().get(FindPolicy.VISIBLE))
                                    .as("Purpose general information must not be shown").isNotPresent();
                            assertThat(page.assignmentCard().get(FindPolicy.VISIBLE))
                                    .as("Assignment card must not be shown").isNotPresent();
                            assertThat(page.riskAnalysisCard().get(FindPolicy.VISIBLE))
                                    .as("Risk-analysis card must not be shown").isNotPresent();
                        }
                )));
    }

    private Purpose createToCompileRiskAnalysis(User reviewer) {
        return interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(
                        RandomUtils.randomAlphanumericName("consumer-purpose-summary"),
                        producer, consumer, reviewer)
                .get(Purpose.class);
    }

    private void updateAssignment(
            Purpose purpose, List<User> reviewers, RiskAnalysisReviewMode mode, boolean submit
    ) {
        interopJourney.withConsumer(consumer, UserRole.ADMIN);
        purposeUseCase.assignRiskAnalysisReviewers(
                purpose, mode, reviewers.stream().map(User::getUserId).toList());
        if (submit) {
            purposeUseCase.submitRiskAnalysis(purpose);
        }
    }

    private static void reload(ConsumerPurposeSummaryPage page, String purposeId) {
        page.navigateTo(purposeId);
        page.assertLoaded();
    }

    private void verifySummaryState(
            ConsumerPurposeSummaryPage page, List<User> expectedReviewers, String expectedMode, String expectedStatus
    ) {
        page.pageTitle().readAndAssert("Riepilogo");
        var card = page.assignmentCard();
        expand(card.header());
        card.modeLabel().readAndAssert("Modalità");
        card.modeValue().readAndAssert(expectedMode);
        card.assignedToLabel().readAndAssert(expectedReviewers.size() == 1 ? "Valutatore" : "Valutatori");
        String assignedTo = card.assignedToValue().read();
        assertThat(assignedTo).as("Assigned reviewer names must not be blank").isNotBlank();
        assertThat(assignedTo.split("\\s*,\\s*"))
                .as("Only the expected reviewers should appear, without duplicates or stale assignments")
                .containsExactlyInAnyOrderElementsOf(expectedReviewers.stream().map(this::reviewerDisplayName).toList());
        page.riskAnalysisCard().statusChip().text().readAndAssert(expectedStatus);
        assertThat(page.publishButton().isDisabled())
                .as("Publication must remain disabled until reviewer approval").isTrue();
    }

    private static void expand(Button header) {
        String expanded = header.get(FindPolicy.PRESENT)
                .orElseThrow(() -> new IllegalStateException("Assignment accordion header is absent"))
                .getAttributes().get(ARIA_EXPANDED);
        assertThat(expanded).as("Accordion must expose a valid expanded state").isIn("true", "false");
        if ("false".equals(expanded)) {
            header.click();
        }
        PollingUtils.pollUntil(
                () -> header.get(FindPolicy.PRESENT)
                        .orElseThrow(() -> new IllegalStateException("Assignment accordion header disappeared"))
                        .getAttributes().get(ARIA_EXPANDED),
                "true"::equals,
                "Assignment accordion did not expand"
        );
    }

    private String reviewerDisplayName(User reviewer) {
        return switch (reviewer) {
            case F_ALONSO -> "Fernando Alonso";
            case L_STROLL -> "Lance Stroll";
            case SILVERIO49 -> "Ione Coppola";
            default -> throw new IllegalArgumentException("No display name mapping configured for reviewer " + reviewer);
        };
    }
}
