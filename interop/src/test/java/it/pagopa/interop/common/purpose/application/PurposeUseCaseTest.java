package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.kernel.domain.EServiceRef;
import it.pagopa.interop.common.purpose.domain.Purpose;
import it.pagopa.interop.common.purpose.domain.PurposeVersion;
import it.pagopa.interop.common.purpose.domain.PurposeVersionState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurposeUseCaseTest {
    private final PurposeGateway gateway = mock(PurposeGateway.class);
    private final PurposeUseCase useCase = new PurposeUseCase(gateway, mock(PurposeCommandFactory.class));
    private final EServiceDescriptor published = EServiceDescriptor.builder().id(UUID.randomUUID())
            .version("1").state(EServiceDescriptorState.PUBLISHED).publishedAt(Instant.EPOCH)
            .dailyCallsPerConsumer(50).build();
    private final EService eService = EService.builder().id(UUID.randomUUID())
            .descriptors(List.of(published, published.toBuilder().id(UUID.randomUUID())
                    .version("2").state(EServiceDescriptorState.DRAFT).dailyCallsPerConsumer(100).build())).build();
    private final PurposeVersion active = PurposeVersion.builder().id(UUID.randomUUID())
            .createdAt(Instant.EPOCH).purposeVersionState(PurposeVersionState.ACTIVE).dailyCalls(10).build();
    private final Purpose purpose = Purpose.builder().id(UUID.randomUUID()).eserviceId(eService.getId())
            .version(active).build();
    private final PurposeVersion waiting = active.toBuilder().id(UUID.randomUUID())
            .purposeVersionState(PurposeVersionState.WAITING_FOR_APPROVAL).dailyCalls(51).build();

    @Test
    void exceedsThePublishedConsumerThresholdByExactlyOneAndPreservesTheActiveVersion() {
        Purpose expected = purpose.toBuilder().version(waiting).build();
        when(gateway.updatePurposeDailyCalls(purpose.getRef(), 51)).thenReturn(expected);

        assertThat(useCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, eService)).isSameAs(expected);

        verify(gateway).updatePurposeDailyCalls(purpose.getRef(), 51);
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void verifiesConflictingMetadataUpdateThroughTheGateway() {
        useCase.shouldFailToUpdatePurposeMetadataAsConflict();

        verify(gateway).shouldFailToUpdatePurposeMetadataAsConflict();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void updatesOrdinaryPurposeMetadata() {
        Purpose updated = purpose.toBuilder().title("new title").description("new description").build();
        when(gateway.getPurpose(purpose.getRef())).thenReturn(updated);

        assertThat(useCase.updatePurposeMetadata(purpose, "new title", "new description", null, null, null))
                .isSameAs(updated);

        verify(gateway).updatePurposeMetadata(purpose.getRef(), "new title", "new description", null, null, null);
        verify(gateway).getPurpose(purpose.getRef());
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void waitsForAllRequestedMetadataFields() {
        PurposeVersion draft = active.toBuilder()
                .purposeVersionState(PurposeVersionState.DRAFT)
                .dailyCalls(500)
                .build();
        Purpose updated = purpose.toBuilder()
                .clearVersions()
                .title("new title")
                .description("new description")
                .isFreeOfCharge(true)
                .freeOfChargeReason("new reason")
                .version(draft)
                .build();
        Purpose staleFreeOfCharge = updated.toBuilder().isFreeOfCharge(false).build();
        Purpose staleFreeOfChargeReason = updated.toBuilder().freeOfChargeReason("old reason").build();
        Purpose staleDailyCalls = updated.toBuilder()
                .clearVersions()
                .version(draft.toBuilder().dailyCalls(10).build())
                .build();
        when(gateway.getPurpose(purpose.getRef()))
                .thenReturn(staleFreeOfCharge, staleFreeOfChargeReason, staleDailyCalls, updated);

        assertThat(useCase.updatePurposeMetadata(
                purpose, "new title", "new description", true, "new reason", 500))
                .isSameAs(updated);

        verify(gateway).updatePurposeMetadata(purpose.getRef(), "new title", "new description", true, "new reason", 500);
        verify(gateway, times(4)).getPurpose(purpose.getRef());
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void updatesReversePurposeMetadata() {
        Purpose updated = purpose.toBuilder().title("new title").description("new description").build();
        when(gateway.getPurpose(purpose.getRef())).thenReturn(updated);

        assertThat(useCase.updateReversePurposeMetadata(purpose, "new title", "new description", null, null, null))
                .isSameAs(updated);

        verify(gateway).updateReversePurposeMetadata(
                purpose.getRef(), "new title", "new description", null, null, null);
        verify(gateway).getPurpose(purpose.getRef());
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void requiresAnActiveVersion() {
        assertThatThrownBy(() -> useCase.updatePurposeDailyCallsEstimateBeyondThreshold(
                purpose.toBuilder().clearVersions().build(), eService))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("No active version");
        verifyNoInteractions(gateway);
    }

    @Test
    void rejectsAnExistingVersionWaitingForApproval() {
        assertThatThrownBy(() -> useCase.updatePurposeDailyCallsEstimateBeyondThreshold(
                purpose.toBuilder().version(waiting).build(), eService))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("already has a version waiting");
        verifyNoInteractions(gateway);
    }

    @Test
    void rejectsAnUnrelatedEService() {
        assertThatThrownBy(() -> useCase.updatePurposeDailyCallsEstimateBeyondThreshold(
                purpose, eService.toBuilder().id(UUID.randomUUID()).build()))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("does not belong");
        verifyNoInteractions(gateway);
    }

    @Test
    void archivesTheRequestedPurposeVersion() {
        Purpose archived = purpose.toBuilder()
                .version(active.toBuilder().purposeVersionState(PurposeVersionState.ARCHIVED).build())
                .build();
        when(gateway.archivePurpose(purpose.getRef(), active.getRef())).thenReturn(archived);

        assertThat(useCase.archivePurpose(purpose, active)).isSameAs(archived);

        verify(gateway).archivePurpose(purpose.getRef(), active.getRef());
        verifyNoMoreInteractions(gateway);
    }

    @Test
    void attemptsCloningForTheSelectedPurposeAndItsOwnEService() {
        var commands = mock(PurposeCommandFactory.class);
        var command = mock(PurposeCloneCommand.class);
        var eServiceRef = EServiceRef.of(purpose.getEserviceId());
        when(commands.cloneCommand(eServiceRef)).thenReturn(command);

        new PurposeUseCase(gateway, commands).attemptPurposeCloning(purpose);

        verify(commands).cloneCommand(eServiceRef);
        verify(gateway).attemptPurposeCloning(purpose.getRef(), command);
        verifyNoMoreInteractions(gateway, commands);
    }

    @Test
    void attemptsSigningWithTheKnownMetadataVersionWithoutUsingThePositiveOperation() {
        var commands = mock(PurposeCommandFactory.class);
        var command = mock(SignRiskAnalysisCommand.class);
        var current = purpose.toBuilder().metadataVersion(7).build();
        when(commands.signRiskAnalysisCommand(7)).thenReturn(command);

        new PurposeUseCase(gateway, commands).attemptRiskAnalysisSigning(current);

        verify(commands).signRiskAnalysisCommand(7);
        verify(gateway).attemptRiskAnalysisSigning(current.getRef(), command);
        verifyNoMoreInteractions(gateway, commands);
    }

    @Test
    void refreshesMissingMetadataBeforeAttemptingSigning() {
        var commands = mock(PurposeCommandFactory.class);
        var command = mock(SignRiskAnalysisCommand.class);
        when(gateway.getPurpose(purpose.getRef())).thenReturn(purpose.toBuilder().metadataVersion(8).build());
        when(commands.signRiskAnalysisCommand(8)).thenReturn(command);

        new PurposeUseCase(gateway, commands).attemptRiskAnalysisSigning(purpose);

        var order = inOrder(gateway, commands);
        order.verify(gateway).getPurpose(purpose.getRef());
        order.verify(commands).signRiskAnalysisCommand(8);
        order.verify(gateway).attemptRiskAnalysisSigning(purpose.getRef(), command);
        verifyNoMoreInteractions(gateway, commands);
    }

    @Test
    void failsBeforeSigningIfMetadataRemainsUnavailable() {
        var commands = mock(PurposeCommandFactory.class);
        when(gateway.getPurpose(purpose.getRef())).thenReturn(purpose);

        assertThatThrownBy(() -> new PurposeUseCase(gateway, commands).attemptRiskAnalysisSigning(purpose))
            .isInstanceOf(IllegalStateException.class).hasMessageContaining("Missing metadataVersion");

        verify(gateway).getPurpose(purpose.getRef());
        verifyNoMoreInteractions(gateway);
        verifyNoInteractions(commands);
    }

    @Test
    void positiveSigningStillUsesThePositiveGatewayWithRefreshedMetadata() {
        var commands = mock(PurposeCommandFactory.class);
        var command = mock(SignRiskAnalysisCommand.class);
        when(gateway.getPurpose(purpose.getRef())).thenReturn(purpose.toBuilder().metadataVersion(9).build());
        when(commands.signRiskAnalysisCommand(9)).thenReturn(command);
        when(gateway.signRiskAnalysis(purpose.getRef(), command)).thenReturn(purpose);

        assertThat(new PurposeUseCase(gateway, commands).signRiskAnalysis(purpose)).isSameAs(purpose);

        verify(gateway).getPurpose(purpose.getRef());
        verify(gateway).signRiskAnalysis(purpose.getRef(), command);
        verifyNoMoreInteractions(gateway);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Informazioni insufficienti", "Motivazione https://example.org"})
    void attemptsRejectionWithTheExactReasonWithoutUsingThePositiveOperation(String reason) {
        var commands = mock(PurposeCommandFactory.class);
        var command = mock(RejectRiskAnalysisCommand.class);
        when(commands.rejectRiskAnalysisCommand(reason)).thenReturn(command);

        new PurposeUseCase(gateway, commands).attemptRiskAnalysisRejection(purpose, reason);

        verify(commands).rejectRiskAnalysisCommand(reason);
        verify(gateway).attemptRiskAnalysisRejection(purpose.getRef(), command);
        verifyNoMoreInteractions(gateway, commands);
    }
}
