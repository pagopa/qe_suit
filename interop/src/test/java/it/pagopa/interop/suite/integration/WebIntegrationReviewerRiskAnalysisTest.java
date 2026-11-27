package it.pagopa.interop.suite.integration;

import it.pagopa.domain.Identifiable;
import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.suit.component.Button;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.agreement.application.AgreementGateway;
import it.pagopa.interop.common.agreement.domain.Agreement;
import it.pagopa.interop.common.eservice.application.EServiceGateway;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.GracePeriodDays;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.context.AuthSession;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeGateway;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.ReviewerRiskAnalysisPage;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that backend risk-analysis assignments and review outcomes appear in
 * the reviewer workspace, and that an administrator cannot access that workspace.
 */
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
@DisplayName("Risk Analysis Web Integration Tests")
class WebIntegrationReviewerRiskAnalysisTest {

    static final int PAGE_SIZE = 10;
    private static final int SCAN_ATTEMPTS = 3;
    private static final String RISK_ANALYSIS_REJECTION_REASON = "Risk analysis rejected by contract test";
    private static final String UNAUTHORIZED_TITLE = "Non autorizzato";
    private static final String UNAUTHORIZED_SUBTITLE = "Non possiedi le autorizzazioni necessarie per visualizzare la pagina.";

    private final WebBrowserContractValidator webContractValidator;

    private final InteropJourney interopJourney;
    private final PurposeUseCase purposeUseCase;
    private final PurposeGateway purposeGateway;
    private final AgreementGateway agreementGateway;
    private final EServiceGateway eServiceGateway;
    private final AuthSession authSession;
    private final Tenant consumer = Tenant.PAGO_PA;
    private final User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;

    private final String olderEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-older-compile-and-signed");
    private final String newerEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-approve-and-rejected");
    private final String intermediateEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-intermediate");
    private final String lastEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-last-created");
    private final List<Purpose> ownedPurposes = new ArrayList<>();
    private final List<Agreement> ownedAgreements = new ArrayList<>();
    private final List<EService> ownedEServices = new ArrayList<>();

    /**
     * Cleans up all created purposes, agreements and e-services after each
     * test because of the number of the used resources.
     * If any cleanup fails, the test is marked as failed and the exception is
     * rethrown after attempting to clean up all remaining fixtures.
     */
    @AfterEach
    void cleanUpFixtures() {
        if (ownedPurposes.isEmpty() && ownedAgreements.isEmpty() && ownedEServices.isEmpty()) return;
        IllegalStateException failure = new IllegalStateException("Risk-analysis fixture cleanup did not complete");
        try {
            try (AuthSession.Snapshot ignored = authSession.snapshot()) {
                cleanUpOwned(ownedPurposes, consumer, purpose -> purposeGateway.deletePurpose(purpose.getRef()), failure);
                cleanUpOwned(ownedAgreements, consumer, agreementGateway::archiveAgreement, failure);
                cleanUpOwned(ownedEServices, producer, eService -> eServiceGateway.archiveEService(eService.getRef(), GracePeriodDays.NUMBER_60), failure);
            }
        } catch (RuntimeException | AssertionError error) {
            failure.addSuppressed(new IllegalStateException("Fixture cleanup session failed", error));
        }
        if (failure.getSuppressed().length != 0) throw failure;
    }

    private <T extends Identifiable> void cleanUpOwned(
            List<T> fixtures, Tenant owner, Consumer<T> cleanup, IllegalStateException failure) {
        for (int i = fixtures.size() - 1; i >= 0; i--) {
            T fixture = fixtures.get(i);
            try {
                authSession.set(User.getTenantAdmin(owner), owner);
                cleanup.accept(fixture);
            } catch (RuntimeException | AssertionError error) {
                failure.addSuppressed(new IllegalStateException(
                        "Failed to clean up " + fixture.getClass().getSimpleName() + " " + fixture.getId(), error));
            }
        }
    }

    private EService registerFixture(EService eService) {
        ownedEServices.add(eService);
        Agreement agreement = interopJourney.get(Agreement.class);
        if (!eService.getId().equals(agreement.getEserviceId())) {
            throw new IllegalStateException("Agreement does not belong to created e-service " + eService.getId());
        }
        ownedAgreements.add(agreement);
        Purpose purpose = interopJourney.get(Purpose.class);
        if (!eService.getId().equals(purpose.getEserviceId())) {
            throw new IllegalStateException("Purpose does not belong to created e-service " + eService.getId());
        }
        ownedPurposes.add(purpose);
        return eService;
    }

