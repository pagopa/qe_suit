package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.infrastructure.response.ApiResponse;
import io.restassured.builder.ResponseBuilder;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisAssignmentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisRejectionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSignSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSubmissionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.Purpose;
import it.pagopa.interop.generated.openapi.clients.bff.model.Reviewer;
import it.pagopa.interop.generated.openapi.clients.bff.model.ReviewerWorkflow;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSigningState;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static it.pagopa.infrastructure.template.action.strategy.PollingStrategy.UNTIL_SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BffPurposeGatewayRiskAnalysisTest {

    private final BffPurposeRestClient restClient = mock(BffPurposeRestClient.class);
    private final BffPurposeCommandFactory commandFactory = mock(BffPurposeCommandFactory.class);
    private final BffPurposeGateway gateway = new BffPurposeGateway(
            restClient,
            mock(BffPurposeMapper.class),
            commandFactory
    );


    @Test
    void savesFormOnceAndWaitsForAllAnswersToBecomeVisible() {
        UUID purposeId = UUID.randomUUID();
        var expected = it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm.builder()
                .version("3.0")
                .answers(Map.of("institutionalPurpose", List.of("Updated"),
                        "usesPersonalData", List.of("NO")))
                .build();
        TestChain<Void> write = (TestChain<Void>) mock(TestChain.class, RETURNS_DEEP_STUBS);
        TestChain<Purpose> read =(TestChain<Purpose>) mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.saveRiskAnalysisForm(eq(purposeId), any())).thenReturn(write);
        when(write.withoutPolling().getRaw().isSuccess()).thenReturn(true);
        when(restClient.getPurpose(purposeId)).thenReturn(read);
        when(read.withoutPolling().getRaw().isSuccess()).thenReturn(true);
        when(read.withoutPolling().get()).thenReturn(
                new Purpose().riskAnalysisForm(new it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm()
                        .version("3.0").answers(Map.of("institutionalPurpose", List.of("Old")))),
                new Purpose().riskAnalysisForm(new it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisForm()
                        .version(expected.getVersion()).answers(expected.getAnswers()))
        );

        gateway.saveRiskAnalysisForm(PurposeRef.of(purposeId), expected);

        var payload = ArgumentCaptor.forClass(
                it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisFormSeed.class);
        verify(restClient).saveRiskAnalysisForm(eq(purposeId), payload.capture());
        assertThat(payload.getValue().getVersion()).isEqualTo(expected.getVersion());
        assertThat(payload.getValue().getAnswers()).isEqualTo(expected.getAnswers());
        verify(restClient, times(2)).getPurpose(purposeId);
        verify(write, never()).withPolling(any());
    }

    @Test
    void failsImmediatelyWhenFormSaveIsRejected() {
        UUID purposeId = UUID.randomUUID();
        TestChain<Void> write = (TestChain<Void>) mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.saveRiskAnalysisForm(eq(purposeId), any())).thenReturn(write);
        when(write.withoutPolling().getRaw().isSuccess()).thenReturn(false);
        var form = it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisForm.builder()
                .version("3.0").answers(Map.of()).build();

        assertThatThrownBy(() -> gateway.saveRiskAnalysisForm(PurposeRef.of(purposeId), form))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("save failed");

        verify(restClient, never()).getPurpose(any());
        verify(restClient).saveRiskAnalysisForm(eq(purposeId), any());
    }

    @Test
    void rejectsFailedOrMissingFormReads() {
        UUID purposeId = UUID.randomUUID();
        TestChain<Purpose> read = (TestChain<Purpose>) mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.getPurpose(purposeId)).thenReturn(read);
        when(read.withoutPolling().getRaw().isSuccess()).thenReturn(false, true);
        when(read.withoutPolling().get()).thenReturn(new Purpose());

        assertThatThrownBy(() -> gateway.getRiskAnalysisForm(PurposeRef.of(purposeId)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("read failed");
        assertThatThrownBy(() -> gateway.getRiskAnalysisForm(PurposeRef.of(purposeId)))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("incomplete or absent");
    }

    @ParameterizedTest
    @EnumSource(RiskAnalysisReviewMode.class)
    void assignsRiskAnalysisReviewersWithRequestedModeAndIds(RiskAnalysisReviewMode reviewMode) {
        UUID purposeId = UUID.randomUUID();
        List<UUID> reviewerIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        TestChain<Void> chain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.assignRiskAnalysisReviewers(eq(purposeId), any())).thenReturn(chain);
        TestChain<Purpose> readChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.getPurpose(purposeId)).thenReturn(readChain);
        when(readChain.withoutPolling().getRaw().isSuccess()).thenReturn(true);
        when(readChain.withoutPolling().get()).thenReturn(
                purposeWithReviewers(List.of(reviewerIds.get(1), reviewerIds.get(0)))
        );

        gateway.assignRiskAnalysisReviewers(PurposeRef.of(purposeId), reviewMode, reviewerIds);

        ArgumentCaptor<RiskAnalysisAssignmentSeed> request =
                ArgumentCaptor.forClass(RiskAnalysisAssignmentSeed.class);
        verify(restClient).assignRiskAnalysisReviewers(eq(purposeId), request.capture());
        assertThat(request.getValue().getReviewerIds()).containsExactlyElementsOf(reviewerIds);
        assertThat(request.getValue().getReviewMode()).isEqualTo(toBffReviewMode(reviewMode));
        verify(restClient).getPurpose(purposeId);
    }

    @Test
    void waitsForExactReviewerIdsWithoutRepeatingTheAssignment() {
        UUID purposeId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();
        UUID removedReviewerId = UUID.randomUUID();
        TestChain<Void> writeChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        TestChain<Purpose> readChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.assignRiskAnalysisReviewers(eq(purposeId), any())).thenReturn(writeChain);
        when(restClient.getPurpose(purposeId)).thenReturn(readChain);
        when(readChain.withoutPolling().getRaw().isSuccess()).thenReturn(true);
        when(readChain.withoutPolling().get()).thenReturn(
                purposeWithReviewers(List.of(reviewerId, removedReviewerId)),
                purposeWithReviewers(List.of(removedReviewerId)),
                purposeWithReviewers(List.of(reviewerId))
        );
        clearInvocations(readChain);

        gateway.assignRiskAnalysisReviewers(PurposeRef.of(purposeId),
                RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS, List.of(reviewerId));

        verify(restClient).assignRiskAnalysisReviewers(eq(purposeId), any());
        verify(writeChain, times(1)).withPolling(UNTIL_SUCCESS);
        verify(restClient, times(3)).getPurpose(purposeId);
        verify(readChain, times(3)).withoutPolling();
        verify(readChain, never()).withPolling(any());
    }

    @Test
    void waitsForTheWorkflowAndReviewerListToBecomeVisible() {
        UUID purposeId = UUID.randomUUID();
        UUID reviewerId = UUID.randomUUID();
        TestChain<Void> writeChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        TestChain<Purpose> readChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.assignRiskAnalysisReviewers(eq(purposeId), any())).thenReturn(writeChain);
        when(restClient.getPurpose(purposeId)).thenReturn(readChain);
        when(readChain.withoutPolling().getRaw().isSuccess()).thenReturn(true);
        when(readChain.withoutPolling().get()).thenReturn(
                new Purpose(),
                new Purpose().reviewerWorkflow(new ReviewerWorkflow().reviewers(null)),
                purposeWithReviewers(List.of(reviewerId))
        );

        gateway.assignRiskAnalysisReviewers(PurposeRef.of(purposeId),
                RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS, List.of(reviewerId));

        verify(restClient, times(3)).getPurpose(purposeId);
    }

    @Test
    void failsOnAnUnsuccessfulReadWithoutRetrying() {
        UUID purposeId = UUID.randomUUID();
        TestChain<Void> writeChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        TestChain<Purpose> readChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.assignRiskAnalysisReviewers(eq(purposeId), any())).thenReturn(writeChain);
        when(restClient.getPurpose(purposeId)).thenReturn(readChain);

        assertThatThrownBy(() -> gateway.assignRiskAnalysisReviewers(PurposeRef.of(purposeId),
                RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS, List.of(UUID.randomUUID())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Reviewer-assignment read failed");
        verify(restClient).getPurpose(purposeId);
    }

    @Test
    void propagatesReadFailuresAfterAssignment() {
        UUID purposeId = UUID.randomUUID();
        var failure = new IllegalStateException("Cannot read purpose");
        TestChain<Void> writeChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.assignRiskAnalysisReviewers(eq(purposeId), any())).thenReturn(writeChain);
        when(restClient.getPurpose(purposeId)).thenThrow(failure);

        assertThatThrownBy(() -> gateway.assignRiskAnalysisReviewers(PurposeRef.of(purposeId),
                RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS, List.of(UUID.randomUUID())))
                .isSameAs(failure);
        verify(restClient).assignRiskAnalysisReviewers(eq(purposeId), any());
    }

    private static Purpose purposeWithReviewers(List<UUID> reviewerIds) {
        return new Purpose().reviewerWorkflow(new ReviewerWorkflow().reviewers(
                reviewerIds.stream().map(id -> new Reviewer().userId(id)).toList()
        ));
    }

    @Test
    void submitsRiskAnalysisWithFactoryPayload() {
        UUID purposeId = UUID.randomUUID();
        RiskAnalysisSubmissionSeed payload = new RiskAnalysisSubmissionSeed();
        TestChain<Void> chain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(commandFactory.riskAnalysisSubmissionRequest()).thenReturn(payload);
        when(restClient.submitRiskAnalysis(purposeId, payload)).thenReturn(chain);

        gateway.submitRiskAnalysis(PurposeRef.of(purposeId));

        verify(commandFactory).riskAnalysisSubmissionRequest();
        verify(restClient).submitRiskAnalysis(purposeId, payload);
    }

    @Test
    void signsRiskAnalysisWithRequestedMetadataVersion() {
        UUID purposeId = UUID.randomUUID();
        TestChain<Void> chain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.signRiskAnalysis(eq(purposeId), any())).thenReturn(chain);
        TestChain<Purpose> readChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.getPurpose(purposeId)).thenReturn(readChain);
        when(readChain.withoutPolling().getRaw().isSuccess()).thenReturn(true);
        when(readChain.withoutPolling().get()).thenReturn(
                new Purpose(),
                new Purpose().reviewerWorkflow(new ReviewerWorkflow().signingState(RiskAnalysisSigningState.ASSIGNED)),
                new Purpose().reviewerWorkflow(new ReviewerWorkflow().signingState(RiskAnalysisSigningState.SIGNED))
        );

        gateway.signRiskAnalysis(PurposeRef.of(purposeId), 0);

        ArgumentCaptor<RiskAnalysisSignSeed> request = ArgumentCaptor.forClass(RiskAnalysisSignSeed.class);
        verify(restClient).signRiskAnalysis(eq(purposeId), request.capture());
        assertThat(request.getValue().getMetadataVersionToSign()).isZero();
        verify(restClient, times(3)).getPurpose(purposeId);
        verify(chain, times(1)).withPolling(UNTIL_SUCCESS);
    }

    @Test
    void signsUsingTheMetadataVersionFromTheResponseHeader() {
        UUID purposeId = UUID.randomUUID();
        PurposeRef ref = PurposeRef.of(purposeId);
        BffPurposeGateway signingGateway = spy(gateway);
        TestChain<Purpose> readChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.getPurpose(purposeId)).thenReturn(readChain);
        when(readChain.withoutPolling().getRaw()).thenReturn(new ApiResponse(
                new ResponseBuilder().setStatusCode(200).setBody("{}")
                        .setHeader("X-Metadata-Version", "7").build()
        ));
        doNothing().when(signingGateway).signRiskAnalysis(ref, 7);

        signingGateway.signRiskAnalysis(ref);

        verify(signingGateway).signRiskAnalysis(ref, 7);
        verify(restClient).getPurpose(purposeId);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "invalid", "2.5", "-1", "2147483648"})
    void rejectsMissingOrInvalidMetadataVersionWithoutSigning(String header) {
        UUID purposeId = UUID.randomUUID();
        TestChain<Purpose> readChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.getPurpose(purposeId)).thenReturn(readChain);
        var response = new ResponseBuilder().setStatusCode(200).setBody("{}");
        if (header != null) {
            response.setHeader("x-metadata-version", header);
        }
        when(readChain.withoutPolling().getRaw()).thenReturn(new ApiResponse(response.build()));

        assertThatThrownBy(() -> gateway.signRiskAnalysis(PurposeRef.of(purposeId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("x-metadata-version");

        verify(restClient, never()).signRiskAnalysis(any(), any());
    }

    @Test
    void propagatesSigningReadFailuresWithoutRepeatingTheSignature() {
        UUID purposeId = UUID.randomUUID();
        TestChain<Void> chain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        TestChain<Purpose> readChain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.signRiskAnalysis(eq(purposeId), any())).thenReturn(chain);
        when(restClient.getPurpose(purposeId)).thenReturn(readChain);

        assertThatThrownBy(() -> gateway.signRiskAnalysis(PurposeRef.of(purposeId), 2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Risk-analysis signing read failed");
        verify(restClient).signRiskAnalysis(eq(purposeId), any());
        verify(restClient).getPurpose(purposeId);
    }

    @Test
    void rejectsRiskAnalysisWithRequestedReason() {
        UUID purposeId = UUID.randomUUID();
        String rejectionReason = "Risk analysis rejected by contract test";
        TestChain<Void> chain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.rejectRiskAnalysis(eq(purposeId), any())).thenReturn(chain);

        gateway.rejectRiskAnalysis(PurposeRef.of(purposeId), rejectionReason);

        ArgumentCaptor<RiskAnalysisRejectionSeed> request = ArgumentCaptor.forClass(RiskAnalysisRejectionSeed.class);
        verify(restClient).rejectRiskAnalysis(eq(purposeId), request.capture());
        assertThat(request.getValue().getRejectionReason()).isEqualTo(rejectionReason);
    }

    private static it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode toBffReviewMode(
            RiskAnalysisReviewMode reviewMode
    ) {
        return switch (reviewMode) {
            case ADMIN_WRITES_ADMIN_SIGNS ->
                    it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode.ADMIN_WRITES_ADMIN_SIGNS;
            case ADMIN_WRITES_REVIEWER_SIGNS ->
                    it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode.ADMIN_WRITES_REVIEWER_SIGNS;
            case REVIEWER_WRITES_REVIEWER_SIGNS ->
                    it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisReviewMode.REVIEWER_WRITES_REVIEWER_SIGNS;
        };
    }
}
