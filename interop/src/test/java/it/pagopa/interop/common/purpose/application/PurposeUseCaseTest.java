package it.pagopa.interop.common.purpose.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptorState;
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

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1, 1_000_000_000, Integer.MAX_VALUE})
    void rejectsMissingOrUnexceedableThresholds(Integer threshold) {
        var invalidService = eService.toBuilder()
                .descriptors(List.of(published.toBuilder().dailyCallsPerConsumer(threshold).build())).build();

        assertThatThrownBy(() -> useCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, invalidService))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("Cannot exceed consumer daily calls threshold");
        verifyNoInteractions(gateway);
    }

    @Test
    void acceptsTheLargestExceedableThresholdWithoutOverflow() {
        var service = eService.toBuilder().descriptors(List.of(
                published.toBuilder().dailyCallsPerConsumer(999_999_999).build())).build();
        Purpose expected = purpose.toBuilder()
                .version(waiting.toBuilder().dailyCalls(1_000_000_000).build()).build();
        when(gateway.updatePurposeDailyCalls(purpose.getRef(), 1_000_000_000)).thenReturn(expected);

        assertThat(useCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, service)).isSameAs(expected);
    }

    @Test
    void requiresANewWaitingVersionRatherThanAnAutomaticallyActivatedOne() {
        when(gateway.updatePurposeDailyCalls(purpose.getRef(), 51))
                .thenReturn(purpose.toBuilder().version(waiting.toBuilder()
                        .purposeVersionState(PurposeVersionState.ACTIVE).build()).build());

        assertThatThrownBy(() -> useCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, eService))
                .isInstanceOf(AssertionError.class).hasMessageContaining("waiting for approval");
    }

    @Test
    void rejectsAnIncorrectDailyCallsEstimate() {
        when(gateway.updatePurposeDailyCalls(purpose.getRef(), 51))
                .thenReturn(purpose.toBuilder().version(waiting.toBuilder().dailyCalls(52).build()).build());

        assertThatThrownBy(() -> useCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, eService))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void rejectsReplacingTheActiveVersionWithTheWaitingVersion() {
        when(gateway.updatePurposeDailyCalls(purpose.getRef(), 51))
                .thenReturn(purpose.toBuilder().clearVersions()
                        .version(waiting.toBuilder().id(active.getId()).build()).build());

        assertThatThrownBy(() -> useCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, eService))
                .isInstanceOf(AssertionError.class).hasMessageContaining("must be new");
    }

    @Test
    void rejectsChangingThePreviousActiveVersion() {
        when(gateway.updatePurposeDailyCalls(purpose.getRef(), 51))
                .thenReturn(purpose.toBuilder().clearVersions().version(waiting)
                        .version(active.toBuilder().purposeVersionState(PurposeVersionState.ARCHIVED).build()).build());

        assertThatThrownBy(() -> useCase.updatePurposeDailyCallsEstimateBeyondThreshold(purpose, eService))
                .isInstanceOf(AssertionError.class);
    }
}
