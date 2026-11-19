package it.pagopa.interop.suite.integration;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.interop.TestBootApp;
import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.infrastructure.WebBrowserContractValidator;
import it.pagopa.interop.common.infrastructure.config.JunitContextConfig;
import it.pagopa.interop.common.journey.application.InteropJourney;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.reviewer_riskanalysis.infrastructure.suit.page.RiskAnalysisPage;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
    private static final String RISK_ANALYSIS_REJECTION_REASON = "Risk analysis rejected by contract test";
    private static final String UNAUTHORIZED_TITLE = "Non autorizzato";
    private static final String UNAUTHORIZED_SUBTITLE = "Non possiedi le autorizzazioni necessarie per visualizzare la pagina.";

    private final WebBrowserContractValidator webContractValidator;

    private final InteropJourney interopJourney;
    private final PurposeUseCase purposeUseCase;
    private final Tenant consumer = Tenant.PAGO_PA;
    private final User reviewer = User.getTenantUser(consumer, UserRole.REVIEWER);
    private final Tenant producer = Tenant.COMUNE_DI_MILANO;

    private final String olderEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-older-compile-and-signed");
    private final String newerEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-approve-and-rejected");
    private final String intermediateEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-intermediate");
    private final String lastEServiceName = RandomUtils.randomAlphanumericName("risk-analysis-last-created");

    /**
     * Creates two e-services with two purposes each: compilation and signed for the
     * older service, approval and rejected for the newer service. Checks statuses,
     * reviewer counts and newest-first ordering in both tabs.
     *
     * @return dynamic integration tests for the reviewer's pending and completed analyses
     */
    @TestFactory
    @DisplayName("Reviewer views assigned RA")
    Stream<DynamicTest> authorizedUserTests() {
        List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);
        assertThat(reviewers)
                .as("At least two reviewers must be configured for the consumer")
                .hasSizeGreaterThanOrEqualTo(2);

        EService olderEService = interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(olderEServiceName, producer, consumer, reviewer)
                .get(EService.class);
        createAndSignRiskAnalysis(olderEService);

        EService newerEService = createToApproveRiskAnalysisWithTwoReviewers(reviewers, newerEServiceName);
        createAndRejectRiskAnalysis(newerEService);

        return webContractValidator.as(reviewer, consumer)
                .on(RiskAnalysisPage.class)
                .tests(Stream.of(
                        new WebScenario<>(
                                "Reviewer views assigned risk analyses",
                                page -> {
                                },
                                page -> {
                                    verifyTodoTab(page);

                                    page.completedTab().click();

                                    verifyCompletedTab(page);
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
    Stream<DynamicTest> moreThenOnePageRiskAnalysisTests() {
        PaginatedRiskAnalyses fixtures = createPaginatedRiskAnalyses();
        Purpose oldest = fixtures.pending().get(0);
        Purpose newest = fixtures.pending().get(fixtures.pending().size() - 1);
        Purpose oldestCompleted = fixtures.completed().get(0);
        Purpose newestCompleted = fixtures.completed().get(fixtures.completed().size() - 1);
        return webContractValidator.as(reviewer, consumer)
                .on(RiskAnalysisPage.class)
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
                                    RiskAnalysisPageScan pending = scanAssignments(page, oldest, newest);
                                    pending.assertOrder(oldest.getId(), newest.getId());

                                    page.completedTab().click();
                                    PollingUtils.pollUntil(
                                            () -> page.completedTab().isSelected() && !page.todoTab().isSelected(),
                                            Boolean.TRUE::equals,
                                            "The 'Concluse' tab was not selected"
                                    );
                                    RiskAnalysisPageScan completed = scanAssignments(page, oldestCompleted, newestCompleted);
                                    completed.assertOrder(oldestCompleted.getId(), newestCompleted.getId());
                                    assertCompletedAssignment(completed.find(oldestCompleted.getId()), "approvata");
                                    assertCompletedAssignment(completed.find(newestCompleted.getId()), "rifiutata");
                                }
                        )
                ));
    }


    record PaginatedRiskAnalyses(List<Purpose> pending, List<Purpose> completed) {}

    PaginatedRiskAnalyses createPaginatedRiskAnalyses() {
        List<Purpose> purposes = new ArrayList<>();
        EService oldestService = interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(olderEServiceName, producer, consumer, reviewer).get(EService.class);
        purposes.add(interopJourney.get(Purpose.class));

        EService intermediateService = interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(intermediateEServiceName, producer, consumer, reviewer).get(EService.class);
        purposes.add(interopJourney.get(Purpose.class));

        for (int i = 1; i < PAGE_SIZE - 1; i++) {
            createAdditionalRiskAnalysisToApprove(intermediateService);
            purposes.add(interopJourney.get(Purpose.class));
        }
        EService newestService = interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(lastEServiceName, producer, consumer, reviewer).get(EService.class);
        purposes.add(interopJourney.get(Purpose.class));

        List<Purpose> completed = new ArrayList<>();
        createAdditionalRiskAnalysisToApprove(oldestService);
        completed.add(interopJourney.get(Purpose.class));
        for (int i = 0; i < PAGE_SIZE - 1; i++) {
            createAdditionalRiskAnalysisToApprove(intermediateService);
            completed.add(interopJourney.get(Purpose.class));
        }
        createAdditionalRiskAnalysisToApprove(newestService);
        completed.add(interopJourney.get(Purpose.class));
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

    RiskAnalysisPageScan scanAssignments(RiskAnalysisPage page, Purpose oldest, Purpose newest) {
        List<RiskAnalysisPage.RiskAnalysisRow> rows = PollingUtils.pollUntil(
                page::readRecordsEserviceStatus,
                current -> current.size() == PAGE_SIZE && page.pagination().hasNext()
                        && !page.pagination().hasPrevious(),
                "The first assignments page did not load with " + PAGE_SIZE + " rows and a next page"
        );
        RiskAnalysisPageScan scan = new RiskAnalysisPageScan();
        int pageNumber = 1;
        int lastPage = page.pagination().lastPageNumber();
        while (true) {
            scan.addPage(rows);
            if (pageNumber >= 2 && scan.contains(oldest.getId()) && scan.contains(newest.getId())) {
                return scan;
            }
            if (pageNumber >= lastPage || !page.pagination().hasNext()) {
                throw new IllegalStateException("Inconclusive pagination scan: expected assignments were not found "
                        + "on at least two pages");
            }
            Set<UUID> previousIds = rows.stream().map(RiskAnalysisPage.RiskAnalysisRow::purposeId)
                    .collect(Collectors.toSet());
            int nextPage = ++pageNumber;
            page.pagination().goNext();
            rows = PollingUtils.pollUntil(
                    page::readRecordsEserviceStatus,
                    current -> !current.isEmpty()
                            && current.stream().noneMatch(row -> previousIds.contains(row.purposeId())),
                    "Inconclusive pagination scan: page " + nextPage
                            + " did not load distinct assignment rows"
            );
        }
    }

    private void assertCompletedAssignment(RiskAnalysisPage.RiskAnalysisRow row, String status) {
        assertThat(row.status()).isEqualToIgnoringCase(status);
        assertThat(row.reviewerOrTotReviewer()).isEqualTo(reviewerDisplayName(reviewer));
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
                .on(RiskAnalysisPage.class)
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

    private EService createToApproveRiskAnalysisWithTwoReviewers(List<User> reviewers, String eServiceName) {
        List<User> assignedReviewers = reviewers.subList(0, 2);

        return interopJourney
                .withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToApprove(
                        eServiceName,
                        producer,
                        consumer,
                        assignedReviewers
                )
                .get(EService.class);
    }

    private void verifyCompletedTab(RiskAnalysisPage page) {
        PollingUtils.pollUntil(
                () -> page.completedTab().isSelected() && !page.todoTab().isSelected(),
                Boolean.TRUE::equals,
                "The 'Concluse' tab was not selected after clicking it"
        );

        List<RiskAnalysisPage.RiskAnalysisRow> firstPageRows =
                page.readRecordsEserviceStatus();

        verifyStatusAndReviewerName(olderEServiceName, firstPageRows, "approvata");
        verifyStatusAndReviewerName(newerEServiceName, firstPageRows, "rifiutata");

        assertOlderAnalysisAppearsAfterNewer(
                firstPageRows,
                olderEServiceName,
                newerEServiceName
        );
    }

    private void verifyTodoTab(RiskAnalysisPage page) {
        assertThat(page.todoTab().isSelected())
                .as("The 'Da fare' tab must be selected")
                .isTrue();

        List<RiskAnalysisPage.RiskAnalysisRow> firstPageRows =
                page.readRecordsEserviceStatus();

        verifyStatusAndReviewerCount(olderEServiceName, firstPageRows, 1, "da compilare");
        verifyStatusAndReviewerCount(newerEServiceName, firstPageRows, 2, "da approvare");

        assertOlderAnalysisAppearsAfterNewer(
                firstPageRows,
                olderEServiceName,
                newerEServiceName
        );
    }

    private void verifyStatusAndReviewerCount(
            String eServiceName,
            List<RiskAnalysisPage.RiskAnalysisRow> rows,
            int expectedReviewerCount,
            String expectedStatus
    ) {
        RiskAnalysisPage.RiskAnalysisRow row = findRowForEService(rows, eServiceName);

        verifyStatus(row, expectedStatus);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer count for e-service '%s' must not be blank", eServiceName)
                .isNotBlank();
        assertThat(Integer.parseInt(row.reviewerOrTotReviewer()))
                .as("The reviewer count for e-service '%s' must equal %d", eServiceName, expectedReviewerCount)
                .isEqualTo(expectedReviewerCount);
    }

    private void verifyStatusAndReviewerName(
            String eServiceName,
            List<RiskAnalysisPage.RiskAnalysisRow> rows,
            String expectedStatus
    ) {
        RiskAnalysisPage.RiskAnalysisRow row = findRowForEService(rows, eServiceName);

        verifyStatus(row, expectedStatus);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer name for e-service '%s' must not be blank", eServiceName)
                .isNotBlank();

        String reviewerName = reviewerDisplayName(reviewer);
        assertThat(row.reviewerOrTotReviewer())
                .as("The reviewer name for e-service '%s' must equal '%s'", eServiceName, reviewerName)
                .isEqualTo(reviewerName);
    }

    private void verifyStatus(RiskAnalysisPage.RiskAnalysisRow row, String expectedStatus) {
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

    /**
     * Finds the row of an e-service in the currently displayed table.
     *
     * @param rows         table rows read from the active tab
     * @param eServiceName name of the e-service to find
     * @return the matching row
     * @throws AssertionError if the e-service is absent
     */
    private RiskAnalysisPage.RiskAnalysisRow findRowForEService(
            List<RiskAnalysisPage.RiskAnalysisRow> rows,
            String eServiceName
    ) {
        return rows.stream()
                .filter(r -> eServiceName.equals(r.eserviceName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "E-service not found on the current page: " + eServiceName
                                + ". Found: " + rows.stream().map(RiskAnalysisPage.RiskAnalysisRow::eserviceName).toList()
                ));
    }

    private void createAndSignRiskAnalysis(EService eService) {
        createAdditionalRiskAnalysisToApprove(eService);
        interopJourney
                .withConsumer(consumer, reviewer).signRiskAnalysis();
    }

    private void createAndRejectRiskAnalysis(EService eService) {
        createAdditionalRiskAnalysisToApprove(eService);
        interopJourney
                .withConsumer(consumer, reviewer).rejectRiskAnalysis(RISK_ANALYSIS_REJECTION_REASON);
    }

    private void createAdditionalRiskAnalysisToApprove(EService eService) {
        interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .createPurposeAndLinkToEService(eService, PurposeVersionState.DRAFT)
                .assignRiskAnalysisReviewers(
                        RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS,
                        List.of(reviewer.getUserId())
                )
                .submitRiskAnalysis();
    }

    /**
     * Checks newest-first ordering for two analyses expected to be present in the
     * supplied rows. The caller verifies their presence through their statuses.
     *
     * @param rows              table rows read from the relevant tab
     * @param olderEServiceName e-service name for the older analysis
     * @param newerEServiceName e-service name for the newer analysis
     */
    private static void assertOlderAnalysisAppearsAfterNewer(
            List<RiskAnalysisPage.RiskAnalysisRow> rows,
            String olderEServiceName,
            String newerEServiceName
    ) {
        List<String> eserviceNames = rows.stream()
                .map(RiskAnalysisPage.RiskAnalysisRow::eserviceName)
                .toList();
        assertThat(eserviceNames.indexOf(olderEServiceName))
                .as("The older RA for e-service '%s' must appear after the newer RA for e-service '%s' in descending date order",
                        olderEServiceName,
                        newerEServiceName)
                .isGreaterThan(eserviceNames.indexOf(newerEServiceName));
    }

    static final class RiskAnalysisPageScan {
        private final List<RiskAnalysisPage.RiskAnalysisRow> rows = new ArrayList<>();
        private final Map<UUID, Integer> positions = new HashMap<>();
        private final Map<UUID, Integer> pageNumbers = new HashMap<>();
        private int pages;

        void addPage(List<RiskAnalysisPage.RiskAnalysisRow> page) {
            if (page.isEmpty()) {
                throw new IllegalStateException("Inconclusive pagination scan: empty page");
            }
            for (RiskAnalysisPage.RiskAnalysisRow row : page) {
                if (positions.putIfAbsent(row.purposeId(), rows.size()) != null) {
                    throw new IllegalStateException("Inconclusive pagination scan: repeated purpose " + row.purposeId());
                }
                pageNumbers.put(row.purposeId(), pages + 1);
                rows.add(row);
            }
            pages++;
        }

        boolean contains(UUID purposeId) {
            return positions.containsKey(purposeId);
        }

        RiskAnalysisPage.RiskAnalysisRow find(UUID purposeId) {
            Integer position = positions.get(purposeId);
            if (position == null) {
                throw new IllegalStateException("Inconclusive pagination scan: purpose not found " + purposeId);
            }
            return rows.get(position);
        }

        void assertOrder(UUID oldest, UUID newest) {
            if (pages < 2 || !contains(oldest) || !contains(newest)) {
                throw new IllegalStateException("Inconclusive pagination scan: expected purposes not found on multiple pages");
            }
            assertThat(positions.get(newest))
                    .as("The newest risk analysis must precede the oldest")
                    .isLessThan(positions.get(oldest));
            assertThat(pageNumbers.get(newest))
                    .as("The newest risk analysis must appear on an earlier page than the oldest")
                    .isLessThan(pageNumbers.get(oldest));
        }
    }
}