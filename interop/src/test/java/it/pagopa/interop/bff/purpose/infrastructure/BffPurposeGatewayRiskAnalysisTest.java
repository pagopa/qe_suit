package it.pagopa.interop.bff.purpose.infrastructure;

import it.pagopa.infrastructure.template.action.TestChain;
import it.pagopa.interop.common.kernel.domain.PurposeRef;
import it.pagopa.interop.common.risk_analysis.domain.RiskAnalysisReviewMode;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisAssignmentSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisRejectionSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSignSeed;
import it.pagopa.interop.generated.openapi.clients.bff.model.RiskAnalysisSubmissionSeed;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @ParameterizedTest
    @EnumSource(RiskAnalysisReviewMode.class)
    void assignsRiskAnalysisReviewersWithRequestedModeAndIds(RiskAnalysisReviewMode reviewMode) {
        UUID purposeId = UUID.randomUUID();
        List<UUID> reviewerIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        TestChain<Void> chain = mock(TestChain.class, RETURNS_DEEP_STUBS);
        when(restClient.assignRiskAnalysisReviewers(eq(purposeId), any())).thenReturn(chain);

        gateway.assignRiskAnalysisReviewers(PurposeRef.of(purposeId), reviewMode, reviewerIds);

        ArgumentCaptor<RiskAnalysisAssignmentSeed> request =
                ArgumentCaptor.forClass(RiskAnalysisAssignmentSeed.class);
        verify(restClient).assignRiskAnalysisReviewers(eq(purposeId), request.capture());
        assertThat(request.getValue().getReviewerIds()).containsExactlyElementsOf(reviewerIds);
        assertThat(request.getValue().getReviewMode()).isEqualTo(toBffReviewMode(reviewMode));
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

        gateway.signRiskAnalysis(PurposeRef.of(purposeId), 0);

        ArgumentCaptor<RiskAnalysisSignSeed> request = ArgumentCaptor.forClass(RiskAnalysisSignSeed.class);
        verify(restClient).signRiskAnalysis(eq(purposeId), request.capture());
        assertThat(request.getValue().getMetadataVersionToSign()).isZero();
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