    /**
     * Creates two e-services with two purposes each: compilation and signed for the
     * older service, approval and rejected for the newer service. Checks statuses,
     * reviewer counts and newest-first ordering in both tabs.
     *
     * @return dynamic integration tests for the reviewer's pending and completed analyses
     */
    @TestFactory
    @DisplayName("Reviewer views assigned RA")
    @Tag("risk-analysis-sorting")
    Stream<DynamicTest> authorizedUserTests() {
        List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);
        assertThat(reviewers)
                .as("At least two reviewers must be configured for the consumer")
                .hasSizeGreaterThanOrEqualTo(2);

        EService olderEService = registerFixture(interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(olderEServiceName, producer, consumer, reviewer)
                .get(EService.class));
        Purpose olderPending = interopJourney.get(Purpose.class);
        Purpose olderCompleted = createAndSignRiskAnalysis(olderEService);

        EService newerEService = createToApproveRiskAnalysisWithTwoReviewers(reviewers, newerEServiceName);
        Purpose newerPending = interopJourney.get(Purpose.class);
        Purpose newerCompleted = createAndRejectRiskAnalysis(newerEService);

        return webContractValidator.as(reviewer, consumer)
                .on(ReviewerRiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Reviewer views assigned risk analyses",
                                page -> {
                                },
                                page -> {
                                    verifyTodoTab(page, olderPending, newerPending);
                                    page.completedTab().click();
                                    verifyCompletedTab(page, olderCompleted, newerCompleted);
                                }
                        )
                ));
    }


    /**
     * Checks that the producer's administrator sees the unauthorized view
     * instead of the reviewer workspace.
     *
     * @return dynamic integration tests for unauthorized access
     */
    @TestFactory
    @DisplayName("Unauthorized user sees the error page")
    Stream<DynamicTest> unauthorizedUserTests() {
        return webContractValidator.as(User.getTenantAdmin(producer), producer)
                .on(ReviewerRiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "An ADMIN user visualizes 'Unauthorized'",
                                page -> {
                                },
                                page -> {
                                    assertThat(page.tabs().get().isPresent()).isFalse();

                                    page.unauthorizedTitle().readAndAssert(UNAUTHORIZED_TITLE);
                                    page.unauthorizedSubtitle().readAndAssert(UNAUTHORIZED_SUBTITLE);
                                    assertThat(page.backToHomeBtn().isDisabled())
                                            .as("The 'Torna alla home' button must be enabled")
                                            .isFalse();
                                }
                        )
                ));
    }
    /**
     * Test implemented to verify resolution of PIN-11557 - QA-19703.
     * Creates a set of pending and completed risk analyses that span multiple pages.
     * Checks that the analyses are ordered correctly across pages in both tabs.
     *
     * @return dynamic integration tests for the reviewer's paginated analyses
     */
    @TestFactory
    @DisplayName("Reviewer sees risk analyses ordered across pages")
    @Tag("risk-analysis-sorting")
    Stream<DynamicTest> moreThenOnePageRiskAnalysisTests() {
        PaginatedRiskAnalyses fixtures = createPaginatedRiskAnalyses();
        Purpose oldest = fixtures.pending().get(0);
        Purpose newest = fixtures.pending().get(fixtures.pending().size() - 1);

        Purpose oldestCompleted = fixtures.completed().get(0);
        Purpose newestCompleted = fixtures.completed().get(fixtures.completed().size() - 1);

        return webContractValidator.as(reviewer, consumer)
                .on(ReviewerRiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Pending and completed risk analyses are ordered across pages",
                                page -> {},
                                page -> {

                                    PollingUtils.pollUntil(
                                            () -> page.todoTab().isSelected() && !page.completedTab().isSelected(),
                                            Boolean.TRUE::equals,
                                            "The 'Da fare' tab was not selected"
                                    );
                                    verifyOrderInTab(page, fixtures.pending(), oldest, newest);

                                    page.completedTab().click();
                                    PollingUtils.pollUntil(
                                            () -> page.completedTab().isSelected() && !page.todoTab().isSelected(),
                                            Boolean.TRUE::equals,
                                            "The 'Concluse' tab was not selected"
                                    );
                                    RiskAnalysisPageScan completed = verifyOrderInTab(page, fixtures.completed(), oldestCompleted, newestCompleted);
                                    assertCompletedAssignment(completed.find(oldestCompleted.getId()), "approvata");
                                    assertCompletedAssignment(completed.find(newestCompleted.getId()), "rifiutata");
                                }
                        )
                ));
    }


    private RiskAnalysisPageScan verifyOrderInTab(ReviewerRiskAnalysisPage page, List<Purpose> fixtures, Purpose oldest, Purpose newest) {
        RiskAnalysisPageScan scan = scanAssignments(page, fixtures);
        scan.assertOrder(oldest.getId(), newest.getId());
        scan.assertDifferentPages(oldest.getId(), newest.getId());
        return scan;
    }

    record PaginatedRiskAnalyses(List<Purpose> pending, List<Purpose> completed) {}

    PaginatedRiskAnalyses createPaginatedRiskAnalyses() {
        List<Purpose> purposes = new ArrayList<>();
        EService oldestService = registerFixture(interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(olderEServiceName, producer, consumer, reviewer).get(EService.class));
        purposes.add(interopJourney.get(Purpose.class));

        EService intermediateService = registerFixture(interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(intermediateEServiceName, producer, consumer, reviewer).get(EService.class));
        purposes.add(interopJourney.get(Purpose.class));

        for (int i = 1; i < PAGE_SIZE - 1; i++) {
            purposes.add(createAdditionalRiskAnalysisToApprove(intermediateService));
        }
        EService newestService = registerFixture(interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(lastEServiceName, producer, consumer, reviewer).get(EService.class));
        purposes.add(interopJourney.get(Purpose.class));

        List<Purpose> completed = new ArrayList<>();
        completed.add(createAdditionalRiskAnalysisToApprove(oldestService));
        for (int i = 0; i < PAGE_SIZE - 1; i++) {
            completed.add(createAdditionalRiskAnalysisToApprove(intermediateService));
        }
        completed.add(createAdditionalRiskAnalysisToApprove(newestService));
        completeRiskAnalyses(completed);
        return new PaginatedRiskAnalyses(List.copyOf(purposes), List.copyOf(completed));
    }

    void completeRiskAnalyses(List<Purpose> purposes) {
        interopJourney.withConsumer(consumer, reviewer);
        purposeUseCase.signRiskAnalysis(purposes.get(0));
        for (Purpose purpose : purposes.subList(1, purposes.size())) {
            purposeUseCase.rejectRiskAnalysis(purpose, RISK_ANALYSIS_REJECTION_REASON);
        }
    }

    RiskAnalysisPageScan scanAssignments(ReviewerRiskAnalysisPage page, List<Purpose> purposes) {
        Set<UUID> expectedIds = purposes.stream().map(Purpose::getId).collect(Collectors.toSet());
        List<UnstableScanException> failedAttempts = new ArrayList<>();
        for (int attempt = 1; attempt <= SCAN_ATTEMPTS; attempt++) {
            try {
                return scanUntilAllAssignmentsFound(page, expectedIds);
            } catch (UnstableScanException error) {
                failedAttempts.add(error);
            }
        }
        AssertionError failure = new AssertionError(
                "Pagination scan failed after " + SCAN_ATTEMPTS + " attempts: " + expectedIds);
        failedAttempts.forEach(failure::addSuppressed);
        throw failure;
    }

    private RiskAnalysisPageScan scanUntilAllAssignmentsFound(ReviewerRiskAnalysisPage page, Set<UUID> expectedIds) {
        awaitScan(page::readRecordsEserviceStatus, current -> !current.isEmpty(),
                "Assignments did not load in the selected tab");
        returnToFirstPage(page);
        List<ReviewerRiskAnalysisPage.RiskAnalysisRow> rows = awaitScan(
                page::readRecordsEserviceStatus, current -> !current.isEmpty(),
                "Assignments did not load on the first page");
        RiskAnalysisPageScan scan = new RiskAnalysisPageScan();
        int pageNumber = 1;
        int lastPage = lastPageNumber(page);
        while (true) {
            if (lastPageNumber(page) != lastPage) {
                throw new UnstableScanException("Page count changed during scan");
            }
            scan.addPage(rows);
            if (expectedIds.stream().allMatch(scan::contains)) {
                return scan;
            }
            if (pageNumber >= lastPage || !page.pagination().hasNext()) {
                throw new UnstableScanException("Expected assignments were not found: " + expectedIds);
            }
            page.pagination().goNext();
            pageNumber++;
            rows = awaitPage(page, pageNumber, rows);
        }
    }

    private int lastPageNumber(ReviewerRiskAnalysisPage page) {
        return page.hasPagination() ? page.pagination().lastPageNumber() : 1;
    }

    private void returnToFirstPage(ReviewerRiskAnalysisPage page) {
        int currentPage = selectedPageNumber(page);
        while (currentPage > 1) {
            List<ReviewerRiskAnalysisPage.RiskAnalysisRow> before = page.readRecordsEserviceStatus();
            page.pagination().goBack();
            currentPage--;
            awaitPage(page, currentPage, before);
        }
    }

    private int selectedPageNumber(ReviewerRiskAnalysisPage page) {
        if (!page.hasPagination()) return 1;
        return page.pagination().buttons().stream()
                .filter(Button::isSelected)
                .mapToInt(button -> Integer.parseInt(button.read().trim()))
                .findFirst().orElseThrow(() -> new UnstableScanException("No selected pagination page"));
    }

    private List<ReviewerRiskAnalysisPage.RiskAnalysisRow> awaitPage(ReviewerRiskAnalysisPage page, int number, List<ReviewerRiskAnalysisPage.RiskAnalysisRow> before) {
        return awaitScan(page::readRecordsEserviceStatus,
                rows -> selectedPageNumber(page) == number && !rows.isEmpty() && !rows.equals(before),
                "Assignments did not load on page " + number);
    }

    private static <T> T awaitScan(Supplier<T> read, Predicate<T> ready, String message) {
        try {
            return PollingUtils.pollUntil(read, ready, message);
        } catch (IllegalStateException error) {
            if (!(error.getCause() instanceof ConditionTimeoutException)) throw error;
            throw new UnstableScanException(message, error);
        }
    }

    private static final class UnstableScanException extends RuntimeException {
        UnstableScanException(String message) {
            super(message);
        }

        UnstableScanException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private void assertCompletedAssignment(ReviewerRiskAnalysisPage.RiskAnalysisRow row, String status) {
        assertThat(row.status()).isEqualToIgnoringCase(status);
        assertThat(row.reviewerOrTotReviewer()).isEqualTo(reviewerDisplayName(reviewer));
    }

    private EService createToApproveRiskAnalysisWithTwoReviewers(List<User> reviewers, String eServiceName) {
        List<User> assignedReviewers = reviewers.subList(0, 2);

        return registerFixture(interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(
                        eServiceName,
                        producer,
                        consumer,
                        assignedReviewers
                )
                .get(EService.class));
    }

    private void verifyCompletedTab(ReviewerRiskAnalysisPage page, Purpose older, Purpose newer) {
        PollingUtils.pollUntil(
                () -> page.completedTab().isSelected() && !page.todoTab().isSelected(),
                Boolean.TRUE::equals,
                "The 'Concluse' tab was not selected after clicking it"
        );

        RiskAnalysisPageScan scan = scanAssignments(page, List.of(older, newer));
        verifyStatusAndReviewerName(scan.find(older.getId()), "approvata");
        verifyStatusAndReviewerName(scan.find(newer.getId()), "rifiutata");
        scan.assertOrder(older.getId(), newer.getId());
    }

    private void verifyTodoTab(ReviewerRiskAnalysisPage page, Purpose older, Purpose newer) {
        assertThat(page.todoTab().isSelected())
                .as("The 'Da fare' tab must be selected")
                .isTrue();

        RiskAnalysisPageScan scan = scanAssignments(page, List.of(older, newer));
        verifyStatusAndReviewerCount(scan.find(older.getId()), 1, "da compilare");
        verifyStatusAndReviewerCount(scan.find(newer.getId()), 2, "da approvare");
        scan.assertOrder(older.getId(), newer.getId());
    }

    private void verifyStatusAndReviewerCount(
            ReviewerRiskAnalysisPage.RiskAnalysisRow row,
            int expectedReviewerCount,
            String expectedStatus
    ) {
        String eServiceName = row.eserviceName();

        verifyStatus(row, expectedStatus);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer count for e-service '%s' must not be blank", eServiceName)
                .isNotBlank();
        assertThat(Integer.parseInt(row.reviewerOrTotReviewer()))
                .as("The reviewer count for e-service '%s' must equal %d", eServiceName, expectedReviewerCount)
                .isEqualTo(expectedReviewerCount);
    }

    private void verifyStatusAndReviewerName(
            ReviewerRiskAnalysisPage.RiskAnalysisRow row,
            String expectedStatus
    ) {
        String eServiceName = row.eserviceName();

        verifyStatus(row, expectedStatus);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer name for e-service '%s' must not be blank", eServiceName)
                .isNotBlank();

        String reviewerName = reviewerDisplayName(reviewer);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer name for e-service '%s' must equal '%s'", eServiceName, reviewerName)
                .isEqualTo(reviewerName);
    }

    private void verifyStatus(ReviewerRiskAnalysisPage.RiskAnalysisRow row, String expectedStatus) {
        assertThat(row.status())
                .as("The risk analysis for e-service '%s' must have status '%s'", row.eserviceName(), expectedStatus)
                .isEqualToIgnoringCase(expectedStatus);
    }

    private String reviewerDisplayName(User reviewer) {
        return switch (reviewer) {
            case F_ALONSO -> "Fernando Alonso";
            case L_STROLL -> "Lance Stroll";
            case SILVERIO49 -> "Ione Coppola";
            default -> throw new IllegalArgumentException(
                    "No display name mapping configured for reviewer " + reviewer
            );
        };
    }

    private Purpose createAndSignRiskAnalysis(EService eService) {
        Purpose purpose = createAdditionalRiskAnalysisToApprove(eService);
        interopJourney
                .withConsumer(consumer, reviewer).signRiskAnalysis();
        return purpose;
    }

    private Purpose createAndRejectRiskAnalysis(EService eService) {
        Purpose purpose = createAdditionalRiskAnalysisToApprove(eService);
        interopJourney
                .withConsumer(consumer, reviewer).rejectRiskAnalysis(RISK_ANALYSIS_REJECTION_REASON);
        return purpose;
    }

    private Purpose createAdditionalRiskAnalysisToApprove(EService eService) {
        interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .createPurposeAndLinkToEService(eService, PurposeVersionState.DRAFT);
        Purpose purpose = interopJourney.get(Purpose.class);
        if (!eService.getId().equals(purpose.getEserviceId())) {
            throw new IllegalStateException("Purpose does not belong to created e-service " + eService.getId());
        }
        ownedPurposes.add(purpose);
        interopJourney.assignRiskAnalysisReviewers(
                        RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS,
                        List.of(reviewer.getUserId())
                )
                .submitRiskAnalysis();
        return purpose;
    }

    static final class RiskAnalysisPageScan {
        private final List<ReviewerRiskAnalysisPage.RiskAnalysisRow> rows = new ArrayList<>();
        private final Map<UUID, Integer> positions = new HashMap<>();
        private final Map<UUID, Integer> pageNumbers = new HashMap<>();
        private int pages;

        void addPage(List<ReviewerRiskAnalysisPage.RiskAnalysisRow> page) {
            if (page.isEmpty()) {
                throw new UnstableScanException("Empty page");
            }
            for (ReviewerRiskAnalysisPage.RiskAnalysisRow row : page) {
                if (positions.putIfAbsent(row.purposeId(), rows.size()) != null) {
                    throw new UnstableScanException("Repeated purpose " + row.purposeId());
                }
                pageNumbers.put(row.purposeId(), pages + 1);
                rows.add(row);
            }
            pages++;
        }

        boolean contains(UUID purposeId) {
            return positions.containsKey(purposeId);
        }

        ReviewerRiskAnalysisPage.RiskAnalysisRow find(UUID purposeId) {
            Integer position = positions.get(purposeId);
            if (position == null) {
                throw new IllegalStateException("Inconclusive pagination scan: purpose not found " + purposeId);
            }
            return rows.get(position);
        }

        void assertOrder(UUID oldest, UUID newest) {
            find(oldest);
            find(newest);
            assertThat(positions.get(newest))
                    .as("The newest risk analysis must precede the oldest")
                    .isLessThan(positions.get(oldest));
        }

        void assertDifferentPages(UUID oldest, UUID newest) {
            find(oldest);
            find(newest);
            assertThat(pageNumbers.get(newest))
                    .as("The newest risk analysis must appear on an earlier page than the oldest")
                    .isLessThan(pageNumbers.get(oldest));
        }
    }
}