package it.pagopa.interop.suite.integration;

import it.pagopa.infrastructure.contract.browser.WebScenario;
import it.pagopa.infrastructure.channel.CurrentChannel;
import it.pagopa.domain.Identifiable;
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
import it.pagopa.interop.common.kernel.domain.Channel;
import it.pagopa.interop.common.kernel.domain.Tenant;
import it.pagopa.interop.common.kernel.domain.User;
import it.pagopa.interop.common.kernel.domain.UserRole;
import it.pagopa.interop.common.purpose.application.PurposeUseCase;
import it.pagopa.interop.common.purpose.application.PurposeGateway;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.web.infrastructure.config.WebJUnitSuitConfig;
import it.pagopa.interop.web.riskanalysis.infrastructure.suit.page.ReviewerRiskAnalysisPage;
import it.pagopa.utils.RandomUtils;
import it.pagopa.utils.async.PollingUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
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
import java.util.function.Consumer;
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
    private final PurposeGateway purposeGateway;
    private final AgreementGateway agreementGateway;
    private final EServiceGateway eServiceGateway;
    private final AuthSession authSession;
    private final CurrentChannel<Channel> currentChannel;
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

    @AfterEach
    void cleanUpFixtures() {
        if (ownedPurposes.isEmpty() && ownedAgreements.isEmpty() && ownedEServices.isEmpty()) return;
        IllegalStateException failure = new IllegalStateException("Risk-analysis fixture cleanup did not complete");
        Channel previousChannel = currentChannel.getCurrentChannel();
        try {
            currentChannel.setCurrentChannel(Channel.BFF);
            try (AuthSession.Snapshot ignored = authSession.snapshot()) {
                cleanUpOwned(ownedPurposes, consumer, purpose -> purposeGateway.deletePurpose(purpose.getRef()), failure);
                cleanUpOwned(ownedAgreements, consumer, agreementGateway::archiveAgreement, failure);
                cleanUpOwned(ownedEServices, producer,
                        eService -> eServiceGateway.archiveEService(eService.getRef(), GracePeriodDays.NUMBER_60), failure);
            }
        } catch (RuntimeException | AssertionError error) {
            failure.addSuppressed(new IllegalStateException("Fixture cleanup session failed", error));
        } finally {
            try {
                currentChannel.setCurrentChannel(previousChannel);
            } catch (RuntimeException | AssertionError error) {
                failure.addSuppressed(new IllegalStateException("Failed to restore fixture channel", error));
            }
        }
        if (failure.getSuppressed().length != 0) throw failure;
    }

    private <T extends Identifiable> void cleanUpOwned(
            List<T> fixtures, Tenant owner, Consumer<T> cleanup, IllegalStateException failure
    ) {
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
    Stream<DynamicTest> authorizedUserTests() {
        List<User> reviewers = User.getTenantUsers(consumer, UserRole.REVIEWER);
        assertThat(reviewers)
                .as("At least two reviewers must be configured for the consumer")
                .hasSizeGreaterThanOrEqualTo(2);

        EService olderEService = registerFixture(interopJourney.withConsumer(consumer, UserRole.ADMIN)
                .setUpRiskAnalysisToCompile(olderEServiceName, producer, consumer, reviewer)
                .get(EService.class));
        createAndSignRiskAnalysis(olderEService);

        EService newerEService = createToApproveRiskAnalysisWithTwoReviewers(reviewers, newerEServiceName);
        createAndRejectRiskAnalysis(newerEService);

        return webContractValidator.as(reviewer, consumer)
                .on(ReviewerRiskAnalysisPage.class)
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

    RiskAnalysisPageScan scanAssignments(ReviewerRiskAnalysisPage page, Purpose oldest, Purpose newest) {
        List<ReviewerRiskAnalysisPage.RiskAnalysisRow> rows = PollingUtils.pollUntil(
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
            Set<UUID> previousIds = rows.stream().map(ReviewerRiskAnalysisPage.RiskAnalysisRow::purposeId)
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

    private void assertCompletedAssignment(ReviewerRiskAnalysisPage.RiskAnalysisRow row, String status) {
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

    private void verifyCompletedTab(ReviewerRiskAnalysisPage page) {
        PollingUtils.pollUntil(
                () -> page.completedTab().isSelected() && !page.todoTab().isSelected(),
                Boolean.TRUE::equals,
                "The 'Concluse' tab was not selected after clicking it"
        );

        List<ReviewerRiskAnalysisPage.RiskAnalysisRow> firstPageRows =
                page.readRecordsEserviceStatus();

        verifyStatusAndReviewerName(olderEServiceName, firstPageRows, "approvata");
        verifyStatusAndReviewerName(newerEServiceName, firstPageRows, "rifiutata");

        assertOlderAnalysisAppearsAfterNewer(
                firstPageRows,
                olderEServiceName,
                newerEServiceName
        );
    }

    private void verifyTodoTab(ReviewerRiskAnalysisPage page) {
        assertThat(page.todoTab().isSelected())
                .as("The 'Da fare' tab must be selected")
                .isTrue();

        List<ReviewerRiskAnalysisPage.RiskAnalysisRow> firstPageRows =
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
            List<ReviewerRiskAnalysisPage.RiskAnalysisRow> rows,
            int expectedReviewerCount,
            String expectedStatus
    ) {
        ReviewerRiskAnalysisPage.RiskAnalysisRow row = findRowForEService(rows, eServiceName);

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
            List<ReviewerRiskAnalysisPage.RiskAnalysisRow> rows,
            String expectedStatus
    ) {
        ReviewerRiskAnalysisPage.RiskAnalysisRow row = findRowForEService(rows, eServiceName);

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

    /**
     * Finds the row of an e-service in the currently displayed table.
     *
     * @param rows         table rows read from the active tab
     * @param eServiceName name of the e-service to find
     * @return the matching row
     * @throws AssertionError if the e-service is absent
     */
    private ReviewerRiskAnalysisPage.RiskAnalysisRow findRowForEService(
            List<ReviewerRiskAnalysisPage.RiskAnalysisRow> rows,
            String eServiceName
    ) {
        return rows.stream()
                .filter(r -> eServiceName.equals(r.eserviceName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "E-service not found on the current page: " + eServiceName
                                + ". Found: " + rows.stream().map(ReviewerRiskAnalysisPage.RiskAnalysisRow::eserviceName).toList()
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

    /**
     * Checks newest-first ordering for two analyses expected to be present in the
     * supplied rows. The caller verifies their presence through their statuses.
     *
     * @param rows              table rows read from the relevant tab
     * @param olderEServiceName e-service name for the older analysis
     * @param newerEServiceName e-service name for the newer analysis
     */
    private static void assertOlderAnalysisAppearsAfterNewer(
            List<ReviewerRiskAnalysisPage.RiskAnalysisRow> rows,
            String olderEServiceName,
            String newerEServiceName
    ) {
        List<String> eserviceNames = rows.stream()
                .map(ReviewerRiskAnalysisPage.RiskAnalysisRow::eserviceName)
                .toList();
        assertThat(eserviceNames.indexOf(olderEServiceName))
                .as("The older RA for e-service '%s' must appear after the newer RA for e-service '%s' in descending date order",
                        olderEServiceName,
                        newerEServiceName)
                .isGreaterThan(eserviceNames.indexOf(newerEServiceName));
    }

    static final class RiskAnalysisPageScan {
        private final List<ReviewerRiskAnalysisPage.RiskAnalysisRow> rows = new ArrayList<>();
        private final Map<UUID, Integer> positions = new HashMap<>();
        private final Map<UUID, Integer> pageNumbers = new HashMap<>();
        private int pages;

        void addPage(List<ReviewerRiskAnalysisPage.RiskAnalysisRow> page) {
            if (page.isEmpty()) {
                throw new IllegalStateException("Inconclusive pagination scan: empty page");
            }
            for (ReviewerRiskAnalysisPage.RiskAnalysisRow row : page) {
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

        ReviewerRiskAnalysisPage.RiskAnalysisRow find(UUID purposeId) {
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