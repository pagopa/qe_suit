package it.pagopa.interop.common.eservice.application;

import it.pagopa.interop.common.eservice.domain.EService;
import it.pagopa.interop.common.eservice.domain.EServiceDescriptor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static it.pagopa.interop.common.eservice.domain.EServiceDescriptorState.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EServiceDescriptorRejectionUseCaseTest {
    private final EServiceDescriptorGateway gateway = mock(EServiceDescriptorGateway.class);
    private final EServiceDescriptorUseCase useCase =
            new EServiceDescriptorUseCase(gateway, mock(EServiceRequestFactory.class));
    private final EService service = EService.builder().id(UUID.randomUUID()).build();
    private final EServiceDescriptor pending =
            EServiceDescriptor.builder().id(UUID.randomUUID()).state(WAITING_FOR_APPROVAL).build();

    @Test
    void rejectsPendingVersionAndReturnsTheSynchronizedDraft() {
        var draft = pending.toBuilder().state(DRAFT).build();
        when(gateway.rejectDescriptor(service.getRef(), pending.getRef(), "Cleanup")).thenReturn(draft);

        assertThat(useCase.rejectDescriptor(service, pending, "Cleanup")).isSameAs(draft);
        verify(gateway).rejectDescriptor(service.getRef(), pending.getRef(), "Cleanup");
    }

    @Test
    void rejectsInvalidStatesAndMissingReasonsBeforeCallingTheGateway() {
        assertThatThrownBy(() -> useCase.rejectDescriptor(service, pending.toBuilder().state(PUBLISHED).build(), "Cleanup"))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> useCase.rejectDescriptor(service, pending, " "))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.rejectDescriptor(service, pending, null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(gateway);
    }
}
